package org.rsmod.game.model.mob.list

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.model.mob.Player

private const val LIST_CAPACITY = 2047

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MobListTest {

    private val list = PlayerList(LIST_CAPACITY)

    @Test
    fun `test capacity property`() {
        Assertions.assertEquals(LIST_CAPACITY, list.capacity)

        /* adding an element should not alter capacity */
        list[1] = createPlayer()
        Assertions.assertEquals(LIST_CAPACITY, list.capacity)
    }

    @Test
    fun `test indices property`() {
        val expected = MobList.INDEX_PADDING until LIST_CAPACITY
        Assertions.assertEquals(expected, list.indices)
    }

    @Test
    fun `test size property`() {
        Assertions.assertEquals(0, list.size)

        val index = list.indexOfFirstNullOrNull()
        Assertions.assertNotNull(index)
        list[index!!] = createPlayer()
        Assertions.assertEquals(1, list.size)

        list[index] = null
        Assertions.assertEquals(0, list.size)
    }

    @Test
    fun `test isEmpty returns true only if list contains a non-null element`() {
        Assertions.assertEquals(true, list.isEmpty())
        /* make sure kotlin std isNotEmpty extension also work properly */
        Assertions.assertEquals(false, list.isNotEmpty())

        list[1] = createPlayer()
        Assertions.assertEquals(false, list.isEmpty())
        Assertions.assertEquals(true, list.isNotEmpty())
    }

    @Test
    fun `test free indexes`() {
        val index = list.indexOfFirstNullOrNull()
        Assertions.assertNotNull(index)
        Assertions.assertEquals(MobList.INDEX_PADDING, index)

        list[index!!] = createPlayer()
        val nextIndex = list.indexOfFirstNullOrNull()
        Assertions.assertNotNull(nextIndex)
        Assertions.assertEquals(MobList.INDEX_PADDING + 1, nextIndex)

        list[index] = null
        val recalculatedIndex = list.indexOfFirstNullOrNull()
        Assertions.assertNotNull(recalculatedIndex)
        Assertions.assertEquals(MobList.INDEX_PADDING, recalculatedIndex)
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
