package org.runerealm.content.other.special.attacks.melee

import kotlin.math.max
import kotlin.math.min
import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.stats
import org.runerealm.api.freeze.freeze
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.stat.statSub
import org.runerealm.api.specials.SpecialAttackManager
import org.runerealm.api.specials.SpecialAttackMap
import org.runerealm.api.specials.SpecialAttackRepository
import org.runerealm.api.specials.combat.MeleeSpecialAttack
import org.runerealm.content.other.special.attacks.configs.special_objs
import org.runerealm.content.other.special.attacks.configs.special_seqs
import org.runerealm.content.other.special.attacks.configs.special_spots
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player
import org.runerealm.game.type.seq.SeqType
import org.runerealm.game.type.spot.SpotanimType
import org.runerealm.game.type.stat.StatType

/**
 * God wars godsword special attacks. Every godsword special doubles accuracy, always rolls against
 * the target's slash defence, and multiplies the max hit by `1.1`. On top of that:
 * - Armadyl godsword "The Judgement": the max hit is further multiplied by `1.25` (rounded down
 *   after each multiplier).
 * - Bandos godsword "Warstrike": the max hit is further multiplied by `1.1` (rounded down after
 *   each multiplier). Damage dealt drains the target's Defence, then Strength, Prayer (players
 *   only), Attack, Magic and Ranged, carrying any excess over to the next stat.
 * - Saradomin godsword "Healing Blade": heals the user by 50% of the damage dealt (minimum 10) and
 *   restores Prayer by 25% of the damage dealt (minimum 5).
 * - Zamorak godsword "Ice Cleave": no extra damage. A successful hit freezes the target for 32
 *   cycles (19.2 seconds), subject to the usual freeze immunity rules.
 */
