package org.rsmod.game.model.item.container.transaction

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.item.type.ItemTypeBuilder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemContainerTransactionTests {

    @Test
    fun addStrictExactScatter() {
        val container = inventory()
        val item = item("abyssal_whip", 4151, container.size)

        val result = container.add(item, strict = true)
        repeat(item.amount) {
            Assertions.assertEquals(item.id, container[it]?.id)
        }
        Assertions.assertTrue(container.isFull())
        Assertions.assertTrue(result.success)
    }

    @Test
    fun addStrictTooManyScatter() {
        val container = inventory()
        val item = item("abyssal_whip", 4151, container.size + 1)

        val result = container.add(item, strict = true)
        container.forEach {
            Assertions.assertNull(it)
        }
        Assertions.assertTrue(container.isEmpty())
        Assertions.assertFalse(result.partial)
        Assertions.assertTrue(result.failure)
    }

    @Test
    fun addStrictExactStackable() {
        val container = inventory()
        val item = item("coins", 995, Int.MAX_VALUE, stacks = true)

        val result = container.add(item, strict = true)
        Assertions.assertNotNull(container.first())
        Assertions.assertEquals(item.amount, container.first()?.amount)
        Assertions.assertEquals(item.amount, container.amount(item))
        Assertions.assertEquals(item.amount, result.completed)
        Assertions.assertTrue(result.success)
    }

    @Test
    fun addStrictTooManyStackable() {
        val container = inventory()
        val item = item("coins", 995, Int.MAX_VALUE - 64, stacks = true)

        container.add(item, strict = true)
        Assertions.assertNotNull(container.first())
        Assertions.assertEquals(item.amount, container.first()?.amount)

        val result = container.add(item, strict = true)
        Assertions.assertNotNull(container.first())
        Assertions.assertEquals(1, container.occupied())
        Assertions.assertEquals(item.amount, container.first()?.amount)

        Assertions.assertFalse(result.partial)
        Assertions.assertTrue(result.failure)
    }

    @Test
    fun addLenientExact() {
        val container = inventory()
        val item = item("abyssal_whip", 4151, 2)

        val result = container.add(item, strict = false)
        Assertions.assertNotNull(container[0])
        Assertions.assertNotNull(container[1])
        Assertions.assertEquals(1, container[0]?.amount)
        Assertions.assertEquals(1, container[1]?.amount)
        Assertions.assertTrue(result.success)
        Assertions.assertEquals(item.amount, result.completed)
    }

    @Test
    fun addLenientTooMany() {
        val container = inventory()
        val item = item("abyssal_whip", 4151, container.size + 1)

        val result = container.add(item, strict = false)
        container.forEach {
            Assertions.assertNotNull(it)
            Assertions.assertEquals(1, it?.amount)
        }
        Assertions.assertTrue(container.isFull())
        Assertions.assertTrue(result.partial)
        Assertions.assertTrue(result.failure)
        Assertions.assertEquals(container.size, result.completed)
    }

    @Test
    fun addLenientExactStackable() {
        val container = inventory()
        val item = item("coins", 995, Int.MAX_VALUE, stacks = true)

        val result = container.add(item, strict = false)
        Assertions.assertNotNull(container.first())
        Assertions.assertEquals(item.amount, container.first()?.amount)
        Assertions.assertTrue(result.success)
        Assertions.assertEquals(item.amount, result.completed)
    }

    @Test
    fun addLenientTooManyStackable() {
        val container = inventory()
        val item = item("coins", 995, Int.MAX_VALUE, stacks = true)

        container.add(item, strict = false)
        Assertions.assertNotNull(container.first())
        Assertions.assertEquals(item.amount, container.first()?.amount)

        val result = container.add(item, strict = false)
        Assertions.assertNotNull(container.first())
        Assertions.assertEquals(1, container.occupied())
        Assertions.assertEquals(item.amount, container.first()?.amount)

        Assertions.assertTrue(result.failure)
        Assertions.assertFalse(result.partial)
        Assertions.assertFalse(result.success)
    }

    @Test
    fun addToExistingStack() {
        val container = inventory()
        val item = item("coins", 995, 2000, stacks = true)

        container.add(item, strict = false)
        Assertions.assertNotNull(container.first())
        Assertions.assertEquals(item.amount, container.first()?.amount)

        val result = container.add(item, strict = false)
        Assertions.assertNotNull(container.first())
        Assertions.assertEquals(1, container.occupied())
        Assertions.assertEquals(item.amount * 2, container.first()?.amount)

        Assertions.assertFalse(result.failure)
        Assertions.assertTrue(result.success)
    }

    @Test
    fun removeStrictExact() {
        val container = inventory()
        val item = item("abyssal_whip", 4151, 2)

        container[1] = item("abyssal_whip", item.id)
        container[4] = item("abyssal_whip", item.id)

        val result = container.remove(item, strict = true)
        Assertions.assertNull(container[1])
        Assertions.assertNull(container[4])
        Assertions.assertTrue(result.success)
    }

    @Test
    fun removeStrictTooMany() {
        val container = inventory()
        val item = item("abyssal_whip", 4151, 3)

        container[1] = item("abyssal_whip", item.id)
        container[4] = item("abyssal_whip", item.id)

        val result = container.remove(item, strict = true)
        Assertions.assertNotNull(container[1])
        Assertions.assertNotNull(container[4])
        Assertions.assertFalse(result.partial)
        Assertions.assertTrue(result.failure)
        Assertions.assertEquals(0, result.completed)
    }

    @Test
    fun removeStrictExactStackable() {
        val container = inventory()
        val item = item("coins", 995, 1000)

        container[2] = item("coins", item.id, item.amount)

        val result = container.remove(item, strict = true)
        Assertions.assertNull(container[2])
        Assertions.assertTrue(result.success)
    }

    @Test
    fun removeStrictTooManyStackable() {
        val container = inventory()
        val item = item("coins", 995, 1000)

        container[2] = item("coins", item.id, item.amount - 1)

        val result = container.remove(item, strict = true)
        Assertions.assertEquals(item.amount - 1, container[2]?.amount)
        Assertions.assertFalse(result.partial)
        Assertions.assertTrue(result.failure)
    }

    @Test
    fun removeLenientExact() {
        val container = inventory()
        val item = item("abyssal_whip", 4151, 2)

        container[1] = item("abyssal_whip", item.id)
        container[4] = item("abyssal_whip", item.id)

        val result = container.remove(item, strict = false)
        Assertions.assertNull(container[1])
        Assertions.assertNull(container[4])
        Assertions.assertTrue(result.success)
        Assertions.assertEquals(item.amount, result.completed)
    }

    @Test
    fun removeLenientTooMany() {
        val container = inventory()
        val item = item("abyssal_whip", 4151, 3)

        container[1] = item("abyssal_whip", item.id)
        container[4] = item("abyssal_whip", item.id)

        val result = container.remove(item, strict = false)
        Assertions.assertNull(container[1])
        Assertions.assertNull(container[4])
        Assertions.assertTrue(result.partial)
        Assertions.assertTrue(result.failure)
        Assertions.assertEquals(2, result.completed)
    }

    @Test
    fun removeLenientExactStackable() {
        val container = inventory()
        val item = item("coins", 995, 1000)

        container[2] = item("coins", item.id, item.amount)

        val result = container.remove(item, strict = false)
        Assertions.assertNull(container[2])
        Assertions.assertTrue(result.partial)
        Assertions.assertTrue(result.success)
        Assertions.assertEquals(item.amount, result.completed)
    }

    @Test
    fun removeLenientTooManyStackable() {
        val container = inventory()
        val item = item("coins", 995, 1000)

        container[2] = item("coins", item.id, item.amount - 1)

        val result = container.remove(item, strict = false)
        Assertions.assertNull(container[2])
        Assertions.assertTrue(result.failure)
        Assertions.assertTrue(result.partial)
        Assertions.assertFalse(result.success)
        Assertions.assertEquals(item.amount - 1, result.completed)
    }
}

private fun item(name: String, id: Int, amount: Int = 1, stacks: Boolean = false): Item {
    val type = ItemTypeBuilder().apply {
        this.id = id
        this.name = name
        this.stacks = stacks
    }
    return Item(type.build(), amount)
}

private fun inventory() = ItemContainer(28)
