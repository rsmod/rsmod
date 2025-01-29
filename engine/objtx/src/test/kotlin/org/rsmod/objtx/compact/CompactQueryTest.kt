package org.rsmod.objtx.compact

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.objtx.Inventory
import org.rsmod.objtx.Obj
import org.rsmod.objtx.TransactionInventory
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class CompactQueryTest {
    @Test
    fun `compact inventory with gaps`() {
        val inventory = createInventory(size = 5)
        inventory.objs[0] = Obj(1, 1)
        inventory.objs[2] = Obj(2, 1)
        inventory.objs[4] = Obj(3, 1)

        val transaction = transaction {
            val inv = select(inventory)
            compact { this.from = inv }
        }
        val result = transaction[0]

        val expected = arrayOf(Obj(1, 1), Obj(2, 1), Obj(3, 1), null, null)

        assertInstanceOf<TransactionResult.Ok>(result)
        assertEquals(expected.toList(), inventory.objs.toList())
    }

    @Test
    fun `compact empty inventory`() {
        val inventory = createInventory(size = 5)
        val transaction = transaction {
            val inv = select(inventory)
            compact { this.from = inv }
        }
        val result = transaction[0]
        assertInstanceOf<TransactionResult.Ok>(result)
        assertEquals(0, inventory.occupiedSpace())
    }

    @Test
    fun `compact full inventory`() {
        val inventory = createInventory(size = 5)
        for (i in inventory.indices) {
            inventory.objs[i] = Obj(i + 1, 1)
        }

        val transaction = transaction {
            val inv = select(inventory)
            compact { this.from = inv }
        }
        val result = transaction[0]

        val expected = arrayOf(Obj(1, 1), Obj(2, 1), Obj(3, 1), Obj(4, 1), Obj(5, 1))

        assertInstanceOf<TransactionResult.Ok>(result)
        assertEquals(expected.toList(), inventory.objs.toList())
    }

    @Test
    fun `compact single obj`() {
        val inventory = createInventory(size = 5)
        inventory.objs[2] = Obj(1, 1)

        val transaction = transaction {
            val inv = select(inventory)
            compact { this.from = inv }
        }
        val result = transaction[0]

        val expected = arrayOf(Obj(1, 1), null, null, null, null)

        assertInstanceOf<TransactionResult.Ok>(result)
        assertEquals(expected.toList(), inventory.objs.toList())
    }

    @Test
    fun `compact inventory that is already compacted`() {
        val inventory = createInventory(size = 5)
        inventory.objs[0] = Obj(1, 1)
        inventory.objs[1] = Obj(2, 1)

        val transaction = transaction {
            val inv = select(inventory)
            compact { this.from = inv }
        }
        val result = transaction[0]

        val expected = arrayOf(Obj(1, 1), Obj(2, 1), null, null, null)

        assertInstanceOf<TransactionResult.Ok>(result)
        assertEquals(expected.toList(), inventory.objs.toList())
    }
}

private fun createInventory(@Suppress("SameParameterValue") size: Int): Inventory =
    Inventory(TransactionInventory.NormalStack, arrayOfNulls(size))
