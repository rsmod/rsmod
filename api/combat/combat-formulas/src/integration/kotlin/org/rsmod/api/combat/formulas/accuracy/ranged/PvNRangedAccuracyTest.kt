package org.rsmod.api.combat.formulas.accuracy.ranged

import com.google.inject.Inject
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.test_npcs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.back
import org.rsmod.api.player.feet
import org.rsmod.api.player.front
import org.rsmod.api.player.hands
import org.rsmod.api.player.hat
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.legs
import org.rsmod.api.player.quiver
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ring
import org.rsmod.api.player.torso
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam
import org.rsmod.game.entity.Npc
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.varbit.VarBitType

class PvNRangedAccuracyTest {
    @TestWithArgs(MatchupProvider::class)
    fun `calculate matchup hit chance`(matchup: Matchup, state: GameTestState) =
        state.runInjectedGameTest(RangedAccuracyTestDependencies::class) {
            val accuracy = it.accuracy

            player.setCurrentLevel(stats.ranged, matchup.rangedLvl)
            player.setBaseLevel(stats.ranged, matchup.baseRangedLvl)
            player.setCurrentLevel(stats.hitpoints, matchup.hitpoints)
            player.setBaseLevel(stats.hitpoints, matchup.baseHitpointsLvl)

            player.hat = matchup.hat
            player.back = matchup.back
            player.front = matchup.front
            player.quiver = matchup.quiver
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
        val npc: UnpackedNpcType = test_npcs.man,
        val npcCurrHp: Int = 1,
        val npcMaxHp: Int = 1,
        val blockType: RangedAttackType? = null,
        val hat: InvObj? = null,
        val back: InvObj? = null,
        val front: InvObj? = null,
        val quiver: InvObj? = null,
        val righthand: InvObj? = null,
        val torso: InvObj? = null,
        val lefthand: InvObj? = null,
        val legs: InvObj? = null,
        val hands: InvObj? = null,
        val feet: InvObj? = null,
        val ring: InvObj? = null,
        val rangedLvl: Int = 99,
        val baseRangedLvl: Int = 99,
        val hitpoints: Int = 99,
        val baseHitpointsLvl: Int = 99,
        val prayers: Set<VarBitType> = emptySet(),
        val attackType: RangedAttackType? = null,
        val attackStyle: RangedAttackStyle? = null,
        val specMultiplier: Double = 1.0,
    ) {
        fun withNpcTarget(npc: UnpackedNpcType) = copy(npc = npc)

        fun withHelm(obj: ObjType?) = copy(hat = obj?.let(::InvObj))

        fun withCape(obj: ObjType?) = copy(back = obj?.let(::InvObj))

        fun withAmulet(obj: ObjType?) = copy(front = obj?.let(::InvObj))

        fun withAmmo(obj: ObjType?) = copy(quiver = obj?.let(::InvObj))

        fun withWeapon(obj: ObjType?) = copy(righthand = obj?.let(::InvObj))

        fun withBody(obj: ObjType?) = copy(torso = obj?.let(::InvObj))

        fun withShield(obj: ObjType?) = copy(lefthand = obj?.let(::InvObj))

        fun withLegs(obj: ObjType?) = copy(legs = obj?.let(::InvObj))

        fun withGloves(obj: ObjType?) = copy(hands = obj?.let(::InvObj))

        fun withFeet(obj: ObjType?) = copy(feet = obj?.let(::InvObj))

        fun withRing(obj: ObjType?) = copy(ring = obj?.let(::InvObj))

        fun withPrayers(vararg prayers: VarBitType) = copy(prayers = prayers.toSet())

        fun withAttackType(attackType: RangedAttackType?) = copy(attackType = attackType)

        fun withAttackStyle(attackStyle: RangedAttackStyle?) = copy(attackStyle = attackStyle)

        fun withRangingPotion(): Matchup {
            val add = 4 + (baseRangedLvl * 0.1).toInt()
            return copy(rangedLvl = baseRangedLvl + add)
        }

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
            override fun toString(): String =
                "Player(" +
                    "attackType=$attackType, " +
                    "attackStyle=$attackStyle, " +
                    "specMultiplier=$specMultiplier, " +
                    "rangedLevel=$rangedLvl / $baseRangedLvl, " +
                    "hitpoints=$hitpoints / $baseHitpointsLvl, " +
                    "prayers=${concatenatePrayers()}, " +
                    "worn=[${concatenateWorn()}]" +
                    ")"

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
                Matchup(expectedAccuracy = 97.20)
                    .withAttackType(RangedAttackType.Standard)
                    .withAttackStyle(RangedAttackStyle.Rapid)
                    .withWeapon(objs.shortbow)
                    .withAmmo(objs.bronze_arrow)
                    .withNpcTarget(test_npcs.man),
                Matchup(expectedAccuracy = 19.87)
                    .withAttackType(RangedAttackType.Standard)
                    .withAttackStyle(RangedAttackStyle.Rapid)
                    .withHelm(objs.armadyl_helmet)
                    .withBody(objs.armadyl_chestplate)
                    .withLegs(objs.armadyl_chainskirt)
                    .withCape(objs.avas_assembler)
                    .withAmulet(objs.amulet_of_fury)
                    .withAmmo(objs.dragon_arrow)
                    .withWeapon(objs.twisted_bow)
                    .withGloves(objs.barrows_gloves)
                    .withFeet(objs.pegasian_boots)
                    .withRing(objs.archers_ring_i)
                    .withNpcTarget(test_npcs.corporeal_beast),
                Matchup(expectedAccuracy = 31.20)
                    .withAttackType(RangedAttackType.Standard)
                    .withAttackStyle(RangedAttackStyle.Rapid)
                    .withHelm(objs.armadyl_helmet)
                    .withBody(objs.armadyl_chestplate)
                    .withLegs(objs.armadyl_chainskirt)
                    .withCape(objs.avas_assembler)
                    .withAmulet(objs.amulet_of_fury)
                    .withAmmo(objs.dragon_arrow)
                    .withWeapon(objs.twisted_bow)
                    .withGloves(objs.barrows_gloves)
                    .withFeet(objs.pegasian_boots)
                    .withRing(objs.archers_ring_i)
                    .withPrayers(varbits.rigour)
                    .withNpcTarget(test_npcs.nex),
                Matchup(expectedAccuracy = 79.47)
                    .withAttackType(RangedAttackType.Standard)
                    .withAttackStyle(RangedAttackStyle.Rapid)
                    .withHelm(objs.armadyl_helmet)
                    .withBody(objs.armadyl_chestplate)
                    .withLegs(objs.armadyl_chainskirt)
                    .withCape(objs.avas_assembler)
                    .withAmulet(objs.amulet_of_fury)
                    .withAmmo(objs.runite_bolts)
                    .withWeapon(objs.dragon_hunter_crossbow)
                    .withShield(objs.dragonfire_ward)
                    .withGloves(objs.barrows_gloves)
                    .withFeet(objs.pegasian_boots)
                    .withRing(objs.archers_ring_i)
                    .withPrayers(varbits.hawk_eye)
                    .withRangingPotion()
                    .withNpcTarget(test_npcs.vorkath),
                Matchup(expectedAccuracy = 55.42)
                    .withAttackType(RangedAttackType.Standard)
                    .withAttackStyle(RangedAttackStyle.Rapid)
                    .withHelm(objs.void_ranger_helm)
                    .withBody(objs.elite_void_top)
                    .withLegs(objs.elite_void_robe)
                    .withCape(objs.avas_assembler)
                    .withAmulet(objs.necklace_of_anguish)
                    .withAmmo(objs.dragon_arrow)
                    .withWeapon(objs.twisted_bow)
                    .withGloves(objs.void_gloves)
                    .withFeet(objs.pegasian_boots)
                    .withRing(objs.venator_ring)
                    .withPrayers(varbits.rigour)
                    .withRangingPotion()
                    .withNpcTarget(test_npcs.abyssal_sire),
            )
        }
    }

    private class RangedAccuracyTestDependencies
    @Inject
    constructor(val accuracy: PvNRangedAccuracy)
}
