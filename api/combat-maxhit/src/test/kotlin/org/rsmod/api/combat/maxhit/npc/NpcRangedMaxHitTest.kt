package org.rsmod.api.combat.maxhit.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class NpcRangedMaxHitTest {
    @TestWithArgs(NpcProvider::class)
    fun `calculate base damage with stats`(npc: RangedNpc) {
        val expectedMaxHit = npc.expectedMaxHit
        val visibleRangedLvl = npc.rangedLevel
        val rangedBonus = npc.rangedBonus

        val effectiveRanged = NpcRangedMaxHit.calculateEffectiveRanged(visibleRangedLvl)
        val baseDamage = NpcRangedMaxHit.calculateBaseDamage(effectiveRanged, rangedBonus)

        assertEquals(expectedMaxHit, baseDamage)
    }

    data class RangedNpc(
        val identifier: String,
        val expectedMaxHit: Int,
        val rangedLevel: Int,
        val rangedBonus: Int,
    )

    private object NpcProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                RangedNpc(
                    identifier = "Dagannoth Supreme",
                    expectedMaxHit = 26,
                    rangedLevel = 255,
                    rangedBonus = 0,
                ),
                RangedNpc(
                    identifier = "Karil the Tainted",
                    expectedMaxHit = 20,
                    rangedLevel = 100,
                    rangedBonus = 55,
                ),
                RangedNpc(
                    identifier = "Kree'arra",
                    expectedMaxHit = 69,
                    rangedLevel = 380,
                    rangedBonus = 50,
                ),
                RangedNpc(
                    identifier = "Muttadile (Large)",
                    expectedMaxHit = 45,
                    rangedLevel = 250,
                    rangedBonus = 47,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Armadyl)",
                    expectedMaxHit = 16,
                    rangedLevel = 150,
                    rangedBonus = 0,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Bandos)",
                    expectedMaxHit = 13,
                    rangedLevel = 121,
                    rangedBonus = 0,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Saradomin)",
                    expectedMaxHit = 16,
                    rangedLevel = 146,
                    rangedBonus = 0,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Zamorak)",
                    expectedMaxHit = 15,
                    rangedLevel = 140,
                    rangedBonus = 0,
                ),
                RangedNpc(
                    identifier = "Spiritual ranger (Zaros)",
                    expectedMaxHit = 20,
                    rangedLevel = 190,
                    rangedBonus = 0,
                ),
                RangedNpc(
                    identifier = "TzKal-Zuk",
                    expectedMaxHit = 169,
                    rangedLevel = 400,
                    rangedBonus = 200,
                ),
                RangedNpc(
                    identifier = "TzTok-Jad",
                    expectedMaxHit = 97,
                    rangedLevel = 960,
                    rangedBonus = 0,
                ),
                RangedNpc(
                    identifier = "Vorkath",
                    expectedMaxHit = 32,
                    rangedLevel = 308,
                    rangedBonus = 0,
                ),
                RangedNpc(
                    identifier = "Zulrah",
                    expectedMaxHit = 41,
                    rangedLevel = 300,
                    rangedBonus = 20,
                ),
            )
        }
    }
}
