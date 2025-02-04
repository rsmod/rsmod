package org.rsmod.objtx.shift

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Obj
import org.rsmod.objtx.Transaction
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.bank
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class BankBulkShiftQueryTest {
    @Test
    fun `shift from slots 4 through 6 to slot 8`() {
        val inventory = bank()
        inventory[0] = Obj(1)
        inventory[1] = Obj(2)
        inventory[2] = null
        inventory[3] = null
        inventory[4] = Obj(3)
        inventory[5] = Obj(4)
        inventory[6] = Obj(5)
        inventory[7] = Obj(6)
        inventory[8] = Obj(7)
        inventory[9] = null
        inventory[10] = Obj(8)

        val result = transaction {
            val inv = select(inventory)
            bulkShift {
                from = inv
                fromSlots = 4..6
                intoSlot = 8
            }
        }

        val expected =
            arrayOf(
                Obj(1),
                Obj(2),
                null,
                null,
                Obj(6),
                Obj(7),
                Obj(3),
                Obj(4),
                Obj(5),
                null,
                Obj(8),
                null,
            )
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(8, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slots 4 through 6 to slot 7`() {
        val inventory = bank()
        inventory[0] = Obj(1)
        inventory[1] = Obj(2)
        inventory[2] = null
        inventory[3] = null
        inventory[4] = Obj(3)
        inventory[5] = Obj(4)
        inventory[6] = Obj(5)
        inventory[7] = Obj(6)
        inventory[8] = Obj(7)
        inventory[9] = null
        inventory[10] = Obj(8)

        val result = transaction {
            val inv = select(inventory)
            bulkShift {
                from = inv
                fromSlots = 4..6
                intoSlot = 7
            }
        }

        val expected =
            arrayOf(
                Obj(1),
                Obj(2),
                null,
                null,
                Obj(6),
                Obj(3),
                Obj(4),
                Obj(5),
                Obj(7),
                null,
                Obj(8),
                null,
            )
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(8, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slots 6 through 8 to slot 3`() {
        val inventory = bank()
        inventory[0] = Obj(1)
        inventory[1] = Obj(2)
        inventory[2] = null
        inventory[3] = null
        inventory[4] = Obj(3)
        inventory[5] = Obj(4)
        inventory[6] = Obj(5)
        inventory[7] = Obj(6)
        inventory[8] = Obj(7)
        inventory[9] = null
        inventory[10] = Obj(8)

        val result = transaction {
            val inv = select(inventory)
            bulkShift {
                from = inv
                fromSlots = 6..8
                intoSlot = 3
            }
        }

        val expected =
            arrayOf(
                Obj(1),
                Obj(2),
                null,
                Obj(5),
                Obj(6),
                Obj(7),
                null,
                Obj(3),
                Obj(4),
                null,
                Obj(8),
                null,
            )
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(8, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slots 6 through 8 to slot 5`() {
        val inventory = bank()
        inventory[0] = Obj(1)
        inventory[1] = Obj(2)
        inventory[2] = null
        inventory[3] = null
        inventory[4] = Obj(3)
        inventory[5] = Obj(4)
        inventory[6] = Obj(5)
        inventory[7] = Obj(6)
        inventory[8] = Obj(7)
        inventory[9] = null
        inventory[10] = Obj(8)

        val result = transaction {
            val inv = select(inventory)
            bulkShift {
                from = inv
                fromSlots = 6..8
                intoSlot = 5
            }
        }

        val expected =
            arrayOf(
                Obj(1),
                Obj(2),
                null,
                null,
                Obj(3),
                Obj(5),
                Obj(6),
                Obj(7),
                Obj(4),
                null,
                Obj(8),
                null,
            )
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(8, inventory.occupiedSpace())
    }

    @Test
    fun `throw when intoSlot is within fromSlots range`() {
        val inventory = bank()
        inventory[0] = Obj(1)
        inventory[1] = Obj(2)
        inventory[2] = null
        inventory[3] = null
        inventory[4] = Obj(3)
        inventory[5] = Obj(4)
        inventory[6] = Obj(5)
        inventory[7] = Obj(6)
        inventory[8] = Obj(7)
        inventory[9] = null
        inventory[10] = Obj(8)

        val result = transaction {
            val inv = select(inventory)
            bulkShift {
                from = inv
                fromSlots = 4..6
                intoSlot = 5
            }
        }
        val error = result.err

        assertInstanceOf<TransactionResult.Exception>(error)
        assertEquals(
            "`intoSlot` should not be within range of `fromSlots`. (fromSlots=4..6, intoSlot=5)",
            error.message,
        )
    }
}

private fun Transaction<Obj>.bulkShift(init: Transaction<Obj>.BulkShiftQuery.() -> Unit) {
    val query = BulkShiftQuery().apply(init)
    execute(query)
}
