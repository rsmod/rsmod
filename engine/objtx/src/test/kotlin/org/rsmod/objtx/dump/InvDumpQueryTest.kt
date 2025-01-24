package org.rsmod.objtx.dump

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Obj
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.abyssal_whip
import org.rsmod.objtx.bank
import org.rsmod.objtx.cert_abyssal_whip
import org.rsmod.objtx.inv
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class InvDumpQueryTest {
    @Test
    fun `dump cert obj into bank with uncert max stack minus one`() {
        val invInventory = inv()
        val bankInventory = bank()
        invInventory[0] = Obj(cert_abyssal_whip, 3)
        bankInventory[5] = Obj(abyssal_whip, Int.MAX_VALUE - 1)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            dump {
                from = inv
                into = bank
            }
        }
        assertNull(result.err)
        assertEquals(1, result.completed())
        assertEquals(Obj(abyssal_whip, Int.MAX_VALUE), bankInventory[5])
        assertEquals(Obj(cert_abyssal_whip, 2), invInventory[0])
    }

    @Test
    fun `dump cert obj into bank with uncert max stack`() {
        val invInventory = inv()
        val bankInventory = bank()
        invInventory[0] = Obj(cert_abyssal_whip)
        bankInventory[5] = Obj(abyssal_whip, Int.MAX_VALUE)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            dump {
                from = inv
                into = bank
            }
        }
        assertEquals(TransactionResult.NotEnoughSpace, result.err)
    }

    @Test
    fun `dump inv var objs into bank`() {
        val invInventory = inv()
        val bankInventory = bank()
        for (i in invInventory.indices) {
            invInventory[i] = Obj(id = i)
        }
        invInventory[3] = Obj(id = 3, vars = 16)
        invInventory[10] = Obj(id = 10, vars = 32)
        invInventory[25] = Obj(id = 25, vars = 48)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            dump {
                from = inv
                into = bank
            }
        }
        assertNull(result.err)
        assertEquals(0, invInventory.occupiedSpace())
        assertEquals(invInventory.size, bankInventory.occupiedSpace())
        assertEquals(Obj(id = 3, vars = 16), bankInventory[3])
        assertEquals(Obj(id = 10, vars = 32), bankInventory[10])
        assertEquals(Obj(id = 25, vars = 48), bankInventory[25])
    }

    @Test
    fun `fail to dump inv into full bank`() {
        val invInventory = inv()
        val bankInventory = bank()
        for (i in invInventory.indices) {
            invInventory[i] = Obj(id = i)
        }
        for (i in 0 until bankInventory.size) {
            bankInventory[i] = Obj(id = i + invInventory.size)
        }
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            dump {
                from = inv
                into = bank
            }
        }
        assertEquals(TransactionResult.NotEnoughSpace, result.err)
        assertEquals(invInventory.size, invInventory.occupiedSpace())
        assertEquals(bankInventory.size, bankInventory.occupiedSpace())
    }

    @Test
    fun `dump inv into bank`() {
        val invInventory = inv()
        val bankInventory = bank()
        for (i in invInventory.indices) {
            invInventory[i] = Obj(id = i)
        }
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            dump {
                from = inv
                into = bank
            }
        }
        assertNull(result.err)
        assertEquals(0, invInventory.occupiedSpace())
        assertEquals(invInventory.size, bankInventory.occupiedSpace())
    }

    @Test
    fun `dump inv into bank with exclusion`() {
        val invInventory = inv()
        val bankInventory = bank()
        for (i in invInventory.indices) {
            invInventory[i] = Obj(id = i)
        }
        val excluded = setOf(0, 3, 5)
        val result = transaction {
            val inv = select(invInventory)
            val bank = select(bankInventory)
            dump {
                from = inv
                into = bank
                keepSlots = excluded
            }
        }
        assertNull(result.err)
        assertEquals(excluded.size, invInventory.occupiedSpace())
        assertEquals(invInventory.size - excluded.size, bankInventory.occupiedSpace())
        assertNotNull(invInventory[0])
        assertNotNull(invInventory[3])
        assertNotNull(invInventory[5])
    }
}
