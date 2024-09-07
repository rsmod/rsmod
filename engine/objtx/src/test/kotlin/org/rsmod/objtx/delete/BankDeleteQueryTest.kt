package org.rsmod.objtx.delete

import org.junit.jupiter.api.Assertions.assertEquals
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
import org.rsmod.objtx.pickaxe_handle
import org.rsmod.objtx.placeholder_abyssal_whip
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.red_partyhat
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class BankDeleteQueryTest {
    @Test
    fun `delete obj from empty bank`() {
        val inventory = bank()
        val result = transaction {
            val bankInv = select(inventory)
            delete {
                from = bankInv
                obj = abyssal_whip
            }
        }
        assertInstanceOf(TransactionResult.ObjNotFound::class.java, result.err)
    }

    @Test
    fun `delete obj from empty bank with leniency`() {
        val inventory = bank()
        val result = transaction {
            val bankInv = select(inventory)
            delete {
                from = bankInv
                obj = abyssal_whip
                count = 1
            }
        }
        assertEquals(0, inventory.occupiedSpace())
        assertTrue(result.success)
    }

    @Test
    fun `delete obj`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        val result = transaction {
            val bankInv = select(inventory)
            autoCommit = false
            delete {
                from = bankInv
                obj = abyssal_whip
            }
        }
        check(1 == inventory.occupiedSpace())
        result.commitAll()
        assertEquals(0, inventory.occupiedSpace())
        assertNull(inventory[0])
    }

    @Test
    fun `delete single obj with placeholder flag and placeholder template`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        val result = transaction {
            val bankInv = select(inventory)
            delete {
                from = bankInv
                obj = abyssal_whip
                placehold = true
            }
        }
        assertTrue(result.success)
        assertEquals(1, inventory.occupiedSpace())
        assertEquals(Obj(placeholder_abyssal_whip), inventory[0])
    }

    @Test
    fun `delete single obj with placeholder flag and no placeholder template`() {
        val inventory = bank()
        inventory[0] = Obj(pickaxe_handle)
        val result = transaction {
            val bankInv = select(inventory)
            delete {
                from = bankInv
                obj = pickaxe_handle
                placehold = true
            }
        }
        assertTrue(result.success)
        assertNull(inventory[0])
        assertEquals(0, inventory.occupiedSpace())
    }

    @Test
    fun `delete obj in populated bank`() {
        val inventory = bank()
        inventory[0] = Obj(red_partyhat)
        inventory[2] = Obj(purple_sweets, 10)
        inventory[5] = Obj(abyssal_whip)
        inventory[6] = Obj(red_partyhat)
        val result = transaction {
            val bankInv = select(inventory)
            delete {
                from = bankInv
                obj = abyssal_whip
            }
        }
        assertEquals(3, inventory.occupiedSpace())
        assertNull(inventory[5])
        assertTrue(result.success)
        assertEquals(1, result.completed())
    }

    @Test
    fun `delete multiple var objs with placeholder flag and placeholder template`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip, vars = 192)
        inventory[1] = Obj(abyssal_whip, vars = 55)
        inventory[2] = Obj(abyssal_whip, vars = 16)
        val result = transaction {
            val bankInv = select(inventory)
            delete {
                from = bankInv
                obj = abyssal_whip
                count = 3
                placehold = true
            }
        }
        assertTrue(result.success)
        assertEquals(3, inventory.occupiedSpace())
        assertEquals(Obj(placeholder_abyssal_whip), inventory[0])
        assertEquals(Obj(placeholder_abyssal_whip), inventory[1])
        assertEquals(Obj(placeholder_abyssal_whip), inventory[2])
    }
}
