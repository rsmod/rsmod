package org.rsmod.objtx.delete

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Obj
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.abyssal_whip
import org.rsmod.objtx.inv
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.red_partyhat
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class InvDeleteQueryTest {
    @Test
    fun `delete obj from empty inv`() {
        val inventory = inv()
        val result = transaction {
            val inv = select(inventory)
            delete {
                from = inv
                obj = abyssal_whip
            }
        }
        assertInstanceOf(TransactionResult.ObjNotFound::class.java, result.err)
    }

    @Test
    fun `delete obj from empty inv with leniency`() {
        val inventory = inv()
        val result = transaction {
            val inv = select(inventory)
            delete {
                from = inv
                obj = abyssal_whip
                count = 1
            }
        }
        assertEquals(0, inventory.occupiedSpace())
        assertTrue(result.success)
    }

    @Test
    fun `delete single obj`() {
        val inventory = inv()
        inventory[0] = Obj(abyssal_whip)
        val result = transaction {
            val inv = select(inventory)
            autoCommit = false
            delete {
                from = inv
                obj = abyssal_whip
            }
        }
        check(1 == inventory.occupiedSpace())
        result.commitAll()
        assertEquals(0, inventory.occupiedSpace())
        assertNull(inventory[0])
    }

    @Test
    fun `fail to delete obj not found in strict slot`() {
        val inventory = inv()
        inventory[0] = Obj(abyssal_whip)
        val result = transaction {
            val inv = select(inventory)
            delete {
                from = inv
                obj = abyssal_whip
                strictSlot = 5
            }
        }
        assertEquals(TransactionResult.ObjNotFound, result.err)
        assertEquals(1, inventory.occupiedSpace())
        assertEquals(Obj(abyssal_whip), inventory[0])
    }

    @Test
    fun `delete obj in preferred slot`() {
        val inventory = inv()
        val targetSlot = 5
        inventory[0] = Obj(abyssal_whip)
        inventory[targetSlot] = Obj(abyssal_whip)
        val result = transaction {
            val inv = select(inventory)
            autoCommit = false
            delete {
                from = inv
                obj = abyssal_whip
                slot = targetSlot
            }
        }
        check(2 == inventory.occupiedSpace())
        result.commitAll()
        assertEquals(1, inventory.occupiedSpace())
        assertNull(inventory[targetSlot])
        assertEquals(Obj(abyssal_whip), inventory[0])
    }

    @Test
    fun `delete obj using preferred slot`() {
        val inventory = inv()
        val otherSlot = 5
        val actualSlot = 8
        inventory[0] = Obj(abyssal_whip)
        inventory[otherSlot] = Obj(red_partyhat)
        inventory[actualSlot] = Obj(abyssal_whip)
        inventory[actualSlot + 1] = Obj(abyssal_whip)
        val result = transaction {
            val inv = select(inventory)
            autoCommit = false
            delete {
                from = inv
                obj = abyssal_whip
                slot = otherSlot
            }
        }
        check(4 == inventory.occupiedSpace())
        result.commitAll()
        assertEquals(3, inventory.occupiedSpace())
        assertNull(inventory[actualSlot])
        assertNotNull(inventory[otherSlot])
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(Obj(red_partyhat), inventory[otherSlot])
        assertEquals(Obj(abyssal_whip), inventory[actualSlot + 1])
    }

    @Test
    fun `delete obj in populated inv`() {
        val inventory = inv()
        inventory[0] = Obj(red_partyhat)
        inventory[2] = Obj(purple_sweets, 10)
        inventory[5] = Obj(abyssal_whip)
        inventory[6] = Obj(red_partyhat)
        val result = transaction {
            val inv = select(inventory)
            autoCommit = false
            delete {
                from = inv
                obj = abyssal_whip
            }
        }
        check(4 == inventory.occupiedSpace())
        result.commitAll()
        assertEquals(3, inventory.occupiedSpace())
        assertNull(inventory[5])
        assertTrue(result.success)
        assertEquals(1, result.completed())
    }
}
