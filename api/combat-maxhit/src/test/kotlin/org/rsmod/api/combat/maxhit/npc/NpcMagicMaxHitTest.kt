package org.rsmod.api.combat.maxhit.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class NpcMagicMaxHitTest {
    @TestWithArgs(NpcProvider::class)
    fun `calculate base damage with stats`(npc: MagicNpc) {
        val expectedMaxHit = npc.expectedMaxHit
        val visibleMagicLvl = npc.magicLevel
        val magicBonus = npc.magicBonus

        val effectiveMagic = NpcMagicMaxHit.calculateEffectiveMagic(visibleMagicLvl)
        val baseDamage = NpcMagicMaxHit.calculateBaseDamage(effectiveMagic, magicBonus)

        assertEquals(expectedMaxHit, baseDamage)
    }

    data class MagicNpc(
        val identifier: String,
        val expectedMaxHit: Int,
        val magicLevel: Int,
        val magicBonus: Int,
    )

    // Note: A lot of magic-based npc attacks use hardcoded max hit values, which do not
    // align with the standard max hit formula.
    private object NpcProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                MagicNpc(
                    identifier = "Corrupted Hunllef",
                    expectedMaxHit = 68,
                    magicLevel = 240,
                    magicBonus = 112,
                ),
                MagicNpc(
                    identifier = "Muttadile (Large)",
                    expectedMaxHit = 23,
                    magicLevel = 250,
                    magicBonus = -8,
                ),
                MagicNpc(
                    identifier = "The Leviathan",
                    expectedMaxHit = 32,
                    magicLevel = 160,
                    magicBonus = 58,
                ),
                MagicNpc(
                    identifier = "TzKal-Zuk",
                    expectedMaxHit = 128,
                    magicLevel = 150,
                    magicBonus = 450,
                ),
                MagicNpc(
                    identifier = "Vanguard",
                    expectedMaxHit = 22,
                    magicLevel = 150,
                    magicBonus = 25,
                ),
                MagicNpc(
                    identifier = "Vorkath",
                    expectedMaxHit = 30,
                    magicLevel = 150,
                    magicBonus = 56,
                ),
                MagicNpc(
                    identifier = "Zulrah",
                    expectedMaxHit = 41,
                    magicLevel = 300,
                    magicBonus = 20,
                ),
            )
        }
    }
}
