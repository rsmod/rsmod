package org.rsmod.api.combat.formulas.accuracy.magic

import com.google.inject.Inject
import org.rsmod.api.combat.commons.magic.Spellbook
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

class PvNMagicSpellAccuracyTest {
    @TestWithArgs(MatchupProvider::class)
    fun `calculate matchup hit chance`(matchup: Matchup, state: GameTestState) =
        state.runInjectedGameTest(SpellAccuracyTestDependencies::class) {
            val accuracy = it.accuracy

            player.setCurrentLevel(stats.magic, matchup.magicLvl)
            player.setBaseLevel(stats.magic, matchup.baseMagicLvl)
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
                accuracy.getSpellHitChance(
                    player = player,
                    target = npc,
                    spell = matchup.spell,
                    spellbook = matchup.spellbook,
                    usedSunfireRune = false,
                )
            assertEquals(matchup.expectedAccuracy, accuracyRoll / 100.0)
        }

    data class Matchup(
        val expectedAccuracy: Double,
        val spell: ObjType = objs.spell_wind_strike,
        val spellbook: Spellbook = Spellbook.Standard,
        val npc: UnpackedNpcType = test_npcs.man,
        val npcCurrHp: Int = 1,
        val npcMaxHp: Int = 1,
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
        val magicLvl: Int = 99,
        val baseMagicLvl: Int = 99,
        val hitpoints: Int = 99,
        val baseHitpointsLvl: Int = 99,
        val prayers: Set<VarBitType> = emptySet(),
    ) {
        fun withSpell(spell: ObjType): Matchup = copy(spell = spell)

        fun withNpcTarget(npc: UnpackedNpcType) = copy(npc = npc)

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

        fun withSaturatedHeart(): Matchup {
            val add = 4 + (baseMagicLvl * 0.1).toInt()
            return copy(magicLvl = baseMagicLvl + add)
        }

        fun withSmellingSalts(): Matchup {
            val add = 11 + (baseMagicLvl * 0.16).toInt()
            return copy(magicLvl = baseMagicLvl + add)
        }

        override fun toString(): String =
            "Matchup(" +
                "expectedAccuracy=$expectedAccuracy, " +
                "npc=${NpcStatFormat()}, " +
                "player=${PlayerStatFormat()}" +
                ")"

        private inner class NpcStatFormat {
            override fun toString(): String =
                "Npc(" + "name=${npc.name}, " + "hitpoints=$npcCurrHp / $npcMaxHp" + ")"
        }

        private inner class PlayerStatFormat {
            override fun toString(): String =
                "Player(" +
                    "magicLevel=$magicLvl / $baseMagicLvl, " +
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
                Matchup(expectedAccuracy = 97.30)
                    .withSpell(objs.spell_wind_strike)
                    .withWeapon(objs.air_staff)
                    .withNpcTarget(test_npcs.man),
                Matchup(expectedAccuracy = 98.57)
                    .withSpell(objs.spell_wind_strike)
                    .withWeapon(objs.air_staff)
                    .withHelm(objs.void_mage_helm)
                    .withBody(objs.elite_void_top)
                    .withLegs(objs.elite_void_robe)
                    .withGloves(objs.void_gloves)
                    .withFeet(objs.eternal_boots)
                    .withRing(objs.magus_ring)
                    .withNpcTarget(test_npcs.man),
                Matchup(expectedAccuracy = 98.87)
                    .withSpell(objs.spell_water_strike)
                    .withSmellingSalts()
                    .withWeapon(objs.staff_of_light)
                    .withHelm(objs.ahrims_hood_100)
                    .withCape(objs.imbued_saradomin_cape)
                    .withAmulet(objs.occult_necklace)
                    .withBody(objs.ahrims_robetop_100)
                    .withLegs(objs.ahrims_robeskirt_100)
                    .withGloves(objs.barrows_gloves)
                    .withFeet(objs.infinity_boots)
                    .withRing(objs.seers_ring_i)
                    .withPrayers(varbits.mystic_vigour_unlocked, varbits.mystic_might)
                    .withNpcTarget(test_npcs.dagannoth_rex),
                Matchup(expectedAccuracy = 14.76)
                    .withSpell(objs.spell_wind_strike)
                    .withWeapon(objs.staff_of_light)
                    .withHelm(objs.ancestral_hat)
                    .withCape(objs.imbued_saradomin_cape)
                    .withAmulet(objs.occult_necklace)
                    .withBody(objs.ancestral_robe_top)
                    .withLegs(objs.ancestral_robe_bottom)
                    .withGloves(objs.tormented_bracelet)
                    .withFeet(objs.eternal_boots)
                    .withRing(objs.magus_ring)
                    .withNpcTarget(test_npcs.corporeal_beast),
                Matchup(expectedAccuracy = 20.36)
                    .withSpell(objs.spell_fire_surge)
                    .withSaturatedHeart()
                    .withWeapon(objs.staff_of_light)
                    .withHelm(objs.ancestral_hat)
                    .withCape(objs.imbued_saradomin_cape)
                    .withAmulet(objs.occult_necklace)
                    .withBody(objs.ancestral_robe_top)
                    .withLegs(objs.ancestral_robe_bottom)
                    .withGloves(objs.tormented_bracelet)
                    .withFeet(objs.eternal_boots)
                    .withRing(objs.magus_ring)
                    .withPrayers(varbits.augury)
                    .withNpcTarget(test_npcs.corporeal_beast),
            )
        }
    }

    private class SpellAccuracyTestDependencies @Inject constructor(val accuracy: PvNMagicAccuracy)
}
