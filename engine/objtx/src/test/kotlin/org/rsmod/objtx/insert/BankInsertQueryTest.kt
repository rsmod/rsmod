package org.rsmod.objtx.insert

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
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
import org.rsmod.objtx.placeholder_abyssal_whip
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.red_partyhat
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class BankInsertQueryTest {
    @Test
    fun `fail to insert obj in taken strict slot`() {
        val inventory = bank()
        inventory[2] = Obj(red_partyhat)
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = abyssal_whip
                strictSlot = 2
            }
        }
        assertEquals(TransactionResult.StrictSlotTaken, transaction.err)
        assertEquals(Obj(red_partyhat), inventory[2])
        assertEquals(1, inventory.occupiedSpace())
    }

    @Test
    fun `insert obj in strict slot`() {
        val inventory = bank()
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = abyssal_whip
                strictSlot = 2
            }
        }
        assertNull(transaction.err)
        assertEquals(Obj(abyssal_whip), inventory[2])
        assertEquals(1, inventory.occupiedSpace())
        assertEquals(1, transaction.size)
        assertEquals(1, transaction.completed())
    }

    @Test
    fun `obj with cert template and cert flag should auto-uncert`() {
        val inventory = bank()
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = abyssal_whip
                cert = true
            }
        }
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(1, transaction.completed())
    }

    @Test
    fun `insert non-stackable obj`() {
        val inventory = bank()
        val targetSlot = 10
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = abyssal_whip
                slot = targetSlot
                uncert = true
            }
        }
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        assertEquals(Obj(abyssal_whip), inventory[targetSlot])
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(1, transaction.completed())
        assertEquals(0, transaction[0]?.left)
    }

    @Test
    fun `insert non-stackable obj into target slot`() {
        val inventory = bank()
        val request = 10
        val targetSlot = 10
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = abyssal_whip
                count = request
                slot = targetSlot
                uncert = true
            }
        }
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        assertEquals(Obj(abyssal_whip, request), inventory[targetSlot])
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request, transaction.completed())
        assertEquals(0, transaction[0]?.left)
    }

    @Test
    fun `fail to insert stackable obj due to count overflow`() {
        val inventory = bank()
        inventory[0] = Obj(purple_sweets)
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = purple_sweets
                strictCount = Int.MAX_VALUE
                uncert = true
            }
        }
        assertTrue(transaction.failure)
        assertFalse(transaction.success)
        assertEquals(TransactionResult.NotEnoughSpace, transaction.err)
        assertEquals(Obj(purple_sweets), inventory[0])
        assertEquals(1, transaction.size)
        assertEquals(0, transaction.completed())
    }

    @Test
    fun `insert stackable obj capping count overflow due to leniency`() {
        val inventory = bank()
        inventory[0] = Obj(purple_sweets)
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = purple_sweets
                count = Int.MAX_VALUE
                uncert = true
            }
        }
        assertFalse(transaction.failure)
        assertTrue(transaction.success)
        assertEquals(Int.MAX_VALUE - 1, transaction[0]?.completed)
        assertEquals(1, transaction[0]?.left)
        assertEquals(Obj(purple_sweets, Int.MAX_VALUE), inventory[0])
    }

    @Test
    fun `insert cert obj`() {
        val inventory = bank()
        val request = 10
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = cert_abyssal_whip
                count = request
                uncert = true
            }
        }
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        assertEquals(Obj(abyssal_whip, request), inventory[0])
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request, transaction.completed())
    }

    @Test
    fun `insert var obj without cert template`() {
        val inventory = bank()
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = purple_sweets
                count = 2
                vars = 16
                slot = 10
                uncert = true
            }
        }
        assertFalse(transaction.failure)
        assertTrue(transaction.success)
        assertEquals(2, inventory.occupiedSpace())
        assertEquals(Obj(purple_sweets, vars = 16), inventory[10])
        assertEquals(Obj(purple_sweets, vars = 16), inventory[11])
    }

    @Test
    fun `insert var obj with cert template`() {
        val inventory = bank()
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = abyssal_whip
                count = 2
                vars = 16
                slot = 10
                uncert = true
            }
        }
        assertTrue(transaction.failure)
        assertFalse(transaction.success)
        assertInstanceOf(TransactionResult.VarObjIncorrectlyHasCert::class.java, transaction.err)
    }

    @Test
    fun `insert uncert obj into inv containing its placeholder`() {
        val inventory = bank()
        val placeholderSlot = 5
        inventory[placeholderSlot] = Obj(placeholder_abyssal_whip)
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = abyssal_whip
                count = 5
                slot = 10
                uncert = true
            }
        }
        assertTrue(transaction.success)
        assertFalse(transaction.failure)
        assertEquals(5, transaction.completed())
        assertEquals(Obj(abyssal_whip, 5), inventory[placeholderSlot])
        assertEquals(1, inventory.occupiedSpace())
    }

    @Test
    fun `insert cert obj into inv containing its placeholder`() {
        val inventory = bank()
        val placeholderSlot = 5
        inventory[placeholderSlot] = Obj(placeholder_abyssal_whip)
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = cert_abyssal_whip
                count = 5
                slot = 10
                uncert = true
            }
        }
        assertTrue(transaction.success)
        assertFalse(transaction.failure)
        assertEquals(5, transaction.completed())
        assertEquals(Obj(abyssal_whip, 5), inventory[placeholderSlot])
        assertEquals(1, inventory.occupiedSpace())
    }

    @Test
    fun `fail to insert obj with count of zero`() {
        val inventory = bank()
        val transaction = transaction {
            val bankInv = select(inventory)
            insert {
                into = bankInv
                obj = abyssal_whip
                count = 0
                uncert = true
            }
        }
        assertEquals(TransactionResult.InvalidCountRequest, transaction.err)
        assertEquals(0, inventory.occupiedSpace())
        assertEquals(inventory.size, inventory.freeSpace())
    }
}
