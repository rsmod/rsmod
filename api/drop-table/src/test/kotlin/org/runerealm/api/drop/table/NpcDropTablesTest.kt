package org.runerealm.api.drop.table

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.runerealm.api.testing.random.SequenceRandom
import org.runerealm.game.type.npc.HashedNpcType
import org.runerealm.game.type.obj.HashedObjType

class NpcDropTablesTest {
    private val bones = HashedObjType(null, "bones", 526)
    private val coins = HashedObjType(null, "coins", 995)
    private val herb = HashedObjType(null, "herb", 199)
    private val head = HashedObjType(null, "head", 13447)

    private val herbTable = weightedDropTable(2) { obj(2, herb) }

    private val table = npcDropTable {
        always(bones)
        main(128) {
            obj(100, coins, min = 2, max = 12)
            table(20, herbTable)
            nothing(8)
        }
        tertiary(1, 35, head)
    }

    private val tables = NpcDropTables(emptySet())

    @Test
    fun `always drops and main table lower bound`() {
        val random = SequenceRandom()
        random.next = 0 // Main roll: first entry (coins).
        random.then = 7 // Coin quantity.
        random.then = 1 // Tertiary roll fails (1 is not below 1).
        val drops = tables.roll(table, random)
        assertEquals(listOf(RolledDrop(bones, 1), RolledDrop(coins, 7)), drops)
    }

    @Test
    fun `main table entry boundaries`() {
        val random = SequenceRandom()
        random.next = 100 // First roll past coins (0..99): herb sub-table.
        random.then = 0 // Sub-table roll.
        random.then = 34 // Tertiary roll fails.
        assertEquals(listOf(RolledDrop(bones, 1), RolledDrop(herb, 1)), tables.roll(table, random))

        random.reset()
        random.next = 120 // Last 8 weights: nothing.
        random.then = 34
        assertEquals(listOf(RolledDrop(bones, 1)), tables.roll(table, random))
    }

    @Test
    fun `tertiary drop rolled independently`() {
        val random = SequenceRandom()
        random.next = 127 // Nothing.
        random.then = 0 // Tertiary roll succeeds.
        assertEquals(listOf(RolledDrop(bones, 1), RolledDrop(head, 1)), tables.roll(table, random))
    }

    @Test
    fun `weights must add up to denominator`() {
        assertThrows<IllegalArgumentException> { weightedDropTable(128) { obj(127, coins) } }
    }

    @Test
    fun `providers register tables by npc`() {
        val goblin = HashedNpcType(null, "goblin", 3029)
        val provider =
            object : NpcDropTableProvider() {
                init {
                    register(table, goblin)
                }
            }
        val registry = NpcDropTables(setOf(provider))
        assertEquals(table, registry[goblin])
        assertNull(registry[HashedNpcType(null, "cow", 2790)])
    }
}
