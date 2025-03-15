package org.rsmod.api.combat.formulas.accuracy.melee

import com.google.inject.Inject
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.config.refs.npcs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.back
import org.rsmod.api.player.feet
import org.rsmod.api.player.front
import org.rsmod.api.player.hands
import org.rsmod.api.player.hat
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.legs
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ring
import org.rsmod.api.player.torso
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam
import org.rsmod.game.entity.Npc
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.util.ParamMap
import org.rsmod.game.type.util.ParamMapBuilder
import org.rsmod.game.type.varbit.VarBitType

class PvNMeleeAccuracyTest {
    @TestWithArgs(MatchupProvider::class)
    fun `calculate matchup hit chance`(matchup: Matchup, state: GameTestState) =
        state.runInjectedGameTest(MeleeAccuracyTestDependencies::class) {
            val accuracy = it.accuracy

            player.setCurrentLevel(stats.attack, matchup.attackLvl)
            player.setBaseLevel(stats.attack, matchup.baseAttackLvl)
            player.setCurrentLevel(stats.hitpoints, matchup.hitpoints)
            player.setBaseLevel(stats.hitpoints, matchup.baseHitpointsLvl)

            player.hat = matchup.hat
            player.back = matchup.back
            player.front = matchup.front
            player.righthand = matchup.righthand
            player.torso = matchup.torso
            player.lefthand = matchup.lefthand
            player.legs = matchup.legs
            player.hands = matchup.hands
            player.feet = matchup.feet
            player.ring = matchup.ring

            for (prayer in matchup.prayers) {
                player.setVarBit(prayer, 1)
            }

            val npc = Npc(matchup.npc)
            npc.hitpoints = matchup.npcCurrHp
            npc.baseHitpointsLvl = matchup.npcMaxHp

            val accuracyRoll =
                accuracy.getHitChance(
                    player = player,
                    target = npc,
                    attackType = matchup.attackType,
                    attackStyle = matchup.attackStyle,
                    blockType = matchup.blockType ?: matchup.attackType,
                    specialMultiplier = matchup.specMultiplier,
                )
            assertEquals(matchup.expectedAccuracy, accuracyRoll / 100.0)
        }

    data class Matchup(
        val expectedAccuracy: Double,
        val npc: UnpackedNpcType = man,
        val npcCurrHp: Int = 1,
        val npcMaxHp: Int = 1,
        val blockType: MeleeAttackType? = null,
        val hat: InvObj? = null,
        val back: InvObj? = null,
        val front: InvObj? = null,
        val righthand: InvObj? = null,
        val torso: InvObj? = null,
        val lefthand: InvObj? = null,
        val legs: InvObj? = null,
        val hands: InvObj? = null,
        val feet: InvObj? = null,
        val ring: InvObj? = null,
        val attackLvl: Int = 99,
        val baseAttackLvl: Int = 99,
        val hitpoints: Int = 99,
        val baseHitpointsLvl: Int = 99,
        val prayers: Set<VarBitType> = emptySet(),
        val attackType: MeleeAttackType? = null,
        val attackStyle: MeleeAttackStyle? = null,
        val specMultiplier: Double = 1.0,
    ) {
        fun withNpcTarget(npc: UnpackedNpcType) = copy(npc = npc)

        fun withNpcHp(currHp: Int, maxHp: Int) = copy(npcCurrHp = currHp, npcMaxHp = maxHp)

        fun withBlockType(blockType: MeleeAttackType?) = copy(blockType = blockType)

        fun withHelm(obj: ObjType?) = copy(hat = obj?.let(::InvObj))

        fun withCape(obj: ObjType?) = copy(back = obj?.let(::InvObj))

        fun withAmulet(obj: ObjType?) = copy(front = obj?.let(::InvObj))

        fun withWeapon(obj: ObjType?) = copy(righthand = obj?.let(::InvObj))

        fun withBody(obj: ObjType?) = copy(torso = obj?.let(::InvObj))

        fun withShield(obj: ObjType?) = copy(lefthand = obj?.let(::InvObj))

        fun withLegs(obj: ObjType?) = copy(legs = obj?.let(::InvObj))

        fun withGloves(obj: ObjType?) = copy(hands = obj?.let(::InvObj))

        fun withFeet(obj: ObjType?) = copy(feet = obj?.let(::InvObj))

        fun withRing(obj: ObjType?) = copy(ring = obj?.let(::InvObj))

        fun withPrayers(vararg prayers: VarBitType) = copy(prayers = prayers.toSet())

        fun withAttackType(attackType: MeleeAttackType?) = copy(attackType = attackType)

        fun withAttackStyle(attackStyle: MeleeAttackStyle?) = copy(attackStyle = attackStyle)

        fun withSpecMultiplier(specMultiplier: Double) = copy(specMultiplier = specMultiplier)

        override fun toString(): String =
            "Matchup(" +
                "expectedAccuracy=$expectedAccuracy, " +
                "npc=${NpcStatFormat()}, " +
                "player=${PlayerStatFormat()}" +
                ")"

        private inner class NpcStatFormat {
            override fun toString(): String =
                "Npc(" +
                    "name=${npc.name}, " +
                    "hitpoints=$npcCurrHp / $npcMaxHp, " +
                    "blockType=${blockType ?: attackType}" +
                    ")"
        }

        private inner class PlayerStatFormat {
            override fun toString(): String {
                return "Player(" +
                    "attackType=$attackType, " +
                    "attackStyle=$attackStyle, " +
                    "specMultiplier=$specMultiplier, " +
                    "attackLevel=$attackLvl / $baseAttackLvl, " +
                    "hitpoints=$hitpoints / $baseHitpointsLvl, " +
                    "prayers=${concatenatePrayers()}, " +
                    concatenateWorn() +
                    ")"
            }

            private fun concatenateWorn(): String {
                val filteredWorn =
                    listOfNotNull(
                        hat?.let { "helm=${it.id}" },
                        back?.let { "cape=${it.id}" },
                        front?.let { "amulet=${it.id}" },
                        righthand?.let { "weapon=${it.id}" },
                        torso?.let { "chest=${it.id}" },
                        lefthand?.let { "shield=${it.id}" },
                        legs?.let { "legs=${it.id}" },
                        hands?.let { "gloves=${it.id}" },
                        feet?.let { "feet=${it.id}" },
                        ring?.let { "ring=${it.id}" },
                    )
                return filteredWorn.joinToString(", ")
            }

            private fun concatenatePrayers(): String =
                if (prayers.isEmpty()) {
                    "None"
                } else {
                    prayers.joinToString(transform = VarBitType::internalNameValue)
                }
        }
    }

