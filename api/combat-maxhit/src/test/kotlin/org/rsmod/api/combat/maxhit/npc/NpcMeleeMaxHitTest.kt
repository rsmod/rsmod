package org.rsmod.api.combat.maxhit.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class NpcMeleeMaxHitTest {
    @TestWithArgs(NpcProvider::class)
    fun `calculate base damage with stats`(npc: MeleeNpc) {
        val expectedMaxHit = npc.expectedMaxHit
        val visibleStrengthLvl = npc.strengthLevel
        val strengthBonus = npc.strengthBonus

        val effectiveStrength = NpcMeleeMaxHit.calculateEffectiveStrength(visibleStrengthLvl)
        val baseDamage = NpcMeleeMaxHit.calculateBaseDamage(effectiveStrength, strengthBonus)

        assertEquals(expectedMaxHit, baseDamage)
    }

    data class MeleeNpc(
        val identifier: String,
        val expectedMaxHit: Int,
        val strengthLevel: Int,
        val strengthBonus: Int,
    )

    private object NpcProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                MeleeNpc(
                    identifier = "Bandit (Level-130)",
                    expectedMaxHit = 12,
                    strengthLevel = 57,
                    strengthBonus = 52,
                ),
                MeleeNpc(
                    identifier = "Cave horror",
                    expectedMaxHit = 9,
                    strengthLevel = 77,
                    strengthBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Black dragon (Level-247)",
                    expectedMaxHit = 22,
                    strengthLevel = 215,
                    strengthBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Brawler (Level-129)",
                    expectedMaxHit = 14,
                    strengthLevel = 132,
                    strengthBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Brutal black dragon",
                    expectedMaxHit = 29,
                    strengthLevel = 210,
                    strengthBonus = 20,
                ),
                MeleeNpc(
                    identifier = "Dad (Hard)",
                    expectedMaxHit = 57,
                    strengthLevel = 264,
                    strengthBonus = 70,
                ),
                MeleeNpc(
                    identifier = "Dagannoth Rex",
                    expectedMaxHit = 26,
                    strengthLevel = 255,
                    strengthBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Double agent (Level-141)",
                    expectedMaxHit = 19,
                    strengthLevel = 105,
                    strengthBonus = 40,
                ),
                MeleeNpc(
                    identifier = "General Graardor",
                    expectedMaxHit = 60,
                    strengthLevel = 350,
                    strengthBonus = 43,
                ),
                MeleeNpc(
                    identifier = "Man",
                    expectedMaxHit = 1,
                    strengthLevel = 1,
                    strengthBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Molanisk",
                    expectedMaxHit = 5,
                    strengthLevel = 40,
                    strengthBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Muttadile (Large)",
                    expectedMaxHit = 48,
                    strengthLevel = 250,
                    strengthBonus = 55,
                ),
                MeleeNpc(
                    identifier = "Tekton (Normal)",
                    expectedMaxHit = 52,
                    strengthLevel = 390,
                    strengthBonus = 20,
                ),
                MeleeNpc(
                    identifier = "Third Age Warrior",
                    expectedMaxHit = 18,
                    strengthLevel = 75,
                    strengthBonus = 75,
                ),
                MeleeNpc(
                    identifier = "TzTok-Jad",
                    expectedMaxHit = 97,
                    strengthLevel = 960,
                    strengthBonus = 0,
                ),
                MeleeNpc(
                    identifier = "Vorkath",
                    expectedMaxHit = 32,
                    strengthLevel = 308,
                    strengthBonus = 0,
                ),
            )
        }
    }
}
