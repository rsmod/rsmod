package org.rsmod.api.combat.formulas.accuracy.magic

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatStance
import org.rsmod.api.combat.formulas.test_npcs
import org.rsmod.api.combat.weapon.scripts.WeaponAttackStylesScript
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
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
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam
import org.rsmod.game.entity.Npc
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.varbit.VarBitType

class NvPMagicAccuracyTest {
    @TestWithArgs(MatchupProvider::class)
    fun `calculate matchup hit chance`(matchup: Matchup, state: GameTestState) =
        state.runInjectedGameTest(
            MagicAccuracyTestDependencies::class,
            MagicAccuracyTestModule,
            WeaponAttackStylesScript::class,
        ) {
            val accuracy = it.accuracy

            player.setCurrentLevel(stats.defence, matchup.defenceLvl)
            player.setBaseLevel(stats.defence, matchup.baseDefenceLvl)
            player.setCurrentLevel(stats.hitpoints, matchup.hitpoints)
            player.setBaseLevel(stats.hitpoints, matchup.baseHitpointsLvl)
            player.setCurrentLevel(stats.magic, matchup.magicLvl)
            player.setBaseLevel(stats.magic, matchup.baseMagicLvl)
            player.setVarp(varps.com_mode, matchup.blockStance.varValue)

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
            val accuracyRoll = accuracy.getHitChance(npc = npc, target = player)
            assertEquals(matchup.expectedAccuracy, accuracyRoll / 100.0)
        }

    data class Matchup(
        val expectedAccuracy: Double,
        val npc: UnpackedNpcType = test_npcs.man,
        val blockStance: CombatStance = CombatStance.Stance1,
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
        val defenceLvl: Int = 99,
        val baseDefenceLvl: Int = 99,
        val hitpoints: Int = 99,
        val baseHitpointsLvl: Int = 99,
        val magicLvl: Int = 99,
        val baseMagicLvl: Int = 99,
        val prayers: Set<VarBitType> = emptySet(),
    ) {
        fun withNpcSource(npc: UnpackedNpcType) = copy(npc = npc)

        fun withBlockStance(stance: CombatStance) = copy(blockStance = stance)

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

        fun withDefenceLevel(defenceLvl: Int) = copy(defenceLvl = defenceLvl)

        fun withMagicLevel(magicLvl: Int) = copy(magicLvl = magicLvl)

        fun withPrayers(vararg prayers: VarBitType) = copy(prayers = prayers.toSet())

        fun withSaturatedHeart(): Matchup {
            val add = 4 + (baseMagicLvl * 0.1).toInt()
            return copy(magicLvl = baseMagicLvl + add)
        }

        override fun toString(): String =
            "Matchup(" +
                "expectedAccuracy=$expectedAccuracy, " +
                "npc=${NpcStatFormat()}, " +
                "player=${PlayerStatFormat()}" +
                ")"

        private inner class NpcStatFormat {
            override fun toString(): String = "Npc(name=${npc.name})"
        }

        private inner class PlayerStatFormat {
            override fun toString(): String =
                "Player(" +
                    "blockStance=$blockStance, " +
                    "defenceLevel=$defenceLvl / $baseDefenceLvl, " +
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
                Matchup(expectedAccuracy = 79.92).withNpcSource(test_npcs.dagannoth_prime),
                Matchup(expectedAccuracy = 82.95)
                    .withNpcSource(test_npcs.dagannoth_prime)
                    .withBlockStance(CombatStance.Stance4) // Unarmed "Block" stance.
                    .withMagicLevel(magicLvl = 50)
                    .withPrayers(varbits.augury),
                Matchup(expectedAccuracy = 51.11)
                    .withNpcSource(test_npcs.dagannoth_prime)
                    .withSaturatedHeart()
                    .withHelm(objs.ancestral_hat)
                    .withCape(objs.imbued_saradomin_cape)
                    .withAmulet(objs.occult_necklace)
                    .withBody(objs.ancestral_robe_top)
                    .withLegs(objs.ancestral_robe_bottom)
                    .withGloves(objs.tormented_bracelet)
                    .withFeet(objs.eternal_boots)
                    .withRing(objs.magus_ring)
                    .withPrayers(varbits.mystic_will),
                Matchup(expectedAccuracy = 83.38)
                    .withNpcSource(test_npcs.dagannoth_prime)
                    .withHelm(objs.torva_full_helm)
                    .withBody(objs.torva_platebody)
                    .withLegs(objs.torva_platelegs)
                    .withCape(objs.infernal_cape)
                    .withAmulet(objs.amulet_of_rancour)
                    .withGloves(objs.ferocious_gloves)
                    .withFeet(objs.primordial_boots)
                    .withRing(objs.ultor_ring)
                    .withPrayers(varbits.piety)
                    .withDefenceLevel(defenceLvl = 75),
            )
        }
    }

    private class MagicAccuracyTestDependencies @Inject constructor(val accuracy: NvPMagicAccuracy)

    private object MagicAccuracyTestModule : AbstractModule() {
        override fun configure() {
            bind(AttackStyles::class.java).`in`(Scopes.SINGLETON)
        }
    }
}
