package org.rsmod.game.pathfinder.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_EAST
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_NORTH
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_SOUTH
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_WEST
import org.rsmod.game.pathfinder.util.RotationUtils.rotate

class RotationUtilsTest {

    @Test
    fun testRotateWidth() {
        val (width, height) = 3 to 2
        assertEquals(width, rotate(objectRot = 0, width, height))
        assertEquals(height, rotate(objectRot = 1, width, height))
        assertEquals(width, rotate(objectRot = 2, width, height))
        assertEquals(height, rotate(objectRot = 3, width, height))
    }

    @Test
    fun testRotateHeight() {
        val (width, height) = 3 to 2
        assertEquals(height, rotate(objectRot = 0, height, width))
        assertEquals(width, rotate(objectRot = 1, height, width))
        assertEquals(height, rotate(objectRot = 2, height, width))
        assertEquals(width, rotate(objectRot = 3, height, width))
    }

    @Test
    fun testRotateBlockAccessFlagNorth() {
        assertEquals(BLOCK_NORTH, rotate(objectRot = 0, BLOCK_NORTH))
        assertEquals(BLOCK_EAST, rotate(objectRot = 1, BLOCK_NORTH))
        assertEquals(BLOCK_SOUTH, rotate(objectRot = 2, BLOCK_NORTH))
        assertEquals(BLOCK_WEST, rotate(objectRot = 3, BLOCK_NORTH))
    }

    @Test
    fun testRotateBlockAccessFlagEast() {
        assertEquals(BLOCK_EAST, rotate(objectRot = 0, BLOCK_EAST))
        assertEquals(BLOCK_SOUTH, rotate(objectRot = 1, BLOCK_EAST))
        assertEquals(BLOCK_WEST, rotate(objectRot = 2, BLOCK_EAST))
        assertEquals(BLOCK_NORTH, rotate(objectRot = 3, BLOCK_EAST))
    }

    @Test
    fun testRotateBlockAccessFlagSouth() {
        assertEquals(BLOCK_SOUTH, rotate(objectRot = 0, BLOCK_SOUTH))
        assertEquals(BLOCK_WEST, rotate(objectRot = 1, BLOCK_SOUTH))
        assertEquals(BLOCK_NORTH, rotate(objectRot = 2, BLOCK_SOUTH))
        assertEquals(BLOCK_EAST, rotate(objectRot = 3, BLOCK_SOUTH))
    }

    @Test
    fun testRotateBlockAccessFlagWest() {
        assertEquals(BLOCK_WEST, rotate(objectRot = 0, BLOCK_WEST))
        assertEquals(BLOCK_NORTH, rotate(objectRot = 1, BLOCK_WEST))
        assertEquals(BLOCK_EAST, rotate(objectRot = 2, BLOCK_WEST))
        assertEquals(BLOCK_SOUTH, rotate(objectRot = 3, BLOCK_WEST))
    }
}
