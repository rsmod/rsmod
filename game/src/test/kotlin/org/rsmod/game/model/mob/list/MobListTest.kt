package org.rsmod.game.model.mob.list

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.model.mob.Player

private const val LIST_CAPACITY = 2047
private const val INDEX_PADDING = 0

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MobListTest {

    private val list = PlayerList(LIST_CAPACITY, INDEX_PADDING)

    @Test
    fun `test capacity property`() {
        Assertions.assertEquals(LIST_CAPACITY, list.capacity)

        /* adding an element should not alter capacity */
        list[1] = createPlayer()
        Assertions.assertEquals(LIST_CAPACITY, list.capacity)
    }

    @Test
    fun `test indices property`() {
        val expected = 0 until LIST_CAPACITY
        Assertions.assertEquals(expected, list.indices)
    }

    @Test
    fun `test size property`() {
        Assertions.assertEquals(LIST_CAPACITY, list.size)

        val index = list.nextAvailableIndex()
        Assertions.assertNotNull(index)
        list[index!!] = createPlayer()
        Assertions.assertEquals(LIST_CAPACITY, list.size)

        list[index] = null
        Assertions.assertEquals(LIST_CAPACITY, list.size)
    }

    @Test
    fun `test isEmpty returns false if list contains a non-null element`() {
        Assertions.assertTrue(list.isEmpty())
        /* make sure kotlin std isNotEmpty extension also works properly */
        Assertions.assertFalse(list.isNotEmpty())

        list[1] = createPlayer()
        Assertions.assertFalse(list.isEmpty())
        Assertions.assertTrue(list.isNotEmpty())
    }

    @Test
    fun `test isFull returns true only if list is full of non-null elements`() {
        Assertions.assertFalse(list.isFull())

        list[1] = createPlayer()
        Assertions.assertFalse(list.isFull())

        for (i in INDEX_PADDING until LIST_CAPACITY - 1) {
            list[i] = createPlayer()
        }
        Assertions.assertFalse(list.isFull())

        list[LIST_CAPACITY - 1] = createPlayer()
        Assertions.assertTrue(list.isFull())
    }

    @Test
    fun `test next available index is null when list is full`() {
        Assertions.assertFalse(list.isFull())
        for (i in INDEX_PADDING until list.size) {
            list[i] = createPlayer()
        }
        Assertions.assertNull(list.nextAvailableIndex())
    }

    @Test
    fun `test available indexes OSRS emulation`() {
        val firstIndex = list.nextAvailableIndex()
        /* first ever available index should be 1 */
        Assertions.assertEquals(1, firstIndex)

        list[firstIndex!!] = createPlayer()
        val secondIndex = list.nextAvailableIndex()
        Assertions.assertEquals(2, secondIndex)

        list[secondIndex!!] = createPlayer()
        val thirdIndex = list.nextAvailableIndex()
        Assertions.assertEquals(3, thirdIndex)

        /*
         * Next available indexes shouldn't rely on elements being null or not
         */
        list[firstIndex] = null
        list[secondIndex] = null

        list[thirdIndex!!] = createPlayer()
        list[thirdIndex] = null
        val fourthIndex = list.nextAvailableIndex()
        Assertions.assertEquals(4, fourthIndex)

        list[fourthIndex!!] = createPlayer()
        list[fourthIndex] = null
        val fifthIndex = list.nextAvailableIndex()
        Assertions.assertEquals(5, fifthIndex)

        /*
         * After last index (capacity) has been used, the next available index
         * should start from initial index padding.
         */
        list[LIST_CAPACITY - 1] = createPlayer()
        val sixthIndex = list.nextAvailableIndex()
        Assertions.assertEquals(INDEX_PADDING, sixthIndex)
    }

    @Test
    fun `test element added to list matches pointer`() {
        val mob = createPlayer()
        val mob2 = createPlayer()
        list[1] = mob
        Assertions.assertSame(mob, list[1])
        Assertions.assertNotSame(mob2, list[1])
    }

    private fun createPlayer(): Player = Player(PlayerEntity.ZERO)
}
