package org.rsmod.api.combat.accuracy.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class NpcMeleeAccuracyTest {
    @TestWithArgs(NpcProvider::class)
    fun `calculate base rolls with stats`(npc: MeleeNpc) {
        val expectedAttackRoll = npc.expectedAttackRoll
        val expectedDefenceRoll = npc.expectedDefenceRoll
        val visibleAttackLvl = npc.attackLevel
        val attackBonus = npc.attackBonus
        val visibleDefenceLvl = npc.defenceLevel
        val defenceBonus = npc.defenceBonus

        val effectiveAttack = NpcMeleeAccuracy.calculateEffectiveAttack(visibleAttackLvl)
        val attackRoll = NpcMeleeAccuracy.calculateBaseAttackRoll(effectiveAttack, attackBonus)
        assertEquals(expectedAttackRoll, attackRoll)

        val effectiveDefence = NpcMeleeAccuracy.calculateEffectiveDefence(visibleDefenceLvl)
        val defenceRoll = NpcMeleeAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
        assertEquals(expectedDefenceRoll, defenceRoll)
    }

    data class MeleeNpc(
        val identifier: String,
        val expectedAttackRoll: Int,
        val expectedDefenceRoll: Int,
        val attackLevel: Int,
        val attackBonus: Int,
        val defenceLevel: Int,
        val defenceBonus: Int,
    )

    private object NpcProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                MeleeNpc(
                    identifier = "Bandit (Level-130) [Slash]",
                    expectedAttackRoll = 7986,
                    expectedDefenceRoll = 5742,
                    attackLevel = 57,
                    attackBonus = 57,
                    defenceLevel = 57,
                    defenceBonus = 23,
                ),
                MeleeNpc(
                    identifier = "Bandit (Level-130) [Stab]",
                    expectedAttackRoll = 7986,
                    expectedDefenceRoll = 4224,
                    attackLevel = 57,
                    attackBonus = 57,
                    defenceLevel = 57,
                    defenceBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Brawler (Level-129)",
                    expectedAttackRoll = 4864,
                    expectedDefenceRoll = 8128,
                    attackLevel = 67,
                    attackBonus = 0,
                    defenceLevel = 118,
                    defenceBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Dad (Hard) [Slash]",
                    expectedAttackRoll = 14664,
                    expectedDefenceRoll = 5251,
                    attackLevel = 132,
                    attackBonus = 40,
                    defenceLevel = 50,
                    defenceBonus = 25,
                ),
                MeleeNpc(
                    identifier = "Dad (Hard) [Crush]",
                    expectedAttackRoll = 14664,
                    expectedDefenceRoll = 6136,
                    attackLevel = 132,
                    attackBonus = 40,
                    defenceLevel = 50,
                    defenceBonus = 40,
                ),
                MeleeNpc(
                    identifier = "Dagannoth Rex",
                    expectedAttackRoll = 16896,
                    expectedDefenceRoll = 84216,
                    attackLevel = 255,
                    attackBonus = 0,
                    defenceLevel = 255,
                    defenceBonus = 255,
                ),
                MeleeNpc(
                    identifier = "Double agent (Level-141)",
                    expectedAttackRoll = 14456,
                    expectedDefenceRoll = 9156,
                    attackLevel = 130,
                    attackBonus = 40,
                    defenceLevel = 100,
                    defenceBonus = 20,
                ),
            )
        }
    }
}
