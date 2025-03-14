package org.rsmod.api.combat.accuracy.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class NpcRangedAccuracyTest {
    @TestWithArgs(NpcProvider::class)
    fun `calculate base rolls with stats`(npc: RangedNpc) {
        val expectedAttackRoll = npc.expectedAttackRoll
        val expectedDefenceRoll = npc.expectedDefenceRoll
        val visibleRangedLvl = npc.rangedLevel
        val rangedBonus = npc.rangedBonus
        val visibleDefenceLvl = npc.defenceLevel
        val defenceBonus = npc.defenceBonus

        val effectiveRanged = NpcRangedAccuracy.calculateEffectiveRanged(visibleRangedLvl)
        val attackRoll = NpcRangedAccuracy.calculateBaseAttackRoll(effectiveRanged, rangedBonus)
        assertEquals(expectedAttackRoll, attackRoll)

        val effectiveDefence = NpcRangedAccuracy.calculateEffectiveDefence(visibleDefenceLvl)
        val defenceRoll = NpcRangedAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
        assertEquals(expectedDefenceRoll, defenceRoll)
    }

    data class RangedNpc(
        val identifier: String,
        val expectedAttackRoll: Int,
        val expectedDefenceRoll: Int,
        val rangedLevel: Int,
        val rangedBonus: Int,
        val defenceLevel: Int,
        val defenceBonus: Int,
    )

    private object NpcProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                RangedNpc(
                    identifier = "Dagannoth Supreme",
                    expectedAttackRoll = 16896,
                    expectedDefenceRoll = 84118,
                    rangedLevel = 255,
                    rangedBonus = 0,
                    defenceLevel = 128,
                    defenceBonus = 550,
                ),
                RangedNpc(
                    identifier = "Karil the Tainted",
                    expectedAttackRoll = 21582,
                    expectedDefenceRoll = 17876,
                    rangedLevel = 100,
                    rangedBonus = 134,
                    defenceLevel = 100,
                    defenceBonus = 100,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Armadyl)",
                    expectedAttackRoll = 10176,
                    expectedDefenceRoll = 14595,
                    rangedLevel = 150,
                    rangedBonus = 0,
                    defenceLevel = 130,
                    defenceBonus = 41,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Bandos)",
                    expectedAttackRoll = 8320,
                    expectedDefenceRoll = 8085,
                    rangedLevel = 121,
                    rangedBonus = 0,
                    defenceLevel = 96,
                    defenceBonus = 13,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Saradomin)",
                    expectedAttackRoll = 9920,
                    expectedDefenceRoll = 9483,
                    rangedLevel = 146,
                    rangedBonus = 0,
                    defenceLevel = 100,
                    defenceBonus = 23,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Zamorak)",
                    expectedAttackRoll = 9536,
                    expectedDefenceRoll = 5696,
                    rangedLevel = 140,
                    rangedBonus = 0,
                    defenceLevel = 80,
                    defenceBonus = 0,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Zaros)",
                    expectedAttackRoll = 12736,
                    expectedDefenceRoll = 12426,
                    rangedLevel = 190,
                    rangedBonus = 0,
                    defenceLevel = 100,
                    defenceBonus = 50,
                ),
                RangedNpc(
                    identifier = "Zulrah",
                    expectedAttackRoll = 35226,
                    expectedDefenceRoll = 35226,
                    rangedLevel = 300,
                    rangedBonus = 50,
                    defenceLevel = 300,
                    defenceBonus = 50,
                ),
            )
        }
    }
}
