package org.rsmod.objtx

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InventoryTest {
    @Test
    fun `check occupied space only counts non-null objs`() {
        val inventory = Inventory(TransactionInventory.NormalStack, arrayOfNulls(28))
        assertTrue(inventory.isEmpty())
        inventory[0] = Obj(abyssal_whip)
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(1, inventory.occupiedSpace())
        inventory[2] = Obj(cert_abyssal_whip)
        assertEquals(Obj(cert_abyssal_whip), inventory[2])
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(2, inventory.occupiedSpace())
        inventory[2] = Obj(abyssal_whip)
        assertEquals(Obj(abyssal_whip), inventory[2])
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(2, inventory.occupiedSpace())
    }

    @Test
    fun `check free space only counts null objs`() {
        val inventory = Inventory(TransactionInventory.NormalStack, arrayOfNulls(28))
        assertTrue(inventory.isEmpty())
        inventory[0] = Obj(abyssal_whip)
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(inventory.size - 1, inventory.freeSpace())
        inventory[2] = Obj(cert_abyssal_whip)
        assertEquals(Obj(cert_abyssal_whip), inventory[2])
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(inventory.size - 2, inventory.freeSpace())
        inventory[2] = Obj(abyssal_whip)
        assertEquals(Obj(abyssal_whip), inventory[2])
        assertEquals(Obj(abyssal_whip), inventory[0])
        assertEquals(inventory.size - 2, inventory.freeSpace())
    }
}
