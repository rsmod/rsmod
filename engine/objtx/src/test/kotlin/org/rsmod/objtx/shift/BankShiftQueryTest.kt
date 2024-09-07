package org.rsmod.objtx.shift

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Obj
import org.rsmod.objtx.abyssal_whip
import org.rsmod.objtx.bank
import org.rsmod.objtx.bronze_arrow
import org.rsmod.objtx.pickaxe_handle
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction
import org.rsmod.objtx.trident_of_the_seas

@Execution(ExecutionMode.SAME_THREAD)
class BankShiftQueryTest {
    @Test
    fun `shift into empty slot should swap instead of shift`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(pickaxe_handle)
        inventory[3] = null
        inventory[4] = Obj(bronze_arrow, 100)
        val result = transaction {
            val inv = select(inventory)
            shift {
                from = inv
                fromSlot = 1
                intoSlot = 3
            }
        }
        assertNull(result.err)
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertNull(inventory[1])
        assertEquals(Obj(pickaxe_handle), inventory[2])
        assertEquals(Obj(purple_sweets, 10), inventory[3])
        assertEquals(Obj(bronze_arrow, 100), inventory[4])
        assertEquals(4, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slot 4 to slot 3`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(pickaxe_handle)
        inventory[3] = Obj(trident_of_the_seas, vars = 16383)
        inventory[4] = Obj(bronze_arrow, 100)
        val result = transaction {
            val inv = select(inventory)
            shift {
                from = inv
                fromSlot = 4
                intoSlot = 3
            }
        }
        assertNull(result.err)
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(Obj(purple_sweets, 10), inventory[1])
        assertEquals(Obj(pickaxe_handle), inventory[2])
        assertEquals(Obj(bronze_arrow, 100), inventory[3])
        assertEquals(Obj(trident_of_the_seas, vars = 16383), inventory[4])
        assertEquals(5, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slot 4 to slot 0`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(pickaxe_handle)
        inventory[3] = Obj(trident_of_the_seas, vars = 16383)
        inventory[4] = Obj(bronze_arrow, 100)
        val result = transaction {
            val inv = select(inventory)
            shift {
                from = inv
                fromSlot = 4
                intoSlot = 0
            }
        }
        assertNull(result.err)
        assertEquals(Obj(bronze_arrow, 100), inventory[0])
        assertEquals(Obj(abyssal_whip), inventory[1])
        assertEquals(Obj(purple_sweets, 10), inventory[2])
        assertEquals(Obj(pickaxe_handle), inventory[3])
        assertEquals(Obj(trident_of_the_seas, vars = 16383), inventory[4])
        assertEquals(5, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slot 3 into slot 4`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(pickaxe_handle)
        inventory[3] = Obj(trident_of_the_seas, vars = 16383)
        inventory[4] = Obj(bronze_arrow, 100)
        val result = transaction {
            val inv = select(inventory)
            shift {
                from = inv
                fromSlot = 3
                intoSlot = 4
            }
        }
        assertNull(result.err)
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(Obj(purple_sweets, 10), inventory[1])
        assertEquals(Obj(pickaxe_handle), inventory[2])
        assertEquals(Obj(bronze_arrow, 100), inventory[3])
        assertEquals(Obj(trident_of_the_seas, vars = 16383), inventory[4])
        assertEquals(5, inventory.occupiedSpace())
    }

    @Test
    fun `shift from slot 0 into slot 3`() {
        val inventory = bank()
        inventory[0] = Obj(abyssal_whip)
        inventory[1] = Obj(purple_sweets, 10)
        inventory[2] = Obj(pickaxe_handle)
        inventory[3] = Obj(trident_of_the_seas, vars = 16383)
        inventory[4] = Obj(bronze_arrow, 100)
        val result = transaction {
            val inv = select(inventory)
            shift {
                from = inv
                fromSlot = 0
                intoSlot = 3
            }
        }
        assertNull(result.err)
        assertEquals(Obj(purple_sweets, 10), inventory[0])
        assertEquals(Obj(pickaxe_handle), inventory[1])
        assertEquals(Obj(trident_of_the_seas, vars = 16383), inventory[2])
        assertEquals(Obj(abyssal_whip), inventory[3])
        assertEquals(Obj(bronze_arrow, 100), inventory[4])
        assertEquals(5, inventory.occupiedSpace())
    }
}
