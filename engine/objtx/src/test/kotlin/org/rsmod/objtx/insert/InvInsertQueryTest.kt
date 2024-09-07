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
import org.rsmod.objtx.cert_abyssal_whip
import org.rsmod.objtx.inv
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class InvInsertQueryTest {
    @Test
    fun `insert non-stackable obj`() {
        val inventory = inv()
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
            }
        }
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(1, transaction.completed())
        assertEquals(0, transaction[0]?.left)
    }

    @Test
    fun `insert non-stackable obj into full inv`() {
        val inventory = inv()
        for (i in inventory.indices) {
            inventory[i] = Obj(abyssal_whip)
        }
        check(inventory.size == inventory.occupiedSpace())
        check(0 == inventory.freeSpace())
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                strictCount = 1
            }
        }
        assertTrue(transaction.failure)
        assertFalse(transaction.success)
        assertEquals(TransactionResult.NotEnoughSpace, transaction.err)
        assertEquals(1, transaction.size)
        assertEquals(0, transaction.completed())
    }

    @Test
    fun `insert non-stackable objs with preferred start slot`() {
        val inventory = inv()
        val request = 10
        val startSlot = 5
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = request
                slot = startSlot
            }
        }
        check(request == inventory.occupiedSpace())
        check(inventory.size - request == inventory.freeSpace())
        repeat(request) { assertEquals(Obj(abyssal_whip), inventory[startSlot + it]) }
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request, transaction.completed())
        assertEquals(0, transaction[0]?.left)
    }

    @Test
    fun `fail to insert too many objs`() {
        val inventory = inv()
        val request = inventory.size + 1
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                strictCount = request
            }
        }
        assertTrue(transaction.failure)
        assertFalse(transaction.success)
        assertEquals(TransactionResult.NotEnoughSpace, transaction.err)
        assertEquals(1, transaction.size)
        assertEquals(0, transaction.completed())
    }

    @Test
    fun `insert obj into full inv`() {
        val inventory = inv()
        for (i in inventory.indices) {
            inventory[i] = Obj(abyssal_whip)
        }
        check(inventory.size == inventory.occupiedSpace())
        check(0 == inventory.freeSpace())
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = 1
            }
        }
        assertFalse(transaction.failure)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(0, transaction.completed())
        assertEquals(1, transaction[0]?.left)
    }

    @Test
    fun `insert stackable obj into preferred slot`() {
        val inventory = inv()
        val request = 500
        val startSlot = 5
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = purple_sweets
                count = request
                slot = startSlot
            }
        }
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        assertEquals(Obj(purple_sweets, request), inventory[startSlot])
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request, transaction.completed())
    }

    @Test
    fun `fail to insert stackable obj due to count overflow`() {
        val inventory = inv()
        val request = 1000
        inventory[5] = Obj(purple_sweets, Int.MAX_VALUE - request + 1)
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = purple_sweets
                strictCount = request
            }
        }
        assertTrue(transaction.failure)
        assertFalse(transaction.success)
        assertEquals(TransactionResult.NotEnoughSpace, transaction.err)
        assertEquals(1, transaction.size)
        assertEquals(0, transaction.completed())
    }

    @Test
    fun `insert stackable obj capping count overflow due to leniency`() {
        val inventory = inv()
        val request = 1000
        val leftover = 50
        val slot = 5
        inventory[slot] = Obj(purple_sweets, Int.MAX_VALUE - request + leftover)
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = purple_sweets
                count = request
            }
        }
        assertFalse(transaction.failure)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request - leftover, transaction.completed())
        assertEquals(Obj(purple_sweets, Int.MAX_VALUE), inventory[slot])
        assertEquals(leftover, transaction[0]?.left)
    }

    @Test
    fun `insert cert template from uncert obj`() {
        val inventory = inv()
        val request = 10
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = request
                cert = true
            }
        }
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        assertEquals(Obj(cert_abyssal_whip, request), inventory[0])
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request, transaction.completed())
    }

    @Test
    fun `insert uncert template from cert obj`() {
        val inventory = inv()
        val request = 10
        val startSlot = 5
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = cert_abyssal_whip
                count = request
                slot = startSlot
                uncert = true
            }
        }
        check(request == inventory.occupiedSpace())
        check(inventory.size - request == inventory.freeSpace())
        repeat(request) { assertEquals(Obj(abyssal_whip), inventory[startSlot + it]) }
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request, transaction.completed())
    }

    @Test
    fun `insert uncert obj with uncert flag`() {
        val inventory = inv()
        val request = 10
        val startSlot = 5
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = request
                slot = startSlot
                uncert = true
            }
        }
        check(request == inventory.occupiedSpace())
        check(inventory.size - request == inventory.freeSpace())
        repeat(request) { assertEquals(Obj(abyssal_whip), inventory[startSlot + it]) }
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request, transaction.completed())
    }

    @Test
    fun `insert cert obj with cert flag`() {
        val inventory = inv()
        val request = 10
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = cert_abyssal_whip
                count = request
                cert = true
            }
        }
        check(1 == inventory.occupiedSpace())
        check(inventory.size - 1 == inventory.freeSpace())
        assertEquals(Obj(cert_abyssal_whip, request), inventory[0])
        assertNull(transaction.err)
        assertTrue(transaction.success)
        assertEquals(1, transaction.size)
        assertEquals(request, transaction.completed())
    }

    @Test
    fun `insert stackable var objs separately with cert flag`() {
        val inventory = inv()
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = purple_sweets
                count = 2
                vars = 8
                cert = true
            }
        }
        assertFalse(transaction.failure)
        assertTrue(transaction.success)
        assertEquals(2, inventory.occupiedSpace())
        assertEquals(Obj(purple_sweets, vars = 8), inventory[0])
        assertEquals(Obj(purple_sweets, vars = 8), inventory[1])
    }

    @Test
    fun `insert var objs with cert template and cert flag`() {
        val inventory = inv()
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = 2
                vars = 8
                cert = true
            }
        }
        assertTrue(transaction.failure)
        assertFalse(transaction.success)
        assertInstanceOf(TransactionResult.VarObjIncorrectlyHasCert::class.java, transaction.err)
    }

    @Test
    fun `fail to insert obj with count of zero`() {
        val inventory = inv()
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = 0
            }
        }
        assertEquals(TransactionResult.InvalidCountRequest, transaction.err)
        assertEquals(0, inventory.occupiedSpace())
        assertEquals(inventory.size, inventory.freeSpace())
    }
}
