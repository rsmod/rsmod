package org.rsmod.objtx.swap

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Obj
import org.rsmod.objtx.bronze_arrow
import org.rsmod.objtx.inv
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction
import org.rsmod.objtx.trident_of_the_seas
import org.rsmod.objtx.worn

@Execution(ExecutionMode.SAME_THREAD)
class WornSwapQueryTest {
    @Test
    fun `merge worn obj into inv ignoring intoSlot`() {
        val wornInventory = worn()
        val invInventory = inv()
        wornInventory[13] = Obj(bronze_arrow, count = 100_000)
        invInventory[3] = Obj(bronze_arrow, count = 1000)
        val result = transaction {
            val worn = select(wornInventory)
            val inv = select(invInventory)
            swap {
                from = worn
                into = inv
                fromSlot = 13
                intoSlot = 1
            }
        }
        assertNull(result.err)
        assertNull(wornInventory[13])
        assertEquals(Obj(bronze_arrow, count = 101_000), invInventory[3])
        assertNull(invInventory[1])
    }

    @Test
    fun `merge worn stackable obj into inv`() {
        val wornInventory = worn()
        val invInventory = inv()
        wornInventory[13] = Obj(bronze_arrow, count = 100_000)
        invInventory[3] = Obj(bronze_arrow, count = 1000)
        val result = transaction {
            val worn = select(wornInventory)
            val inv = select(invInventory)
            swap {
                from = worn
                into = inv
                fromSlot = 13
            }
        }
        assertNull(result.err)
        assertNull(wornInventory[13])
        assertEquals(Obj(bronze_arrow, count = 101_000), invInventory[3])
    }

    @Test
    fun `swap worn stackable obj to inv into any slot`() {
        val wornInventory = worn()
        val invInventory = inv()
        wornInventory[13] = Obj(bronze_arrow, count = 100_000)
        val result = transaction {
            val worn = select(wornInventory)
            val inv = select(invInventory)
            swap {
                from = worn
                into = inv
                fromSlot = 13
            }
        }
        assertNull(result.err)
        assertNull(wornInventory[13])
        assertEquals(Obj(bronze_arrow, count = 100_000), invInventory[0])
    }

    @Test
    fun `swap worn non-stackable obj to inv into strict slot`() {
        val wornInventory = worn()
        val invInventory = inv()
        wornInventory[3] = Obj(trident_of_the_seas, vars = 32)
        val result = transaction {
            val worn = select(wornInventory)
            val inv = select(invInventory)
            swap {
                from = worn
                into = inv
                fromSlot = 3
                intoSlot = 5
            }
        }
        assertNull(result.err)
        assertNull(wornInventory[3])
        assertEquals(Obj(trident_of_the_seas, vars = 32), invInventory[5])
    }

    @Test
    fun `swap worn non-stackable obj to inv into any slot`() {
        val wornInventory = worn()
        val invInventory = inv()
        wornInventory[3] = Obj(trident_of_the_seas, vars = 16)
        val result = transaction {
            val worn = select(wornInventory)
            val inv = select(invInventory)
            swap {
                from = worn
                into = inv
                fromSlot = 3
            }
        }
        assertNull(result.err)
        assertNull(wornInventory[3])
        assertEquals(Obj(trident_of_the_seas, vars = 16), invInventory[0])
    }
}
