package org.rsmod.objtx

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TransactionTest {
    @Test
    fun `fail to register same inventory`() {
        val inventory = inv()
        transaction {
            assertDoesNotThrow { select(inventory) }
            assertThrows<IllegalStateException> { select(inventory) }
        }
    }

    @Test
    fun `register inventories`() {
        val invInventory = inv()
        val invInventory2 = inv()
        val bankInventory = bank()
        val bankInventory2 = bank()
        transaction {
            assertDoesNotThrow { select(invInventory) }
            assertDoesNotThrow { select(invInventory2) }
            assertDoesNotThrow { select(bankInventory) }
            assertDoesNotThrow { select(bankInventory2) }
        }
    }
}
