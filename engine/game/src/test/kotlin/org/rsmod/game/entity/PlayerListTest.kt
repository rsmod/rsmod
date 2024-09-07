package org.rsmod.game.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class PlayerListTest {
    @Test
    fun `next free slot returns null when full`() {
        val list = PlayerList()
        for (i in list.slotPadding until list.capacity) {
            list[i] = createPlayer()
        }
        assertNull(list.nextFreeSlot())
    }

    @Test
    fun `round-robin free slot emulation`() {
        val list = PlayerList()
        val firstSlot = checkNotNull(list.nextFreeSlot())
        assertEquals(1, firstSlot)
        list[firstSlot] = createPlayer()
        assertNotNull(list[firstSlot])
        list.remove(firstSlot)
        list[list.capacity - 1] = createPlayer()
        val roundRobinSlot = checkNotNull(list.nextFreeSlot())
        assertEquals(list.slotPadding, roundRobinSlot)
    }

    @Test
    fun `ensure pointer validity`() {
        val list = PlayerList()
        val player1 = createPlayer()
        val player2 = createPlayer()
        list[1] = player1
        assertSame(player1, list[1])
        assertNotSame(player2, list[1])
        list[2] = player2
        assertNotSame(player1, list[2])
        assertSame(player2, list[2])
        assertSame(player1, list[1])
        assertNotSame(player2, list[1])
    }

    private fun createPlayer(): Player = Player()
}
