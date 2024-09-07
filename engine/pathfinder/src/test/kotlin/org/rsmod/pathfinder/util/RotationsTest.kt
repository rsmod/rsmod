package org.rsmod.pathfinder.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.pathfinder.flag.BlockAccessFlag.EAST
import org.rsmod.pathfinder.flag.BlockAccessFlag.NORTH
import org.rsmod.pathfinder.flag.BlockAccessFlag.SOUTH
import org.rsmod.pathfinder.flag.BlockAccessFlag.WEST
import org.rsmod.pathfinder.util.Rotations.rotate

class RotationsTest {
    @Test
    fun `rotate loc width`() {
        val (width, length) = 3 to 2
        assertEquals(width, rotate(angle = 0, width, length))
        assertEquals(length, rotate(angle = 1, width, length))
        assertEquals(width, rotate(angle = 2, width, length))
        assertEquals(length, rotate(angle = 3, width, length))
    }

    @Test
    fun `rotate loc length`() {
        val (width, length) = 3 to 2
        assertEquals(length, rotate(angle = 0, length, width))
        assertEquals(width, rotate(angle = 1, length, width))
        assertEquals(length, rotate(angle = 2, length, width))
        assertEquals(width, rotate(angle = 3, length, width))
    }

    @Test
    fun `rotate north block-access flag`() {
        assertEquals(NORTH, rotate(angle = 0, NORTH))
        assertEquals(EAST, rotate(angle = 1, NORTH))
        assertEquals(SOUTH, rotate(angle = 2, NORTH))
        assertEquals(WEST, rotate(angle = 3, NORTH))
    }

    @Test
    fun `rotate east block-access flag`() {
        assertEquals(EAST, rotate(angle = 0, EAST))
        assertEquals(SOUTH, rotate(angle = 1, EAST))
        assertEquals(WEST, rotate(angle = 2, EAST))
        assertEquals(NORTH, rotate(angle = 3, EAST))
    }

    @Test
    fun `rotate south block-access flag`() {
        assertEquals(SOUTH, rotate(angle = 0, SOUTH))
        assertEquals(WEST, rotate(angle = 1, SOUTH))
        assertEquals(NORTH, rotate(angle = 2, SOUTH))
        assertEquals(EAST, rotate(angle = 3, SOUTH))
    }

    @Test
    fun `rotate west block-access flag`() {
        assertEquals(WEST, rotate(angle = 0, WEST))
        assertEquals(NORTH, rotate(angle = 1, WEST))
        assertEquals(EAST, rotate(angle = 2, WEST))
        assertEquals(SOUTH, rotate(angle = 3, WEST))
    }
}
