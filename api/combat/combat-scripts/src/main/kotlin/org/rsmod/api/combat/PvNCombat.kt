package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.manager.PlayerAttackManager
import org.rsmod.api.combat.manager.RangedAmmoManager
import org.rsmod.api.combat.player.canPerformMagicSpecial
import org.rsmod.api.combat.player.canPerformMeleeSpecial
import org.rsmod.api.combat.player.canPerformRangedSpecial
import org.rsmod.api.combat.player.canPerformShieldSpecial
import org.rsmod.api.combat.player.specialAttackType
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.params
import org.rsmod.api.npc.isValidTarget
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.quiver
import org.rsmod.api.player.righthand
import org.rsmod.api.specials.SpecialAttackRegistry
import org.rsmod.api.specials.SpecialAttackType
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.spells.attack.SpellAttackRegistry
import org.rsmod.api.spells.attack.attack
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
    private val spellsReg: SpellAttackRegistry,
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

        // Set the next attack clock before executing any special attack, ensuring all attacks
        // default to the weapon's standard attack delay.
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

        val damage = manager.rollMeleeDamage(player, npc, attack)
        manager.giveCombatXp(player, npc, attack, damage)
        manager.playWeaponFx(player, attack)
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

        // Set the next attack clock before executing any special attack, ensuring all attacks
        // default to the weapon's standard attack delay.
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

        val righthandType = objTypes[attack.weapon]

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
        val usingChargeBow = righthandType.isCategoryType(categories.chargebow)
        if (usingChargeBow) {
            manager.clearCombat(player)
            mes("The bow refuses to fire.")
            return
        }

        val quiver = player.quiver
        val quiverType = objTypes.getOrNull(quiver)

        val canUseAmmo = ammunition.attemptAmmoUsage(player, righthandType, quiverType)
        if (!canUseAmmo) {
            manager.clearCombat(player)
            return
        }

        // Note: Some weapons are categorized as `throwing_weapon` but do not behave like standard
        // throwing weapons. For example, the Toxic blowpipe falls under this category but requires
        // special handling. Such weapons should be managed via the `Weapon` system to ensure
        // correct behavior and avoid unintended side effects.
        val usingThrown = righthandType.isCategoryType(categories.throwing_weapon)

        val weaponType = if (usingThrown) righthandType else quiverType
        checkNotNull(weaponType) {
            "Unexpected null weapon type: righthand=$righthandType, quiver=$quiverType"
        }

        val projanimType = righthandType.paramOrNull(params.proj_type)
        val travelSpotanim = weaponType.paramOrNull(params.proj_travel)

        // All valid ammunition requires a `proj_travel` spotanim type and `proj_type` projanim type
        // param so that the projectile can be created and referenced for its proper delays.
        if (projanimType == null || travelSpotanim == null) {
            manager.clearCombat(player)
            mes("You are unable to fire your ammunition.")
            return
        }

        // All valid ranged weapons require a `attack_anim_stance1` seq type param in order to be
        // used in combat.
        val playedAnim = manager.playWeaponFx(player, attack)
        if (!playedAnim) {
            manager.clearCombat(player)
            mes("The bow fails to fire.")
            return
        }

        // Official behavior: If the weapon (quiver or righthand, based on thrown weapon flag) has
        // no `proj_launch` param, a "null" (-1) spotanim will still be sent in the same slot and
        // height as usual.
        val launchSpotanim = weaponType.paramOrNull(params.proj_launch)
        spotanim(launchSpotanim, height = 96, slot = constants.spotanim_slot_combat)

        val projanim = manager.spawnProjectile(player, npc, travelSpotanim, projanimType)
        val (serverDelay, clientDelay) = projanim.durations

        if (usingThrown) {
            ammunition.useThrownWeapon(player, righthandType, npc.coords, dropDelay = serverDelay)
        } else if (quiverType != null) {
            ammunition.useQuiverAmmo(player, quiverType, npc.coords, dropDelay = serverDelay)
        }

        val damage = manager.rollRangedDamage(player, npc, attack)
        manager.giveCombatXp(player, npc, attack, damage)

        val hitAmmoObj = if (usingThrown) null else quiverType
        manager.queueRangedHit(player, npc, hitAmmoObj, damage, clientDelay, serverDelay)

        if (usingThrown && player.righthand == null) {
            mes("That was your last one!")
            return
        }

        manager.continueCombat(player, npc)
    }

    private suspend fun ProtectedAccess.attackMagicSpell(npc: Npc, attack: CombatAttack.Spell) {
        if (!canAttack(npc)) {
            return
        }

        if (manager.isAttackDelayed(player)) {
            manager.continueCombat(player, npc, attack.spell)
            return
        }

        val attackRate = MAGIC_SPELL_ATTACK_RATE
        manager.setNextAttackDelay(player, attackRate)

        val spell = spellsReg[attack.spell.obj]
        if (spell != null) {
            spell.attack(this, npc, attack)
            return
        }

        // All magic spell attacks must be registered in `SpellAttackRegistry`.
        manager.clearCombat(player)
        mes("You attempt to cast the spell, but nothing happens.")
    }

    private suspend fun ProtectedAccess.attackMagicStaff(npc: Npc, attack: CombatAttack.Staff) {
        if (!canAttack(npc)) {
            return
        }

        if (manager.isAttackDelayed(player)) {
            manager.continueCombat(player, npc)
            return
        }

        // Set the next attack clock before executing any special attack, ensuring all attacks
        // default to the weapon's standard attack delay.
        val attackRate = MAGIC_STAFF_ATTACK_RATE
        manager.setNextAttackDelay(player, attackRate)

        // Important: Special attack handlers are responsible for explicitly calling `opnpc2` (or a
        // helper function that does so) to re-engage combat after performing the special attack.
        if (specialAttackType == SpecialAttackType.Weapon) {
            specialAttackType = SpecialAttackType.None
            val activatedSpec = canPerformMagicSpecial(npc, attack, specialsReg, specialEnergy)
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
        val specializedWeapon = weaponsReg.getMagic(attack.weapon)
        if (specializedWeapon != null) {
            val attackHandled = specializedWeapon.attack(this, npc, attack)
            if (attackHandled) {
                return
            }
        }

        // Since most (if not all) powered staves require specialized attack handling logic, they
        // are expected to be registered as separate weapons in the `WeaponRegistry`. This ensures
        // their unique behavior is handled explicitly rather than relying on fallback logic.
        manager.clearCombat(player)
        mes("Your staff fails to respond.")
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