    // Each matchup runs a full integrated test scope, meaning it initializes an entire game
    // environment. Avoid excessive matchups to prevent unnecessary overhead.
    private object MatchupProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                Matchup(expectedAccuracy = 96.93)
                    .withAttackType(MeleeAttackType.Crush)
                    .withAttackStyle(MeleeAttackStyle.Accurate)
                    .withNpcTarget(man),
                Matchup(expectedAccuracy = 46.57)
                    .withAttackType(MeleeAttackType.Stab)
                    .withAttackStyle(MeleeAttackStyle.Accurate)
                    .withHelm(objs.torva_full_helm)
                    .withBody(objs.torva_platebody)
                    .withLegs(objs.torva_platelegs)
                    .withCape(objs.infernal_cape)
                    .withAmulet(objs.amulet_of_rancour)
                    .withWeapon(objs.ghrazi_rapier)
                    .withGloves(objs.ferocious_gloves)
                    .withFeet(objs.primordial_boots)
                    .withRing(objs.ultor_ring)
                    .withPrayers(varbits.piety)
                    .withNpcTarget(corporealBeast),
                Matchup(expectedAccuracy = 92.86)
                    .withAttackType(MeleeAttackType.Stab)
                    .withAttackStyle(MeleeAttackStyle.Aggressive)
                    .withHelm(objs.torva_full_helm)
                    .withBody(objs.torva_platebody)
                    .withLegs(objs.torva_platelegs)
                    .withCape(objs.infernal_cape)
                    .withAmulet(objs.amulet_of_rancour)
                    .withWeapon(objs.osmumtens_fang)
                    .withGloves(objs.ferocious_gloves)
                    .withFeet(objs.primordial_boots)
                    .withRing(objs.ultor_ring)
                    .withPrayers(varbits.chivalry)
                    .withNpcTarget(abyssalDemon),
                Matchup(expectedAccuracy = 96.83)
                    .withAttackType(MeleeAttackType.Stab)
                    .withAttackStyle(MeleeAttackStyle.Aggressive)
                    .withHelm(objs.torva_full_helm)
                    .withBody(objs.torva_platebody)
                    .withLegs(objs.torva_platelegs)
                    .withCape(objs.infernal_cape)
                    .withAmulet(objs.amulet_of_rancour)
                    .withWeapon(objs.osmumtens_fang)
                    .withGloves(objs.ferocious_gloves)
                    .withFeet(objs.primordial_boots)
                    .withRing(objs.ultor_ring)
                    .withPrayers(varbits.chivalry)
                    .withSpecMultiplier(1.5)
                    .withNpcTarget(abyssalDemon),
            )
        }
    }

    private class MeleeAccuracyTestDependencies @Inject constructor(val accuracy: PvNMeleeAccuracy)

    private companion object {
        val man =
            npcTypeFactory.create {
                name = "Man"
                hitpoints = 7
                paramMap = buildParams {
                    this[params.defence_stab] = -21
                    this[params.defence_slash] = -21
                    this[params.defence_crush] = -21
                }
            }

        val corporealBeast =
            npcTypeFactory.create(npcs.corporeal_beast.id) {
                name = "Corporeal Beast"
                size = 5
                hitpoints = 2000
                defence = 310
                paramMap = buildParams {
                    this[params.defence_stab] = 25
                    this[params.defence_slash] = 200
                    this[params.defence_crush] = 100
                }
            }

        val abyssalDemon =
            npcTypeFactory.create {
                name = "Abyssal demon"
                hitpoints = 300
                defence = 135
                paramMap = buildParams {
                    this[params.defence_stab] = 20
                    this[params.defence_slash] = 20
                    this[params.defence_crush] = 20
                }
            }

        private fun buildParams(init: ParamMapBuilder.() -> Unit): ParamMap {
            return ParamMapBuilder().apply(init).toParamMap()
        }
    }
}
