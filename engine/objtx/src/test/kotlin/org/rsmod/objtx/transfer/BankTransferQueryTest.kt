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
import org.rsmod.objtx.placeholder_trident_of_the_seas
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction
import org.rsmod.objtx.trident_of_the_seas

@Execution(ExecutionMode.SAME_THREAD)
class BankTransferQueryTest {
    @Test
    fun `transfer multiple var obj to inv copying vars leaving placeholders`() {
        val bankInventory = bank()
        val invInventory = inv()
        bankInventory[2] = Obj(trident_of_the_seas, vars = 10)
        bankInventory[3] = Obj(trident_of_the_seas, vars = 64)
        bankInventory[4] = Obj(trident_of_the_seas, vars = 100)
        val result = transaction {
            val bank = select(bankInventory)
            val inv = select(invInventory)
            transfer {
                from = bank
                into = inv
                fromSlot = 2
                count = Int.MAX_VALUE
                placehold = true
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertEquals(Obj(trident_of_the_seas, vars = 10), invInventory[0])
        assertEquals(Obj(trident_of_the_seas, vars = 64), invInventory[1])
        assertEquals(Obj(trident_of_the_seas, vars = 100), invInventory[2])
        assertEquals(Obj(placeholder_trident_of_the_seas), bankInventory[2])
        assertEquals(Obj(placeholder_trident_of_the_seas), bankInventory[3])
        assertEquals(Obj(placeholder_trident_of_the_seas), bankInventory[4])
        assertEquals(3, bankInventory.occupiedSpace())
        assertEquals(3, invInventory.occupiedSpace())
    }

    @Test
    fun `transfer single var obj to inv copying vars`() {
        val bankInventory = bank()
        val invInventory = inv()
        bankInventory[2] = Obj(purple_sweets, vars = 32)
        val result = transaction {
            val bank = select(bankInventory)
            val inv = select(invInventory)
            transfer {
                from = bank
                into = inv
                fromSlot = 2
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertNull(bankInventory[2])
        assertEquals(Obj(purple_sweets, vars = 32), invInventory[0])
    }

    @Test
    fun `fail transfer when obj is not found`() {
        val bankInventory = bank()
        val invInventory = inv()
        val result = transaction {
            val bank = select(bankInventory)
            val inv = select(invInventory)
            transfer {
                from = bank
                into = inv
                fromSlot = 0
            }
        }
        assertTrue(result.failure)
        assertEquals(TransactionResult.ObjNotFound, result.err)
    }

    @Test
    fun `transfer obj to inv`() {
        val bankInventory = bank()
        val invInventory = inv()
        bankInventory[2] = Obj(abyssal_whip, 10)
        val result = transaction {
            val bank = select(bankInventory)
            val inv = select(invInventory)
            transfer {
                from = bank
                into = inv
                fromSlot = 2
                count = 2
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertEquals(Obj(abyssal_whip, 8), bankInventory[2])
        assertEquals(Obj(abyssal_whip, 1), invInventory[0])
        assertEquals(Obj(abyssal_whip, 1), invInventory[1])
        assertEquals(1, bankInventory.occupiedSpace())
        assertEquals(2, invInventory.occupiedSpace())
    }

    @Test
    fun `transfer obj to inv as cert`() {
        val bankInventory = bank()
        val invInventory = inv()
        bankInventory[2] = Obj(abyssal_whip, 10)
        val result = transaction {
            val bank = select(bankInventory)
            val inv = select(invInventory)
            transfer {
                from = bank
                into = inv
                fromSlot = 2
                count = 2
                cert = true
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertEquals(Obj(abyssal_whip, 8), bankInventory[2])
        assertEquals(Obj(cert_abyssal_whip, 2), invInventory[0])
        assertEquals(1, bankInventory.occupiedSpace())
        assertEquals(1, invInventory.occupiedSpace())
    }

    @Test
    fun `transfer full obj count to inv`() {
        val bankInventory = bank()
        val invInventory = inv()
        val fullCount = 10
        bankInventory[2] = Obj(abyssal_whip, fullCount)
        val result = transaction {
            val bank = select(bankInventory)
            val inv = select(invInventory)
            transfer {
                from = bank
                into = inv
                fromSlot = 2
                count = Int.MAX_VALUE
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertNull(bankInventory[2])
        for (i in 0 until fullCount) {
            assertEquals(Obj(abyssal_whip, 1), invInventory[i])
        }
        assertEquals(0, bankInventory.occupiedSpace())
        assertEquals(fullCount, invInventory.occupiedSpace())
        assertEquals(fullCount, result.asOk(0)?.completed)
    }

    @Test
    fun `transfer full obj count to inv leaving placeholder`() {
        val bankInventory = bank()
        val invInventory = inv()
        bankInventory[2] = Obj(abyssal_whip, 2)
        val result = transaction {
            val bank = select(bankInventory)
            val inv = select(invInventory)
            transfer {
                from = bank
                into = inv
                fromSlot = 2
                count = Int.MAX_VALUE
                placehold = true
            }
        }
        assertNull(result.err)
        assertTrue(result.success)
        assertEquals(Obj(placeholder_abyssal_whip), bankInventory[2])
        assertEquals(Obj(abyssal_whip, 1), invInventory[0])
        assertEquals(Obj(abyssal_whip, 1), invInventory[1])
        assertEquals(1, bankInventory.occupiedSpace())
        assertEquals(2, invInventory.occupiedSpace())
    }
}
