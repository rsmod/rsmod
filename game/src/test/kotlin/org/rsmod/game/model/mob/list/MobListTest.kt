package org.rsmod.game.model.mob.list

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.model.mob.Player
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MobListTest {

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test capacity property`(list: PlayerList, capacity: Int, indexPadding: Int) {
        Assertions.assertEquals(capacity, list.capacity)

        /* adding an element should not alter capacity */
        list[1] = createPlayer()
        Assertions.assertEquals(capacity, list.capacity)
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test indices property`(list: PlayerList, capacity: Int, indexPadding: Int) {
        val expected = 0 until capacity
        Assertions.assertEquals(expected, list.indices)
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test size property`(list: PlayerList, capacity: Int, indexPadding: Int) {
        Assertions.assertEquals(capacity, list.size)

        val index = list.nextAvailableIndex()
        Assertions.assertNotNull(index)
        list[index!!] = createPlayer()
        Assertions.assertEquals(capacity, list.size)

        list[index] = null
        Assertions.assertEquals(capacity, list.size)
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test isEmpty returns false if list contains a non-null element`(list: PlayerList, capacity: Int, indexPadding: Int) {
        Assertions.assertTrue(list.isEmpty())
        /* make sure kotlin std isNotEmpty extension also works properly */
        Assertions.assertFalse(list.isNotEmpty())

        list[1] = createPlayer()
        Assertions.assertFalse(list.isEmpty())
        Assertions.assertTrue(list.isNotEmpty())
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test isFull returns true only if list is full of non-null elements`(list: PlayerList, capacity: Int, indexPadding: Int) {
        Assertions.assertFalse(list.isFull())

        list[1] = createPlayer()
        Assertions.assertFalse(list.isFull())

        for (i in indexPadding until capacity - 1) {
            list[i] = createPlayer()
        }
        Assertions.assertFalse(list.isFull())

        list[capacity - 1] = createPlayer()
        Assertions.assertTrue(list.isFull())
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test next available index is null when list is full`(list: PlayerList, capacity: Int, indexPadding: Int) {
        Assertions.assertFalse(list.isFull())
        for (i in indexPadding until list.size) {
            list[i] = createPlayer()
        }
        Assertions.assertNull(list.nextAvailableIndex())
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test available indexes OSRS emulation`(list: PlayerList, capacity: Int, indexPadding: Int) {
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
        list[capacity - 1] = createPlayer()
        val sixthIndex = list.nextAvailableIndex()
        Assertions.assertEquals(indexPadding, sixthIndex)
    }

    @ParameterizedTest
    @ArgumentsSource(ListProvider::class)
    fun `test element added to list matches pointer`(list: PlayerList, capacity: Int, indexPadding: Int) {
        val mob = createPlayer()
        val mob2 = createPlayer()
        list[1] = mob
        Assertions.assertSame(mob, list[1])
        Assertions.assertNotSame(mob2, list[1])
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
