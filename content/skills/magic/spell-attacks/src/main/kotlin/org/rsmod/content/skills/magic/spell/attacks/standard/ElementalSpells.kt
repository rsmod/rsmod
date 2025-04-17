package org.rsmod.content.skills.magic.spell.attacks.standard

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.manager.MagicRuneManager.Companion.isFailure
import org.rsmod.api.config.refs.categories
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
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.synth.SynthType

class ElementalSpells @Inject constructor(private val objTypes: ObjTypeList) : SpellAttackMap {
    override fun SpellAttackRepository.register(manager: SpellAttackManager) {
        registerStrikes(manager)
        registerBolts(manager)
        registerBlasts(manager)
        registerWaves(manager)
        registerSurges(manager)
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
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.windstrike_casting,
                    travel = spotanims.windstrike_travel,
                    impact = spotanims.windstrike_impact,
                    castSound = synths.windstrike_cast_and_fire,
                    hitSound = synths.windstrike_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_water_strike,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.waterstrike_casting,
                    travel = spotanims.waterstrike_travel,
                    impact = spotanims.waterstrike_impact,
                    castSound = synths.waterstrike_cast_and_fire,
                    hitSound = synths.waterstrike_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_earth_strike,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.earthstrike_casting,
                    travel = spotanims.earthstrike_travel,
                    impact = spotanims.earthstrike_impact,
                    castSound = synths.earthstrike_cast_and_fire,
                    hitSound = synths.earthstrike_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_fire_strike,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.firestrike_casting,
                    travel = spotanims.firestrike_travel,
                    impact = spotanims.firestrike_impact,
                    castSound = synths.firestrike_cast_and_fire,
                    hitSound = synths.firestrike_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )
    }

    private fun SpellAttackRepository.registerBolts(manager: SpellAttackManager) {
        fun getMaxHit(magicLvl: Int): Int =
            when {
                magicLvl >= 35 -> 12
                magicLvl >= 29 -> 11
                magicLvl >= 23 -> 10
                else -> 9
            }

        register(
            spell = objs.spell_wind_bolt,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.windbolt_casting,
                    travel = spotanims.windbolt_travel,
                    impact = spotanims.windbolt_impact,
                    castSound = synths.windbolt_cast_and_fire,
                    hitSound = synths.windbolt_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_water_bolt,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.waterbolt_casting,
                    travel = spotanims.waterbolt_travel,
                    impact = spotanims.waterbolt_impact,
                    castSound = synths.waterbolt_cast_and_fire,
                    hitSound = synths.waterbolt_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_earth_bolt,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.earthbolt_casting,
                    travel = spotanims.earthbolt_travel,
                    impact = spotanims.earthbolt_impact,
                    castSound = synths.earthbolt_cast_and_fire,
                    hitSound = synths.earthbolt_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_fire_bolt,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.firebolt_casting,
                    travel = spotanims.firebolt_travel,
                    impact = spotanims.firebolt_impact,
                    castSound = synths.firebolt_cast_and_fire,
                    hitSound = synths.firebolt_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )
    }

    private fun SpellAttackRepository.registerBlasts(manager: SpellAttackManager) {
        fun getMaxHit(magicLvl: Int): Int =
            when {
                magicLvl >= 59 -> 16
                magicLvl >= 53 -> 15
                magicLvl >= 47 -> 14
                else -> 13
            }

        register(
            spell = objs.spell_wind_blast,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.windblast_casting,
                    travel = spotanims.windblast_travel,
                    impact = spotanims.windblast_impact,
                    castSound = synths.windblast_cast_and_fire,
                    hitSound = synths.windblast_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_water_blast,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.waterblast_casting,
                    travel = spotanims.waterblast_travel,
                    impact = spotanims.waterblast_impact,
                    castSound = synths.waterblast_cast_and_fire,
                    hitSound = synths.waterblast_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_earth_blast,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.earthblast_casting,
                    travel = spotanims.earthblast_travel,
                    impact = spotanims.earthblast_impact,
                    castSound = synths.earthblast_cast_and_fire,
                    hitSound = synths.earthblast_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_fire_blast,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_caststrike_staff,
                    unarmedAnim = seqs.human_caststrike,
                    launch = spotanims.fireblast_casting,
                    travel = spotanims.fireblast_travel,
                    impact = spotanims.fireblast_impact,
                    castSound = synths.fireblast_cast_and_fire,
                    hitSound = synths.fireblast_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )
    }

    private fun SpellAttackRepository.registerWaves(manager: SpellAttackManager) {
        fun getMaxHit(magicLvl: Int): Int =
            when {
                magicLvl >= 75 -> 20
                magicLvl >= 70 -> 19
                magicLvl >= 65 -> 18
                else -> 17
            }

        register(
            spell = objs.spell_wind_wave,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_castwave_staff,
                    unarmedAnim = seqs.human_castwave,
                    launch = spotanims.windwave_casting,
                    travel = spotanims.windwave_travel,
                    impact = spotanims.windwave_impact,
                    castSound = synths.windwave_cast_and_fire,
                    hitSound = synths.windwave_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_water_wave,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_castwave_staff,
                    unarmedAnim = seqs.human_castwave,
                    launch = spotanims.waterwave_casting,
                    travel = spotanims.waterwave_travel,
                    impact = spotanims.waterwave_impact,
                    castSound = synths.waterwave_cast_and_fire,
                    hitSound = synths.waterwave_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_earth_wave,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_castwave_staff,
                    unarmedAnim = seqs.human_castwave,
                    launch = spotanims.earthwave_casting,
                    travel = spotanims.earthwave_travel,
                    impact = spotanims.earthwave_impact,
                    castSound = synths.earthwave_cast_and_fire,
                    hitSound = synths.earthwave_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_fire_wave,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_castwave_staff,
                    unarmedAnim = seqs.human_castwave,
                    launch = spotanims.firewave_casting,
                    travel = spotanims.firewave_travel,
                    impact = spotanims.firewave_impact,
                    castSound = synths.firewave_cast_and_fire,
                    hitSound = synths.firewave_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )
    }

    private fun SpellAttackRepository.registerSurges(manager: SpellAttackManager) {
        fun getMaxHit(magicLvl: Int): Int =
            when {
                magicLvl >= 95 -> 24
                magicLvl >= 90 -> 23
                magicLvl >= 85 -> 22
                else -> 21
            }

        register(
            spell = objs.spell_wind_surge,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_cast_surge,
                    unarmedAnim = seqs.human_cast_surge,
                    launch = spotanims.windsurge_casting,
                    travel = spotanims.windsurge_travel,
                    impact = spotanims.windsurge_impact,
                    castSound = synths.windsurge_cast_and_fire,
                    hitSound = synths.windsurge_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_water_surge,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_cast_surge,
                    unarmedAnim = seqs.human_cast_surge,
                    launch = spotanims.watersurge_casting,
                    travel = spotanims.watersurge_travel,
                    impact = spotanims.watersurge_impact,
                    castSound = synths.watersurge_cast_and_fire,
                    hitSound = synths.watersurge_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_earth_surge,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_cast_surge,
                    unarmedAnim = seqs.human_cast_surge,
                    launch = spotanims.earthsurge_casting,
                    travel = spotanims.earthsurge_travel,
                    impact = spotanims.earthsurge_impact,
                    castSound = synths.earthsurge_cast_and_fire,
                    hitSound = synths.earthsurge_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )

        register(
            spell = objs.spell_fire_surge,
            attack =
                ElementalSpellAttack(
                    objTypes = objTypes,
                    manager = manager,
                    staffAnim = seqs.human_cast_surge,
                    unarmedAnim = seqs.human_cast_surge,
                    launch = spotanims.firesurge_casting,
                    travel = spotanims.firesurge_travel,
                    impact = spotanims.firesurge_impact,
                    castSound = synths.firesurge_cast_and_fire,
                    hitSound = synths.firesurge_hit,
                    getMaxHit = ::getMaxHit,
                ),
        )
    }

    private class ElementalSpellAttack(
        private val manager: SpellAttackManager,
        private val objTypes: ObjTypeList,
        private val staffAnim: SeqType,
        private val unarmedAnim: SeqType,
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
            val weaponType = objTypes.getOrNull(attack.weapon)
            val castAnim = weaponType.castStrikeAnim()

            anim(castAnim)
            spotanim(launch, height = 92)

            val proj = manager.spawnProjectile(this, target, travel, projanims.magic_spell)
            val (serverDelay, clientDelay) = proj.durations
            val spell = attack.spell.obj

            val splash = manager.rollSplash(this, target, attack, castResult)
            if (splash) {
                manager.playSplashFx(this, target, clientDelay, castSound, soundRadius = 8)
                manager.queueSplashHit(this, target, spell, clientDelay, serverDelay)
                manager.continueCombatIfAutocast(this, target)
                return
            }

            val baseMaxHit = getMaxHit(player.magicLvl)
            val damage = manager.rollMaxHit(this, target, attack, castResult, baseMaxHit)
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

        private fun UnpackedObjType?.castStrikeAnim(): SeqType =
            if (this != null && isCategoryType(categories.staff)) {
                staffAnim
            } else {
                unarmedAnim
            }
    }
}
