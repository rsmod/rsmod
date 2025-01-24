package org.rsmod.game.inv

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.UnpackedInvType
import org.rsmod.game.type.util.UncheckedType

@OptIn(UncheckedType::class)
class InventoryCompactTest {
    @Test
    fun `compact inventory with gaps`() {
        val type = createInvType(size = 5)
        val inventory = Inventory(type, arrayOfNulls(type.size))
        inventory[0] = InvObj(1, 1)
        inventory[2] = InvObj(2, 1)
        inventory[4] = InvObj(3, 1)

        inventory.compact()

        assertEquals(InvObj(1, 1), inventory[0])
        assertEquals(InvObj(2, 1), inventory[1])
        assertEquals(InvObj(3, 1), inventory[2])
        assertNull(inventory[3])
        assertNull(inventory[4])

        assertTrue(inventory.modifiedSlots.get(0))
        assertTrue(inventory.modifiedSlots.get(1))
        assertTrue(inventory.modifiedSlots.get(2))
        assertFalse(inventory.modifiedSlots.get(3))
        assertTrue(inventory.modifiedSlots.get(4))
    }

    @Test
    fun `compact empty inventory`() {
        val type = createInvType(size = 5)
        val inventory = Inventory(type, arrayOfNulls(type.size))

        inventory.compact()

        assertTrue(inventory.isEmpty())
        assertTrue(inventory.modifiedSlots.isEmpty)
    }

    @Test
    fun `compact full inventory`() {
        val type = createInvType(size = 5)
        val inventory = Inventory(type, Array(type.size) { InvObj(it + 1, 1) })

        inventory.compact()

        for (i in inventory.indices) {
            assertNotNull(inventory[i])
            assertEquals(InvObj(i + 1, 1), inventory[i])
        }

        assertTrue(inventory.modifiedSlots.isEmpty)
    }

    @Test
    fun `compact single obj`() {
        val type = createInvType(size = 5)
        val inventory = Inventory(type, arrayOfNulls(type.size))
        inventory[2] = InvObj(1, 1)

        inventory.compact()

        assertEquals(InvObj(1, 1), inventory[0])
        for (i in 1 until inventory.size) {
            assertNull(inventory[i])
        }

        assertTrue(inventory.modifiedSlots.get(0))
        assertTrue(inventory.modifiedSlots.get(2))
        assertFalse(inventory.modifiedSlots.get(1))
        assertFalse(inventory.modifiedSlots.get(3))
    }

    @Test
    fun `compact inventory that is already compacted`() {
        val type = createInvType(size = 5)
        val inventory = Inventory(type, arrayOf(InvObj(1, 1), InvObj(2, 1), null, null, null))

        inventory.compact()

        assertEquals(InvObj(1, 1), inventory[0])
        assertEquals(InvObj(2, 1), inventory[1])
        for (i in 2 until inventory.size) {
            assertNull(inventory[i])
        }

        assertTrue(inventory.modifiedSlots.isEmpty)
    }

    @Suppress("SameParameterValue")
    private fun createInvType(size: Int): UnpackedInvType {
        val builder = InvTypeBuilder("test_inv").apply { this.size = size }
        return builder.build(-1)
    }
}
