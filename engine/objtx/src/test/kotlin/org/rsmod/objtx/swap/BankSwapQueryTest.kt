package org.rsmod.objtx.swap

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Inventory
import org.rsmod.objtx.Obj
import org.rsmod.objtx.TransactionInventory
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.abyssal_whip
import org.rsmod.objtx.bank
import org.rsmod.objtx.bronze_arrow
import org.rsmod.objtx.pickaxe_handle
import org.rsmod.objtx.purple_sweets
import org.rsmod.objtx.red_partyhat
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction
import org.rsmod.objtx.trident_of_the_seas
import org.rsmod.objtx.worn

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

    @Test
    fun `swap stackable obj stack into non-stacking inv with merge`() {
        val bank = bank()
        val worn = worn()
        bank[0] = Obj(red_partyhat)
        bank[1] = Obj(purple_sweets, 100)
        bank[2] = Obj(bronze_arrow, 50)
        bank[3] = Obj(trident_of_the_seas, 3)
        bank[4] = Obj(pickaxe_handle)
        worn[13] = Obj(bronze_arrow, 100)
        val result = transaction {
            val bankInv = select(bank)
            val wornInv = select(worn)
            swap {
                from = bankInv
                into = wornInv
                fromSlot = 2
                intoSlot = 13
                merge = true
                strict = true
            }
        }
        assertNull(result.err)
        assertNull(bank[2])
        assertEquals(Obj(bronze_arrow, 150), worn[13])
        assertEquals(4, bank.occupiedSpace())
        assertEquals(1, worn.occupiedSpace())
    }

    @Test
    fun `swap single non-stackable obj into slot-taken non-stacking inv`() {
        val bank = bank()
        val worn = worn()
        bank[0] = Obj(red_partyhat)
        bank[1] = Obj(purple_sweets, 100)
        bank[2] = Obj(bronze_arrow, 50)
        bank[3] = Obj(trident_of_the_seas)
        bank[4] = Obj(pickaxe_handle)
        worn[3] = Obj(abyssal_whip)
        val result = transaction {
            val bankInv = select(bank)
            val wornInv = select(worn)
            swap {
                from = bankInv
                into = wornInv
                fromSlot = 3
                intoSlot = 3
                merge = true
                strict = true
            }
        }
        assertNull(result.err)
        assertEquals(Obj(abyssal_whip), bank[3])
        assertEquals(Obj(trident_of_the_seas), worn[3])
        assertEquals(5, bank.occupiedSpace())
        assertEquals(1, worn.occupiedSpace())
    }

    @Test
    fun `swap stacked non-stackable obj into non-stacking inv as single obj`() {
        val bank = bank()
        val worn = worn()
        bank[0] = Obj(red_partyhat)
        bank[1] = Obj(purple_sweets, 100)
        bank[2] = Obj(bronze_arrow, 50)
        bank[3] = Obj(trident_of_the_seas, 3)
        bank[4] = Obj(pickaxe_handle)
        val result = transaction {
            val bankInv = select(bank)
            val wornInv = select(worn)
            swap {
                from = bankInv
                into = wornInv
                fromSlot = 3
                intoSlot = 3
                merge = true
                strict = true
            }
        }
        assertNull(result.err)
        assertEquals(Obj(trident_of_the_seas, 2), bank[3])
        assertEquals(Obj(trident_of_the_seas, 1), worn[3])
        assertEquals(5, bank.occupiedSpace())
        assertEquals(1, worn.occupiedSpace())
    }

    @Test
    fun `swap stacked non-stackable obj into slot-taken non-stacking inv as single obj`() {
        val bank = bank()
        val worn = worn()
        bank[0] = Obj(red_partyhat)
        bank[1] = Obj(purple_sweets, 100)
        bank[2] = Obj(bronze_arrow, 50)
        bank[3] = Obj(trident_of_the_seas, 3)
        bank[4] = Obj(pickaxe_handle)
        worn[3] = Obj(abyssal_whip)
        val result = transaction {
            val bankInv = select(bank)
            val wornInv = select(worn)
            swap {
                from = bankInv
                into = wornInv
                fromSlot = 3
                intoSlot = 3
                merge = true
                strict = true
            }
        }
        assertEquals(TransactionResult.StrictSlotTaken, result.err)
        assertEquals(Obj(trident_of_the_seas, 3), bank[3])
        assertEquals(Obj(abyssal_whip), worn[3])
        assertEquals(5, bank.occupiedSpace())
        assertEquals(1, worn.occupiedSpace())
    }

    @Test
    fun `swap stacked non-stackable obj into slot-taken non-stacking inv with leniency`() {
        val bank = bank()
        val worn = worn()
        bank[0] = Obj(red_partyhat)
        bank[1] = Obj(purple_sweets, 100)
        bank[2] = Obj(bronze_arrow, 50)
        bank[3] = Obj(trident_of_the_seas, 3)
        bank[4] = Obj(pickaxe_handle)
        worn[3] = Obj(abyssal_whip)
        val result = transaction {
            val bankInv = select(bank)
            val wornInv = select(worn)
            swap {
                from = bankInv
                into = wornInv
                fromSlot = 3
                intoSlot = 3
                merge = true
                strict = false
            }
        }
        assertNull(result.err)
        assertEquals(Obj(trident_of_the_seas, 2), bank[3])
        assertEquals(Obj(trident_of_the_seas, 1), worn[3])
        assertEquals(Obj(abyssal_whip), bank[5])
        assertEquals(6, bank.occupiedSpace())
        assertEquals(1, worn.occupiedSpace())
    }

    @Test
    fun `swap stacked non-stackable obj in full inv into slot-taken non-stacking inv`() {
        val bank = Inventory(TransactionInventory.AlwaysStack, arrayOfNulls(5), placeholders = true)
        val worn = worn()
        bank[0] = Obj(red_partyhat)
        bank[1] = Obj(purple_sweets, 100)
        bank[2] = Obj(bronze_arrow, 50)
        bank[3] = Obj(trident_of_the_seas, 3)
        bank[4] = Obj(pickaxe_handle)
        worn[3] = Obj(abyssal_whip)
        val result = transaction {
            val bankInv = select(bank)
            val wornInv = select(worn)
            swap {
                from = bankInv
                into = wornInv
                fromSlot = 3
                intoSlot = 3
                merge = true
                strict = false
            }
        }
        assertEquals(TransactionResult.NotEnoughSpace, result.err)
        assertEquals(Obj(trident_of_the_seas, 3), bank[3])
        assertEquals(Obj(abyssal_whip), worn[3])
        assertEquals(5, bank.occupiedSpace())
        assertEquals(1, worn.occupiedSpace())
    }
}
