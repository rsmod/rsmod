package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.fx.MeleeAnimationAndSound
import org.rsmod.api.combat.commons.npc.combatPlayDefendAnim
import org.rsmod.api.combat.commons.npc.queueCombatRetaliate
import org.rsmod.api.combat.commons.ranged.RangedAmmunition
import org.rsmod.api.combat.formulas.AccuracyFormulae
import org.rsmod.api.combat.formulas.MaxHitFormulae
import org.rsmod.api.combat.player.canPerformMeleeSpecial
import org.rsmod.api.combat.player.canPerformRangedSpecial
import org.rsmod.api.combat.player.canPerformShieldSpecial
import org.rsmod.api.combat.player.specialAttackType
import org.rsmod.api.combat.weapon.WeaponSpeeds
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.params
import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.queueHit
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
import org.rsmod.game.hit.HitType
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
    private val hitModifier: NpcHitModifier,
    private val accuracy: AccuracyFormulae,
    private val maxHits: MaxHitFormulae,
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

        if (actionDelay > mapClock) {
            opNpc2(npc)
            return
        }

        // Set the next attack clock before executing the special attack, ensuring specials default
        // to the weapon's standard attack delay.
        val attackRate = speeds.actual(player)
        actionDelay = mapClock + attackRate

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

        val (weapon, type, style, stance) = attack

        val animAndSound = MeleeAnimationAndSound.from(stance)
        val (animParam, soundParam, defaultAnim, defaultSound) = animAndSound

        val attackAnim = ocParamOrNull(weapon, animParam) ?: defaultAnim
        val attackSound = ocParamOrNull(weapon, soundParam) ?: defaultSound

        anim(attackAnim)
        soundSynth(attackSound)

        val successfulHit =
            accuracy.rollMeleeAccuracy(
                player = player,
                target = npc,
                attackType = type,
                attackStyle = style,
                blockType = type,
                specMultiplier = 1.0,
                random = random,
            )

        val damage =
            if (successfulHit) {
                val maxHit = maxHits.getMeleeMaxHit(player, npc, type, style, specMultiplier = 1.0)
                random.of(0..maxHit)
            } else {
                0
            }

        val hit = npc.queueHit(player, 1, HitType.Melee, damage, hitModifier)
        npc.heroPoints(player, hit.damage)
        npc.combatPlayDefendAnim()
        npc.queueCombatRetaliate(player)

        // TODO(combat): This is sending two `setmapflag(null)` packets when it is meant to only
        //  send one. This is due to the `consumeRoute` and `routeTo` in player movement processor.
        //  Will have to review that processor soon to get it to match rs.
        opNpc2(npc)
    }

    private suspend fun ProtectedAccess.attackRanged(npc: Npc, attack: CombatAttack.Ranged) {
        if (!canAttack(npc)) {
            return
        }

        if (actionDelay > mapClock) {
            opNpc2(npc)
            return
        }

        // Set the next attack clock before executing the special attack, ensuring specials default
        // to the weapon's standard attack delay.
        val attackRate = speeds.actual(player)
        actionDelay = mapClock + attackRate

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

        val (weapon, type, style) = attack
        val weaponType = objTypes[weapon]

        val usingChargeBow = weaponType.isCategoryType(categories.chargebow)
        // TODO(combat): Handle weapon attacks and also enforce all chargebows to have one.

        val quiver = player.quiver
        val quiverType = quiver?.let(objTypes::get)

        val canUseAmmo = RangedAmmunition.attemptAmmoUsage(player, weaponType, quiverType)
        if (!canUseAmmo) {
            // Reset the previously assigned action delay as the attack cannot be performed.
            actionDelay = mapClock
            return
        }

        // TODO(combat): Projectiles and proper impact delay calc.
        val clientDelay = 46 + (5 * player.distanceTo(npc.coords))
        val delay = 1 + (clientDelay / 30)

        val usingThrown = weaponType.isCategoryType(categories.throwing_weapon)
        val removeAmmoType = if (usingThrown) weaponType else quiverType
        if (removeAmmoType != null) {
            val conserve = RangedAmmunition.conserveAmmo(player, objTypes, random)
            val removeWearpos = if (usingThrown) Wearpos.RightHand else Wearpos.Quiver

            if (!conserve) {
                RangedAmmunition.detractAmmo(
                    player = player,
                    wearpos = removeWearpos,
                    wornType = removeAmmoType,
                    detract = 1,
                    eventBus = eventBus,
                )
            }

            if (!conserve && random.randomBoolean(RangedAmmunition.DEFAULT_AMMO_DROP_RATE)) {
                RangedAmmunition.attemptAmmoDrop(
                    player = player,
                    delay = delay,
                    ammoType = removeAmmoType,
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

        val attackAnim = ocParamOrNull(weapon, params.attack_anim_stance1)
        val attackSound = ocParamOrNull(weapon, params.attack_sound_stance1)

        if (attackAnim == null) {
            mes("The bow appears to be broken.")
            return
        }

        anim(attackAnim)
        attackSound?.let(::soundSynth)

        val successfulHit = true // TODO(combat): Ranged accuracy

        val damage =
            if (successfulHit) {
                val maxHit = maxHits.getRangedMaxHit(player, npc, type, style, specMultiplier = 1.0)
                random.of(0..maxHit)
            } else {
                0
            }

        // TODO(combat): Verify all timings with rs.
        val hit = npc.queueHit(player, delay, HitType.Ranged, damage, hitModifier)
        npc.heroPoints(player, hit.damage)
        npc.combatPlayDefendAnim(clientDelay)
        npc.queueCombatRetaliate(player, delay)

        if (usingThrown && player.righthand == null) {
            mes("That was your last one!")
            return
        }

        opNpc2(npc)
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
