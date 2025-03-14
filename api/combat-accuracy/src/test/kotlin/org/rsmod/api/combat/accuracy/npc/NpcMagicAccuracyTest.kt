package org.rsmod.api.combat.accuracy.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class NpcMagicAccuracyTest {
    @TestWithArgs(NpcProvider::class)
    fun `calculate base rolls with stats`(npc: MagicNpc) {
        val expectedAttackRoll = npc.expectedAttackRoll
        val expectedDefenceRoll = npc.expectedDefenceRoll
        val visibleMagicLvl = npc.magicLevel
        val magicBonus = npc.magicBonus
        val visibleDefenceLvl = npc.defenceLevel
        val defenceBonus = npc.defenceBonus

        val effectiveMagic = NpcMagicAccuracy.calculateEffectiveMagic(visibleMagicLvl)
        val attackRoll = NpcMagicAccuracy.calculateBaseAttackRoll(effectiveMagic, magicBonus)
        assertEquals(expectedAttackRoll, attackRoll)

        val effectiveDefence = NpcMagicAccuracy.calculateEffectiveDefence(visibleDefenceLvl)
        val defenceRoll = NpcMagicAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
        assertEquals(expectedDefenceRoll, defenceRoll)
    }

    data class MagicNpc(
        val identifier: String,
        val expectedAttackRoll: Int,
        val expectedDefenceRoll: Int,
        val magicLevel: Int,
        val magicBonus: Int,
        val defenceBonus: Int,
        // By default, npcs use their magic level for defence, excluding a select few.
        val defenceLevel: Int = magicLevel,
    )

    private object NpcProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                MagicNpc(
                    identifier = "Baboon Mage (Level-56)",
                    expectedAttackRoll = 4116,
                    expectedDefenceRoll = 47236,
                    magicLevel = 40,
                    magicBonus = 20,
                    defenceBonus = 900,
                ),
                MagicNpc(
                    identifier = "Baboon Mage (Level-68)",
                    expectedAttackRoll = 6141,
                    expectedDefenceRoll = 66516,
                    magicLevel = 60,
                    magicBonus = 25,
                    defenceBonus = 900,
                ),
                MagicNpc(
                    identifier = "Gnome Mage",
                    expectedAttackRoll = 640,
                    expectedDefenceRoll = 340,
                    magicLevel = 1,
                    magicBonus = 0,
                    defenceBonus = -30,
                ),
                MagicNpc(
                    identifier = "Vanguard",
                    expectedAttackRoll = 16536,
                    expectedDefenceRoll = 27666,
                    magicLevel = 150,
                    magicBonus = 40,
                    defenceBonus = 110,
                ),
                MagicNpc(
                    identifier = "Zulrah",
                    expectedAttackRoll = 35226,
                    expectedDefenceRoll = 5871,
                    magicLevel = 300,
                    magicBonus = 50,
                    defenceBonus = -45,
                ),
            )
        }
    }
}
