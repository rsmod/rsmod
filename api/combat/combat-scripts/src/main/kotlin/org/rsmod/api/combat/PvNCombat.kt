package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.manager.PlayerAttackManager
import org.rsmod.api.combat.manager.RangedAmmoManager
import org.rsmod.api.combat.player.canPerformMeleeSpecial
import org.rsmod.api.combat.player.canPerformRangedSpecial
import org.rsmod.api.combat.player.canPerformShieldSpecial
import org.rsmod.api.combat.player.specialAttackType
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.config.refs.categories
import org.rsmod.api.npc.isValidTarget
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.quiver
import org.rsmod.api.player.righthand
import org.rsmod.api.specials.SpecialAttackRegistry
import org.rsmod.api.specials.SpecialAttackType
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.weapons.WeaponRegistry
import org.rsmod.api.weapons.attack
import org.rsmod.game.entity.Npc
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.type.obj.ObjTypeList

internal class PvNCombat
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val speeds: WeaponSpeeds,
    private val specialsReg: SpecialAttackRegistry,
    private val specialEnergy: SpecialAttackEnergy,
    private val weaponsReg: WeaponRegistry,
    private val manager: PlayerAttackManager,
    private val ammunition: RangedAmmoManager,
) {
    suspend fun attack(access: ProtectedAccess, target: Npc, attack: CombatAttack.PlayerAttack) {
        when (attack) {
            is CombatAttack.Melee -> access.attackMelee(target, attack)
            is CombatAttack.Ranged -> access.attackRanged(target, attack)
            is CombatAttack.Spell -> access.attackMagicSpell(target, attack)
            is CombatAttack.Staff -> access.attackMagicStaff(target, attack)
        }
    }

    private suspend fun ProtectedAccess.attackMelee(npc: Npc, attack: CombatAttack.Melee) {
        if (!canAttack(npc)) {
            return
        }

        if (manager.isAttackDelayed(player)) {
            manager.continueCombat(player, npc)
            return
        }

        // Set the next attack clock before executing any special attack, ensuring specials default
        // to the weapon's standard attack delay. (When applicable)
        val attackRate = speeds.actual(player)
        manager.setNextAttackDelay(player, attackRate)

        // Important: Special attack handlers are responsible for explicitly calling `opnpc2` (or a
        // helper function that does so) to re-engage combat after performing the special attack.
        if (specialAttackType == SpecialAttackType.Weapon) {
            specialAttackType = SpecialAttackType.None
            val activatedSpec = canPerformMeleeSpecial(npc, attack, specialsReg, specialEnergy)
            if (activatedSpec) {
                return
            }
        }

        if (specialAttackType == SpecialAttackType.Shield) {
            specialAttackType = SpecialAttackType.None
            val activatedSpec = canPerformShieldSpecial(npc, player.lefthand, specialsReg)
            if (activatedSpec) {
                return
            }
        }

        // Important: Weapon attack handlers are responsible for explicitly calling `opnpc2` (or a
        // helper function that does so) to re-engage combat after performing their attack.
        val specializedWeapon = weaponsReg.getMelee(attack.weapon)
        if (specializedWeapon != null) {
            val attackHandled = specializedWeapon.attack(this, npc, attack)
            if (attackHandled) {
                return
            }
        }

        manager.playWeaponFx(player, attack)

        val damage = manager.rollMeleeDamage(player, npc, attack)
        manager.queueMeleeHit(player, npc, damage)

        // TODO(combat): This is sending two `setmapflag(null)` packets when it is meant to only
        //  send one. This is due to the `consumeRoute` and `routeTo` in player movement processor.
        //  Will have to review that processor soon to get it to match rs.
        manager.continueCombat(player, npc)
    }

    private suspend fun ProtectedAccess.attackRanged(npc: Npc, attack: CombatAttack.Ranged) {
        if (!canAttack(npc)) {
            return
        }

        if (manager.isAttackDelayed(player)) {
            manager.continueCombat(player, npc)
            return
        }

        // Set the next attack clock before executing any special attack, ensuring specials default
        // to the weapon's standard attack delay. (When applicable)
        val attackRate = speeds.actual(player)
        manager.setNextAttackDelay(player, attackRate)

        // Important: Special attack handlers are responsible for explicitly calling `opnpc2` (or a
        // helper function that does so) to re-engage combat after performing the special attack.
        if (specialAttackType == SpecialAttackType.Weapon) {
            specialAttackType = SpecialAttackType.None
            val activatedSpec = canPerformRangedSpecial(npc, attack, specialsReg, specialEnergy)
            if (activatedSpec) {
                return
            }
        }

        if (specialAttackType == SpecialAttackType.Shield) {
            specialAttackType = SpecialAttackType.None
            val activatedSpec = canPerformShieldSpecial(npc, player.lefthand, specialsReg)
            if (activatedSpec) {
                return
            }
        }

        val weaponType = objTypes[attack.weapon]

        // Important: Weapon attack handlers are responsible for explicitly calling `opnpc2` (or a
        // helper function that does so) to re-engage combat after performing their attack.
        val specializedWeapon = weaponsReg.getRanged(attack.weapon)
        if (specializedWeapon != null) {
            val attackHandled = specializedWeapon.attack(this, npc, attack)
            if (attackHandled) {
                return
            }
        }

        // `chargebows` are specialized and not worth trying to have as a generic system. As such,
        // they are required to be registered in `WeaponRegistry` and will return early if they
        // have reached this point (not handled by previous `specializedWeapon` block).
        val usingChargeBow = weaponType.isCategoryType(categories.chargebow)
        if (usingChargeBow) {
            manager.resetAttackDelay(player)
            mes("The bow appears to have malfunctioned.")
            return
        }

        val quiver = player.quiver
        val quiverType = objTypes.getOrNull(quiver)

        val canUseAmmo = ammunition.attemptAmmoUsage(player, weaponType, quiverType)
        if (!canUseAmmo) {
            manager.resetAttackDelay(player)
            return
        }

        val playedAnim = manager.playWeaponFx(player, attack)
        if (!playedAnim) {
            manager.resetAttackDelay(player)
            mes("The bow appears to be broken.")
            return
        }

        // Note: Some weapons are categorized as `throwing_weapon` but do not behave like standard
        // throwing weapons. For example, the Toxic blowpipe falls under this category but requires
        // special handling. Such weapons should be managed via the `Weapon` system to ensure
        // correct behavior and avoid unintended side effects.
        val usingThrown = weaponType.isCategoryType(categories.throwing_weapon)

        // TODO(combat): Projectiles and proper impact delay calc.
        val distance = player.distanceTo(npc)
        // delay + progress + (stepMultiplier * distance)
        val clientDelay = 41 + 5 + (5 * distance)
        val hitDelay = 1 + (clientDelay / 30)

        if (usingThrown) {
            ammunition.useThrownWeapon(player, weaponType, npc.coords, dropDelay = hitDelay)
        } else if (quiverType != null) {
            ammunition.useQuiverAmmo(player, quiverType, npc.coords, dropDelay = hitDelay)
        }

        val damage = manager.rollRangedDamage(player, npc, attack)

        val hitAmmoObj = if (usingThrown) null else quiverType
        manager.queueRangedHit(player, npc, hitAmmoObj, damage, clientDelay, hitDelay)

        if (usingThrown && player.righthand == null) {
            mes("That was your last one!")
            return
        }

        manager.continueCombat(player, npc)
    }

    private suspend fun ProtectedAccess.attackMagicSpell(target: Npc, attack: CombatAttack.Spell) {
        TODO()
    }

    private suspend fun ProtectedAccess.attackMagicStaff(target: Npc, attack: CombatAttack.Staff) {
        TODO()
    }

    private fun ProtectedAccess.canAttack(npc: Npc): Boolean {
        if (!npc.isValidTarget()) {
            return false
        }

        val hasAttackOp = npc.visType.hasOp(InteractionOp.Op2)
        if (!hasAttackOp) {
            mes("You can't attack this npc.")
            return false
        }

        return true
    }
}
