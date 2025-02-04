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
import org.rsmod.objtx.abyssal_whip
import org.rsmod.objtx.bank
import org.rsmod.objtx.bronze_arrow
import org.rsmod.objtx.pickaxe_handle
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction
import org.rsmod.objtx.trident_of_the_seas

@Execution(ExecutionMode.SAME_THREAD)
class BankLeftShiftQueryTest {
    @Test
    fun `shift from slot 4 to slot 2`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = null
        inventory[3] = null
        inventory[4] = Obj(bronze_arrow, 100)

        val result = transaction {
            val inv = select(inventory)
            leftShift {
                from = inv
                startSlot = 4
                toSlot = 2
            }
        }

        val expected =
            arrayOf(Obj(abyssal_whip), Obj(purple_sweets, 10), Obj(bronze_arrow, 100), null, null)
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(3, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slot 3 to slot 0`() {
        val inventory = bank()
        inventory[0] = null
        inventory[1] = null
        inventory[2] = null
        inventory[3] = Obj(trident_of_the_seas, vars = 16383)
        inventory[4] = Obj(bronze_arrow, 100)

        val result = transaction {
            val inv = select(inventory)
            leftShift {
                from = inv
                startSlot = 3
                toSlot = 0
            }
        }

        val expected =
            arrayOf(
                Obj(trident_of_the_seas, vars = 16383),
                Obj(bronze_arrow, 100),
                null,
                null,
                null,
            )
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(2, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slot 3 to slot 0 with center gaps`() {
        val inventory = bank()
        inventory[0] = null
        inventory[1] = null
        inventory[2] = null
        inventory[3] = Obj(trident_of_the_seas, vars = 16383)
        inventory[4] = null
        inventory[5] = null
        inventory[6] = Obj(bronze_arrow, 100)

        val result = transaction {
            val inv = select(inventory)
            leftShift {
                from = inv
                startSlot = 3
                toSlot = 0
            }
        }

        val expected =
            arrayOf(
                Obj(trident_of_the_seas, vars = 16383),
                null,
                null,
                Obj(bronze_arrow, 100),
                null,
                null,
                null,
            )
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(2, inventory.occupiedSpace())
    }

    @Test
    fun `shift with strict mode preventing overwrite`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(pickaxe_handle)
        inventory[3] = Obj(trident_of_the_seas, vars = 16383)
        inventory[4] = Obj(bronze_arrow, 100)

        val result = transaction {
            val inv = select(inventory)
            leftShift {
                from = inv
                startSlot = 4
                toSlot = 2
                strict = true
            }
        }

        val error = result.err
        assertEquals(TransactionResult.StrictSlotTaken, error)
        assertEquals(5, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slot 2 to slot 0 with non-strict mode`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(trident_of_the_seas, vars = 16383)
        inventory[3] = Obj(bronze_arrow, 100)

        val result = transaction {
            val inv = select(inventory)
            leftShift {
                from = inv
                startSlot = 2
                toSlot = 0
                strict = false
            }
        }

        val expected =
            arrayOf(Obj(trident_of_the_seas, vars = 16383), Obj(bronze_arrow, 100), null, null)
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(2, inventory.occupiedSpace())
    }

    @Test
    fun `shift should fail if toSlot is greater than or equal to startSlot`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(pickaxe_handle)
        inventory[3] = Obj(trident_of_the_seas, vars = 16383)
        inventory[4] = Obj(bronze_arrow, 100)

        val result = transaction {
            val inv = select(inventory)
            leftShift {
                from = inv
                startSlot = 2
                toSlot = 4
            }
        }

        val error = result[0]
        assertInstanceOf<TransactionResult.Exception>(error)
        assertEquals("`toSlot` must come before `startSlot`: target=4, start=2", error.message)
    }

    @Test
    fun `shift does nothing when inventory is empty`() {
        val inventory = bank()
        val result = transaction {
            val inv = select(inventory)
            leftShift {
                from = inv
                startSlot = 3
                toSlot = 0
                strict = false
            }
        }
        assertNull(result.err)
        assertEquals(0, inventory.occupiedSpace())
    }
}

private fun Transaction<Obj>.leftShift(init: Transaction<Obj>.LeftShiftQuery.() -> Unit) {
    val query = LeftShiftQuery().apply(init)
    execute(query)
}
