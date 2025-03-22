package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.fx.MeleeAnimationAndSound
import org.rsmod.api.combat.commons.ranged.RangedAmmunition
import org.rsmod.api.combat.manager.PlayerAttackManager
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
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.specials.SpecialAttackRegistry
import org.rsmod.api.specials.SpecialAttackType
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.queue.WorldQueueList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.routefinder.collision.CollisionFlagMap

internal class PvNCombat
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val collision: CollisionFlagMap,
    private val objRepo: ObjRepository,
    private val worldQueues: WorldQueueList,
    private val speeds: WeaponSpeeds,
    private val specialsReg: SpecialAttackRegistry,
    private val specialEnergy: SpecialAttackEnergy,
    private val manager: PlayerAttackManager,
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

        // TODO(combat): "WeaponAttack" handling for specialized weapon attacks, such as Scythe of
        //  Vitur attacks which follows specific logic when performing a standard attack.

        val animAndSound = MeleeAnimationAndSound.from(attack.stance)
        val (animParam, soundParam, defaultAnim, defaultSound) = animAndSound

        val attackAnim = ocParamOrNull(attack.weapon, animParam) ?: defaultAnim
        val attackSound = ocParamOrNull(attack.weapon, soundParam) ?: defaultSound

        anim(attackAnim)
        soundSynth(attackSound)

        val damage = manager.rollMeleeDamage(player, npc, attack)
        manager.queueMeleeHit(player, npc, damage, delay = 1)

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

        val usingChargeBow = weaponType.isCategoryType(categories.chargebow)
        // TODO(combat): Handle weapon attacks and also enforce all chargebows to have one.

        val quiver = player.quiver
        val quiverType = quiver?.let(objTypes::get)

        val canUseAmmo = RangedAmmunition.attemptAmmoUsage(player, weaponType, quiverType)
        if (!canUseAmmo) {
            // Reset the previously assigned action delay as the attack cannot be performed.
            manager.resetAttackDelay(player)
            return
        }

        val attackAnim = ocParamOrNull(attack.weapon, params.attack_anim_stance1)
        val attackSound = ocParamOrNull(attack.weapon, params.attack_sound_stance1)

        if (attackAnim == null) {
            mes("The bow appears to be broken.")
            return
        }

        anim(attackAnim)
        attackSound?.let(::soundSynth)

        // Note: Some weapons are categorized as `throwing_weapon` but do not behave like standard
        // throwing weapons. For example, the Toxic blowpipe falls under this category but requires
        // special handling. Such weapons should be managed via the `WeaponAttack` system to ensure
        // correct behavior and avoid unintended side effects.
        val usingThrown = weaponType.isCategoryType(categories.throwing_weapon)
        val ammoType = if (usingThrown) weaponType else quiverType

        // TODO(combat): Projectiles and proper impact delay calc.
        val distance = player.coords.chebyshevDistance(npc.coords)
        // delay + lengthAdjustment + (stepMultiplier * distance)
        val clientDelay = 41 + 5 + (5 * distance)
        val hitDelay = 1 + (clientDelay / 30)

        if (ammoType != null) {
            val conserve = RangedAmmunition.conserveAmmo(player, objTypes, random)
            val removeWearpos = if (usingThrown) Wearpos.RightHand else Wearpos.Quiver

            if (!conserve) {
                RangedAmmunition.detractAmmo(
                    player = player,
                    wearpos = removeWearpos,
                    wornType = ammoType,
                    detract = 1,
                    eventBus = eventBus,
                )
            }

            if (!conserve && random.randomBoolean(RangedAmmunition.DEFAULT_AMMO_DROP_RATE)) {
                RangedAmmunition.attemptAmmoDrop(
                    player = player,
                    delay = hitDelay,
                    ammoType = ammoType,
                    ammoCount = 1,
                    dropCoord = npc.coords,
                    // TODO(combat): Verify duration. Do they stay longer in raids?
                    dropDuration = constants.ammodrop_duration,
                    collision = collision,
                    worldQueues = worldQueues,
                    objRepo = objRepo,
                )
            }
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
