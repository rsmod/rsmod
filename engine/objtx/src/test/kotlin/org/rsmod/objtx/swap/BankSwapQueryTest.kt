package org.rsmod.objtx.swap

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Obj
import org.rsmod.objtx.bank
import org.rsmod.objtx.bronze_arrow
import org.rsmod.objtx.pickaxe_handle
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.red_partyhat
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction
import org.rsmod.objtx.trident_of_the_seas

@Execution(ExecutionMode.SAME_THREAD)
class BankSwapQueryTest {
    @Test
    fun `swap obj to empty slot`() {
        val inventory = bank()
        inventory[0] = Obj(red_partyhat)
        inventory[1] = Obj(purple_sweets, 100)
        inventory[2] = Obj(bronze_arrow, 50)
        inventory[3] = Obj(trident_of_the_seas)
        inventory[4] = Obj(pickaxe_handle)
        val result = transaction {
            val inv = select(inventory)
            swap {
                from = inv
                into = inv
                fromSlot = 3
                intoSlot = 10
            }
        }
        assertNull(result.err)
        assertEquals(Obj(trident_of_the_seas), inventory[10])
        assertEquals(null, inventory[3])
        assertEquals(5, inventory.occupiedSpace())
        assertEquals(Obj(red_partyhat), inventory[0])
        assertEquals(Obj(purple_sweets, 100), inventory[1])
        assertEquals(Obj(bronze_arrow, 50), inventory[2])
        assertEquals(Obj(pickaxe_handle), inventory[4])
    }

    @Test
    fun `swap two var objs`() {
        val inventory = bank()
        inventory[0] = Obj(trident_of_the_seas, vars = 63)
        inventory[1] = Obj(purple_sweets, 100)
        inventory[2] = Obj(bronze_arrow, 50)
        inventory[3] = Obj(trident_of_the_seas, vars = 10380)
        inventory[4] = Obj(pickaxe_handle)
        val result = transaction {
            val inv = select(inventory)
            swap {
                from = inv
                into = inv
                fromSlot = 3
                intoSlot = 0
            }
        }
        assertNull(result.err)
        assertEquals(Obj(trident_of_the_seas, vars = 10380), inventory[0])
        assertEquals(Obj(trident_of_the_seas, vars = 63), inventory[3])
        assertEquals(5, inventory.occupiedSpace())
        assertEquals(Obj(purple_sweets, 100), inventory[1])
        assertEquals(Obj(bronze_arrow, 50), inventory[2])
        assertEquals(Obj(pickaxe_handle), inventory[4])
    }

    @Test
    fun `swap two objs`() {
        val inventory = bank()
        inventory[0] = Obj(red_partyhat)
        inventory[1] = Obj(purple_sweets, 100)
        inventory[2] = Obj(bronze_arrow, 50)
        inventory[3] = Obj(trident_of_the_seas)
        inventory[4] = Obj(pickaxe_handle)
        val result = transaction {
            val inv = select(inventory)
            swap {
                from = inv
                into = inv
                fromSlot = 3
                intoSlot = 1
            }
        }
        assertNull(result.err)
        assertEquals(Obj(trident_of_the_seas), inventory[1])
        assertEquals(Obj(purple_sweets, 100), inventory[3])
        assertEquals(5, inventory.occupiedSpace())
        assertEquals(Obj(red_partyhat), inventory[0])
        assertEquals(Obj(bronze_arrow, 50), inventory[2])
        assertEquals(Obj(pickaxe_handle), inventory[4])
    }
}
