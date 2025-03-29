package org.rsmod.content.skills.magic.spell.attacks.standard

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.manager.MagicRuneManager.Companion.consumedRune
import org.rsmod.api.combat.manager.MagicRuneManager.Companion.isFailure
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.projanims
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.spells.attack.SpellAttack
import org.rsmod.api.spells.attack.SpellAttackManager
import org.rsmod.api.spells.attack.SpellAttackMap
import org.rsmod.api.spells.attack.SpellAttackRepository
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.synth.SynthType

class ElementalSpells : SpellAttackMap {
    override fun SpellAttackRepository.register(manager: SpellAttackManager) {
        registerStrikes(manager)
    }

    private fun SpellAttackRepository.registerStrikes(manager: SpellAttackManager) {
        fun getMaxHit(magicLvl: Int): Int =
            when {
                magicLvl >= 13 -> 8
                magicLvl >= 9 -> 6
                magicLvl >= 5 -> 4
                else -> 2
            }

        register(
            spell = objs.spell_wind_strike,
            attack =
                ElementalSpell(
                    manager = manager,
                    castAnim = seqs.human_caststrike,
                    launch = spotanims.wind_strike_launch,
                    travel = spotanims.wind_strike_travel,
                    impact = spotanims.wind_strike_hit,
                    castSound = synths.windstrike_cast_and_fire,
                    hitSound = synths.windstrike_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_water_strike,
            attack =
                ElementalSpell(
                    manager = manager,
                    castAnim = seqs.human_caststrike,
                    launch = spotanims.water_strike_launch,
                    travel = spotanims.water_strike_travel,
                    impact = spotanims.water_strike_hit,
                    castSound = synths.waterstrike_cast_and_fire,
                    hitSound = synths.waterstrike_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_earth_strike,
            attack =
                ElementalSpell(
                    manager = manager,
                    castAnim = seqs.human_caststrike,
                    launch = spotanims.earth_strike_launch,
                    travel = spotanims.earth_strike_travel,
                    impact = spotanims.earth_strike_hit,
                    castSound = synths.earthstrike_cast_and_fire,
                    hitSound = synths.earthstrike_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_fire_strike,
            attack =
                ElementalSpell(
                    manager = manager,
                    castAnim = seqs.human_caststrike,
                    launch = spotanims.fire_strike_launch,
                    travel = spotanims.fire_strike_travel,
                    impact = spotanims.fire_strike_hit,
                    castSound = synths.firestrike_cast_and_fire,
                    hitSound = synths.firestrike_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )
    }

    private class ElementalSpell(
        private val manager: SpellAttackManager,
        private val castAnim: SeqType,
        private val launch: SpotanimType,
        private val travel: SpotanimType,
        private val impact: SpotanimType,
        private val castSound: SynthType,
        private val hitSound: SynthType,
        private val getMaxHit: (Int) -> Int,
    ) : SpellAttack {
        override suspend fun ProtectedAccess.attack(target: Npc, attack: CombatAttack.Spell) {
            cast(target, attack)
        }

        override suspend fun ProtectedAccess.attack(target: Player, attack: CombatAttack.Spell) {
            cast(target, attack)
        }

        private fun ProtectedAccess.cast(target: PathingEntity, attack: CombatAttack.Spell) {
            val castResult = manager.attemptCast(this, attack)
            if (castResult.isFailure()) {
                return
            }
            manager.giveCastXp(this, attack)

            val baseMaxHit = getMaxHit(player.magicLvl)

            val proj = manager.spawnProjectile(this, target, travel, projanims.magic_spell)
            val (serverDelay, clientDelay) = proj.durations

            anim(castAnim)
            spotanim(launch, height = 92)

            val splash = false // TODO(combat): Accuracy roll.
            val spell = attack.spell.obj

            if (splash) {
                manager.playSplashFx(this, target, clientDelay, castSound, soundRadius = 8)
                manager.queueSplashHit(this, target, spell, clientDelay, serverDelay)
                manager.continueCombatIfAutocast(this, target)
                return
            }

            val damage = baseMaxHit // TODO(combat): Max hit roll.
            val sunfire = castResult.consumedRune() && castResult.usedSunfire

            manager.playHitFx(
                source = this,
                target = target,
                clientDelay = clientDelay,
                castSound = castSound,
                soundRadius = 8,
                hitSpot = impact,
                hitSpotHeight = 124,
                hitSound = hitSound,
            )
            manager.giveCombatXp(this, target, attack, damage)
            manager.queueMagicHit(this, target, spell, damage, clientDelay, serverDelay)
            manager.continueCombatIfAutocast(this, target)
        }
    }
}
