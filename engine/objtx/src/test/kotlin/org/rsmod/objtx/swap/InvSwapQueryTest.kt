package org.rsmod.objtx.swap

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Obj
import org.rsmod.objtx.abyssal_whip
import org.rsmod.objtx.bronze_arrow
import org.rsmod.objtx.inv
import org.rsmod.objtx.iron_arrow
import org.rsmod.objtx.red_partyhat
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction
import org.rsmod.objtx.trident_of_the_seas
import org.rsmod.objtx.worn

@Execution(ExecutionMode.SAME_THREAD)
class InvSwapQueryTest {
    @Test
    fun `swap obj with existing worn with merge flag`() {
        val invInventory = inv()
        val wornInventory = worn()
        invInventory[1] = Obj(red_partyhat)
        invInventory[5] = Obj(bronze_arrow, count = Int.MAX_VALUE)
        wornInventory[13] = Obj(iron_arrow, count = Int.MAX_VALUE - 10)
        val result = transaction {
            val inv = select(invInventory)
            val worn = select(wornInventory)
            swap {
                from = inv
                into = worn
                fromSlot = 5
                intoSlot = 13
                merge = true
            }
        }
        assertTrue(result.success)
        assertEquals(Obj(bronze_arrow, count = Int.MAX_VALUE), wornInventory[13])
        assertEquals(Obj(iron_arrow, count = Int.MAX_VALUE - 10), invInventory[5])
        assertEquals(Obj(red_partyhat), invInventory[1])
        assertEquals(Int.MAX_VALUE, result.completed())
        assertEquals(2, invInventory.occupiedSpace())
        assertEquals(1, wornInventory.occupiedSpace())
    }

    @Test
    fun `merge obj with existing worn`() {
        val invInventory = inv()
        val wornInventory = worn()
        invInventory[1] = Obj(red_partyhat)
        invInventory[5] = Obj(bronze_arrow, count = Int.MAX_VALUE)
        wornInventory[13] = Obj(bronze_arrow, count = Int.MAX_VALUE - 10)
        val result = transaction {
            val inv = select(invInventory)
            val worn = select(wornInventory)
            swap {
                from = inv
                into = worn
                fromSlot = 5
                intoSlot = 13
                merge = true
            }
        }
        assertTrue(result.success)
        assertEquals(Obj(bronze_arrow, count = Int.MAX_VALUE - 10), invInventory[5])
        assertEquals(Obj(bronze_arrow, count = Int.MAX_VALUE), wornInventory[13])
        assertEquals(Obj(red_partyhat), invInventory[1])
        assertEquals(10, result.completed())
        assertEquals(2, invInventory.occupiedSpace())
        assertEquals(1, wornInventory.occupiedSpace())
    }

    @Test
    fun `merge should fail when existing worn obj has a max stack`() {
        val invInventory = inv()
        val wornInventory = worn()
        invInventory[1] = Obj(red_partyhat)
        invInventory[5] = Obj(bronze_arrow, count = 1)
        wornInventory[13] = Obj(bronze_arrow, count = Int.MAX_VALUE)
        val result = transaction {
            val inv = select(invInventory)
            val worn = select(wornInventory)
            swap {
                from = inv
                into = worn
                fromSlot = 5
                intoSlot = 13
                merge = true
            }
        }
        assertTrue(result.failure)
        assertEquals(Obj(bronze_arrow, count = Int.MAX_VALUE), wornInventory[13])
        assertEquals(Obj(bronze_arrow, count = 1), invInventory[5])
        assertEquals(Obj(red_partyhat), invInventory[1])
        assertEquals(0, result.completed())
        assertEquals(2, invInventory.occupiedSpace())
        assertEquals(1, wornInventory.occupiedSpace())
    }

    @Test
    fun `merge should succeed when inv obj is max stack and worn obj is not`() {
        val invInventory = inv()
        val wornInventory = worn()
        invInventory[1] = Obj(red_partyhat)
        invInventory[5] = Obj(bronze_arrow, count = Int.MAX_VALUE)
        wornInventory[13] = Obj(bronze_arrow, count = 1)
        val result = transaction {
            val inv = select(invInventory)
            val worn = select(wornInventory)
            swap {
                from = inv
                into = worn
                fromSlot = 5
                intoSlot = 13
                merge = true
            }
        }
        assertTrue(result.success)
        assertEquals(Obj(bronze_arrow, count = Int.MAX_VALUE), wornInventory[13])
        assertEquals(Obj(bronze_arrow, count = 1), invInventory[5])
        assertEquals(Obj(red_partyhat), invInventory[1])
        assertEquals(Int.MAX_VALUE - 1, result.completed())
        assertEquals(2, invInventory.occupiedSpace())
        assertEquals(1, wornInventory.occupiedSpace())
    }

    @Test
    fun `swap max stack obj with max stack obj`() {
        val invInventory = inv()
        val wornInventory = worn()
        invInventory[1] = Obj(red_partyhat)
        invInventory[5] = Obj(bronze_arrow, count = Int.MAX_VALUE)
        wornInventory[13] = Obj(iron_arrow, count = Int.MAX_VALUE)
        val result = transaction {
            val inv = select(invInventory)
            val worn = select(wornInventory)
            swap {
                from = inv
                into = worn
                fromSlot = 5
                intoSlot = 13
            }
        }
        assertNull(result.err)
        assertEquals(Obj(bronze_arrow, count = Int.MAX_VALUE), wornInventory[13])
        assertEquals(Obj(iron_arrow, count = Int.MAX_VALUE), invInventory[5])
        assertEquals(Obj(red_partyhat), invInventory[1])
        assertEquals(2, invInventory.occupiedSpace())
        assertEquals(1, wornInventory.occupiedSpace())
    }

    @Test
    fun `swap obj into taken worn slot`() {
        val invInventory = inv()
        val wornInventory = worn()
        invInventory[1] = Obj(red_partyhat)
        invInventory[2] = Obj(abyssal_whip)
        wornInventory[3] = Obj(trident_of_the_seas, vars = 15394)
        val result = transaction {
            val inv = select(invInventory)
            val worn = select(wornInventory)
            swap {
                from = inv
                into = worn
                fromSlot = 2
                intoSlot = 3
            }
        }
        assertNull(result.err)
        assertEquals(Obj(abyssal_whip), wornInventory[3])
        assertEquals(Obj(trident_of_the_seas, vars = 15394), invInventory[2])
        assertEquals(Obj(red_partyhat), invInventory[1])
        assertEquals(2, invInventory.occupiedSpace())
        assertEquals(1, wornInventory.occupiedSpace())
    }

    @Test
    fun `swap obj into empty worn slot`() {
        val invInventory = inv()
        val wornInventory = worn()
        invInventory[1] = Obj(red_partyhat)
        invInventory[2] = Obj(abyssal_whip)
        val result = transaction {
            val inv = select(invInventory)
            val worn = select(wornInventory)
            swap {
                from = inv
                into = worn
                fromSlot = 2
                intoSlot = 3
            }
        }
        assertNull(result.err)
        assertNull(invInventory[2])
        assertEquals(Obj(abyssal_whip), wornInventory[3])
        assertEquals(Obj(red_partyhat), invInventory[1])
        assertEquals(1, invInventory.occupiedSpace())
        assertEquals(1, wornInventory.occupiedSpace())
    }
}
