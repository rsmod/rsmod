package org.rsmod.game.model.mob.list

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.model.mob.Player
import java.util.stream.Stream

@Suppress("UNUSED_PARAMETER")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MobListTest {

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test capacity property`(list: PlayerList, capacity: Int, indexPadding: Int) {
        assertEquals(capacity, list.capacity)

        /* adding an element should not alter capacity */
        list[1] = createPlayer()
        assertEquals(capacity, list.capacity)
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test indices property`(list: PlayerList, capacity: Int, indexPadding: Int) {
        val expected = 0 until capacity
        assertEquals(expected, list.indices)
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test size property`(list: PlayerList, capacity: Int, indexPadding: Int) {
        assertEquals(capacity, list.size)

        val index = list.nextAvailableIndex()
        assertNotNull(index)
        list[index!!] = createPlayer()
        assertEquals(capacity, list.size)

        list[index] = null
        assertEquals(capacity, list.size)
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun testCountNotNull(list: PlayerList, capacity: Int, indexPadding: Int) {
        require(list.size == capacity)
        assertEquals(0, list.countNotNull())

        val index = list.nextAvailableIndex()
        assertNotNull(index)
        list[index!!] = createPlayer()
        assertEquals(1, list.countNotNull())

        list[index] = null
        assertEquals(0, list.countNotNull())
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test isEmpty returns false if list contains a non-null element`(
        list: PlayerList,
        capacity: Int,
        indexPadding: Int
    ) {
        assertTrue(list.isEmpty())
        /* make sure kotlin std isNotEmpty extension also works properly */
        assertFalse(list.isNotEmpty())

        list[1] = createPlayer()
        assertFalse(list.isEmpty())
        assertTrue(list.isNotEmpty())
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test isFull returns true only if list is full of non-null elements`(
        list: PlayerList,
        capacity: Int,
        indexPadding: Int
    ) {
        assertFalse(list.isFull())

        list[1] = createPlayer()
        assertFalse(list.isFull())

        for (i in indexPadding until capacity - 1) {
            list[i] = createPlayer()
        }
        assertFalse(list.isFull())

        list[capacity - 1] = createPlayer()
        assertTrue(list.isFull())
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test next available index is null when list is full`(list: PlayerList, capacity: Int, indexPadding: Int) {
        assertFalse(list.isFull())
        for (i in indexPadding until list.size) {
            list[i] = createPlayer()
        }
        assertNull(list.nextAvailableIndex())
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test available indexes OSRS emulation`(list: PlayerList, capacity: Int, indexPadding: Int) {
        val firstIndex = list.nextAvailableIndex()
        /* first ever available index should be 1 */
        assertEquals(1, firstIndex)

        list[firstIndex!!] = createPlayer()
        val secondIndex = list.nextAvailableIndex()
        assertEquals(2, secondIndex)

        list[secondIndex!!] = createPlayer()
        val thirdIndex = list.nextAvailableIndex()
        assertEquals(3, thirdIndex)

        /*
         * Next available indexes shouldn't rely on elements being null or not
         */
        list[firstIndex] = null
        list[secondIndex] = null

        list[thirdIndex!!] = createPlayer()
        list[thirdIndex] = null
        val fourthIndex = list.nextAvailableIndex()
        assertEquals(4, fourthIndex)

        list[fourthIndex!!] = createPlayer()
        list[fourthIndex] = null
        val fifthIndex = list.nextAvailableIndex()
        assertEquals(5, fifthIndex)

        /*
         * After last index (capacity) has been used, the next available index
         * should start from initial index padding.
         */
        list[capacity - 1] = createPlayer()
        val sixthIndex = list.nextAvailableIndex()
        assertEquals(indexPadding, sixthIndex)
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test element added to list matches pointer`(list: PlayerList, capacity: Int, indexPadding: Int) {
        val mob = createPlayer()
        val mob2 = createPlayer()
        list[1] = mob
        assertSame(mob, list[1])
        assertNotSame(mob2, list[1])
    }

    private fun createPlayer(): Player = Player(PlayerEntity.ZERO)

    private object ListProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(PlayerList(2047, 0), 2047, 0),
                Arguments.of(PlayerList(2047, 1), 2047, 1)
            )
        }
    }
}
