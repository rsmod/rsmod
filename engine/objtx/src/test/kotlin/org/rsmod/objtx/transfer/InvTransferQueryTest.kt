package org.rsmod.objtx.transfer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Obj
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.abyssal_whip
import org.rsmod.objtx.bank
import org.rsmod.objtx.cert_abyssal_whip
import org.rsmod.objtx.inv
import org.rsmod.objtx.placeholder_abyssal_whip
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class InvTransferQueryTest {
    @Test
    fun `transfer obj into bank with its placeholder`() {
        val invInventory = inv()
        val bankInventory = bank()
        invInventory[5] = Obj(abyssal_whip)
        bankInventory[2] = Obj(placeholder_abyssal_whip)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            transfer {
                from = inv
                into = bank
                fromSlot = 5
                count = Int.MAX_VALUE
                cert = true // Always StackType will override to automatically uncert
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertNull(invInventory[5])
        assertEquals(Obj(abyssal_whip), bankInventory[2])
        assertEquals(1, bankInventory.occupiedSpace())
        assertEquals(1, result.completed())
    }

    @Test
    fun `transfer uncert obj to bank`() {
        val invInventory = inv()
        val bankInventory = bank()
        invInventory[5] = Obj(abyssal_whip)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            transfer {
                from = inv
                into = bank
                fromSlot = 5
                cert = true // Always StackType will override to automatically uncert
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertNull(invInventory[5])
        assertEquals(Obj(abyssal_whip), bankInventory[0])
    }

    @Test
    fun `transfer cert obj to bank`() {
        val invInventory = inv()
        val bankInventory = bank()
        invInventory[5] = Obj(cert_abyssal_whip, 3)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            transfer {
                from = inv
                into = bank
                fromSlot = 5
                count = 1
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertEquals(Obj(cert_abyssal_whip, 2), invInventory[5])
        assertEquals(Obj(abyssal_whip), bankInventory[0])
    }

    @Test
    fun `transfer single var obj to bank`() {
        val invInventory = inv()
        val bankInventory = bank()
        invInventory[2] = Obj(purple_sweets, vars = 32)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            transfer {
                from = inv
                into = bank
                fromSlot = 2
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertNull(invInventory[2])
        assertEquals(Obj(purple_sweets, vars = 32), bankInventory[0])
    }

    @Test
    fun `transfer multiple var obj to bank`() {
        val invInventory = inv()
        val bankInventory = bank()
        invInventory[2] = Obj(purple_sweets, vars = 32)
        invInventory[4] = Obj(purple_sweets, vars = 64)
        invInventory[5] = Obj(purple_sweets, vars = 200)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            transfer {
                from = inv
                into = bank
                fromSlot = 2
                count = Int.MAX_VALUE
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertNull(invInventory[2])
        assertNull(invInventory[4])
        assertNull(invInventory[5])
        assertEquals(Obj(purple_sweets, vars = 32), bankInventory[0])
        assertEquals(Obj(purple_sweets, vars = 64), bankInventory[1])
        assertEquals(Obj(purple_sweets, vars = 200), bankInventory[2])
    }

    @Test
    fun `fail transfer when obj is not found`() {
        val invInventory = inv()
        val bankInventory = bank()
        invInventory[10] = Obj(abyssal_whip)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            transfer {
                from = inv
                into = bank
                fromSlot = 0
            }
        }
        assertTrue(result.failure)
        assertEquals(TransactionResult.ObjNotFound, result.err)
    }
}
