package org.runerealm.objtx.shift

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.objtx.Obj
import org.runerealm.objtx.Transaction
import org.runerealm.objtx.TransactionResult
import org.runerealm.objtx.abyssal_whip
import org.runerealm.objtx.bank
import org.runerealm.objtx.bronze_arrow
import org.runerealm.objtx.pickaxe_handle
import org.runerealm.objtx.purple_sweets
import org.runerealm.objtx.select
import org.runerealm.objtx.transaction
import org.runerealm.objtx.trident_of_the_seas

@Execution(ExecutionMode.SAME_THREAD)
class BankRightShiftQueryTest {
    @Test
    fun `shift right by 1 from slot 2`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(bronze_arrow, 100)
        inventory[3] = null
        inventory[4] = null

        val result = transaction {
            val inv = select(inventory)
            rightShift {
                from = inv
                startSlot = 2
                shiftCount = 1
            }
        }

        val expected =
            arrayOf(
                Obj(abyssal_whip),
                Obj(purple_sweets, 10),
                null,
                Obj(bronze_arrow, 100),
                null,
                null,
            )
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(3, inventory.occupiedSpace())
    }

    @Test
    fun `shift right by 2 from slot 1`() {
        val inventory = bank()
        inventory[0] = null
        inventory[1] = Obj(trident_of_the_seas, vars = 16383)
        inventory[2] = Obj(bronze_arrow, 100)
        inventory[3] = null
        inventory[4] = null

        val result = transaction {
            val inv = select(inventory)
            rightShift {
                from = inv
                startSlot = 1
                shiftCount = 2
            }
        }

        val expected =
            arrayOf(
                null,
                null,
                null,
                Obj(trident_of_the_seas, vars = 16383),
                Obj(bronze_arrow, 100),
                null,
                null,
            )
        val actual = inventory.objs.copyOfRange(0, expected.size)

        assertNull(result.err)
        assertEquals(expected.toList(), actual.toList())
        assertEquals(2, inventory.occupiedSpace())
    }

    @Test
    fun `shift right should fail if shift overflows inventory size`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(pickaxe_handle)

        val result = transaction {
            val inv = select(inventory)
            rightShift {
                from = inv
                startSlot = 0
                shiftCount = inventory.size - 2
            }
        }

        assertEquals(TransactionResult.NotEnoughSpace, result.err)
    }

    @Test
    fun `shift right should fail if shift overflows last inv obj`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[inventory.size - 1] = Obj(bronze_arrow, 100)

        val result = transaction {
            val inv = select(inventory)
            rightShift {
                from = inv
                startSlot = 0
                shiftCount = 1
            }
        }

        assertEquals(TransactionResult.NotEnoughSpace, result.err)
    }
}

private fun Transaction<Obj>.rightShift(init: Transaction<Obj>.RightShiftQuery.() -> Unit) {
    val query = RightShiftQuery().apply(init)
    execute(query)
}