class GodswordSpecialAttacks : SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        val ags = special_spots.armadyl_godsword
        registerMelee(
            special_objs.armadyl_godsword,
            Godsword(manager, special_seqs.armadyl_godsword, ags, Effect.Judgement),
        )
        registerMelee(
            special_objs.armadyl_godsword_or,
            Godsword(manager, special_seqs.armadyl_godsword_or, ags, Effect.Judgement),
        )

        val bgs = special_spots.bandos_godsword
        registerMelee(
            special_objs.bandos_godsword,
            Godsword(manager, special_seqs.bandos_godsword, bgs, Effect.Warstrike),
        )
        registerMelee(
            special_objs.bandos_godsword_or,
            Godsword(manager, special_seqs.bandos_godsword_or, bgs, Effect.Warstrike),
        )

        val sgs = special_spots.saradomin_godsword
        registerMelee(
            special_objs.saradomin_godsword,
            Godsword(manager, special_seqs.saradomin_godsword, sgs, Effect.HealingBlade),
        )
        registerMelee(
            special_objs.saradomin_godsword_or,
            Godsword(manager, special_seqs.saradomin_godsword_or, sgs, Effect.HealingBlade),
        )

        val zgs = special_spots.zamorak_godsword
        registerMelee(
            special_objs.zamorak_godsword,
            Godsword(manager, special_seqs.zamorak_godsword, zgs, Effect.IceCleave),
        )
        registerMelee(
            special_objs.zamorak_godsword_or,
            Godsword(manager, special_seqs.zamorak_godsword_or, zgs, Effect.IceCleave),
        )
    }

    private enum class Effect(val secondaryMaxHitMultiplier: Double) {
        Judgement(1.25),
        Warstrike(1.1),
        HealingBlade(1.0),
        IceCleave(1.0),
    }

    private class Godsword(
        private val manager: SpecialAttackManager,
        private val seq: SeqType,
        private val spot: SpotanimType,
        private val effect: Effect,
    ) : MeleeSpecialAttack {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Melee,
        ): Boolean {
            val damage = godswordHit(target, attack)
            if (effect == Effect.Warstrike) {
                warstrike(target, damage)
            }
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Melee,
        ): Boolean {
            val damage = godswordHit(target, attack)
            if (effect == Effect.Warstrike) {
                warstrike(target, damage)
            }
            return true
        }

        private fun ProtectedAccess.godswordHit(
            target: PathingEntity,
            attack: CombatAttack.Melee,
        ): Int {
            anim(seq)
            spotanim(spot = spot, slot = constants.spotanim_slot_combat, height = 96)

            val damage = rollDamage(target, attack)
            manager.giveCombatXp(this, target, attack, damage)
            manager.queueMeleeHit(this, target, damage)

            if (effect == Effect.HealingBlade) {
                healingBlade(damage)
            } else if (effect == Effect.IceCleave) {
                iceCleave(target, damage)
            }

            manager.continueCombat(this, target)
            return damage
        }

        private fun ProtectedAccess.rollDamage(
            target: PathingEntity,
            attack: CombatAttack.Melee,
        ): Int {
            val accurate =
                manager.rollMeleeAccuracy(
                    source = this,
                    target = target,
                    attackType = attack.type,
                    attackStyle = attack.style,
                    blockType = MeleeAttackType.Slash,
                    multiplier = ACCURACY_MULTIPLIER,
                )
            if (!accurate) {
                return 0
            }
            val baseMaxHit =
                manager.calculateMeleeMaxHit(
                    source = this,
                    target = target,
                    attackType = attack.type,
                    attackStyle = attack.style,
                    multiplier = GODSWORD_MAX_HIT_MULTIPLIER,
                )
            val maxHit = (baseMaxHit * effect.secondaryMaxHitMultiplier).toInt()
            if (maxHit <= 0) {
                return 0
            }
            return random.of(1..maxHit)
        }

        private fun ProtectedAccess.healingBlade(damage: Int) {
            val heal = max(MIN_HITPOINTS_HEAL, damage / 2)
            val restore = max(MIN_PRAYER_RESTORE, damage / 4)
            statHeal(stats.hitpoints, constant = heal, percent = 0)
            statHeal(stats.prayer, constant = restore, percent = 0)
        }

        private fun ProtectedAccess.iceCleave(target: PathingEntity, damage: Int) {
            // Only a successful (accurate) hit freezes the target; accurate hits deal at least 1.
            if (damage <= 0) {
                return
            }
            target.spotanim(special_spots.ice_barrage_impact)
            target.freeze(ICE_CLEAVE_FREEZE_CYCLES, random, source = player)
        }

        private fun warstrike(target: Npc, damage: Int) {
            var remaining = damage
            if (remaining <= 0) {
                return
            }
            val defenceDrain = min(remaining, target.defenceLvl)
            target.defenceLvl -= defenceDrain
            remaining -= defenceDrain

            val strengthDrain = min(remaining, target.strengthLvl)
            target.strengthLvl -= strengthDrain
            remaining -= strengthDrain

            val attackDrain = min(remaining, target.attackLvl)
            target.attackLvl -= attackDrain
            remaining -= attackDrain

            val magicDrain = min(remaining, target.magicLvl)
            target.magicLvl -= magicDrain
            remaining -= magicDrain

            val rangedDrain = min(remaining, target.rangedLvl)
            target.rangedLvl -= rangedDrain
        }

        private fun warstrike(target: Player, damage: Int) {
            var remaining = damage
            for (stat in PLAYER_WARSTRIKE_ORDER) {
                if (remaining <= 0) {
                    return
                }
                val drain = min(remaining, target.stat(stat))
                if (drain > 0) {
                    target.statSub(stat, constant = drain, percent = 0)
                }
                remaining -= drain
            }
        }
    }

    private companion object {
        private const val ACCURACY_MULTIPLIER = 2.0
        private const val GODSWORD_MAX_HIT_MULTIPLIER = 1.1
        private const val MIN_HITPOINTS_HEAL = 10
        private const val MIN_PRAYER_RESTORE = 5
        private const val ICE_CLEAVE_FREEZE_CYCLES = 32

        private val PLAYER_WARSTRIKE_ORDER: List<StatType> by lazy {
            listOf(
                stats.defence,
                stats.strength,
                stats.prayer,
                stats.attack,
                stats.magic,
                stats.ranged,
            )
        }
    }
}
