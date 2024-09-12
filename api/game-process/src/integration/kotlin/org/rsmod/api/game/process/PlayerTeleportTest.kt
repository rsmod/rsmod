package org.rsmod.api.game.process

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.game.process.player.PlayerMovementProcessor
import org.rsmod.api.player.protect.protectedTeleport
import org.rsmod.api.testing.GameTestState
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.map.CoordGrid

// These tests are mainly to ensure that the teleport functions are doing what they are intended
// to be doing. They act more as documentation that the logic is made this way for emulation
// purposes.
class PlayerTeleportTest {
    @Test
    fun GameTestState.`teleport takes player to destination`() = runGameTest {
        withPlayer {
            val dest = CoordGrid(0, 50, 50, 9, 10)
            coords = CoordGrid(0, 50, 50, 10, 10)
            protectedTeleport(collision, dest)
            assertEquals(dest, coords)
        }
    }

    @Test
    fun GameTestState.`teleport flags player with walk movespeed`() = runGameTest {
        withPlayer {
            val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory)
            val dest = CoordGrid(0, 50, 50, 9, 10)
            coords = CoordGrid(0, 50, 50, 10, 10)
            protectedTeleport(collision, dest)
            assertEquals(dest, coords)
            assertEquals(MoveSpeed.Walk, moveSpeed)
            // After a cycle, the player will have their move speed set back to stationary.
            movement.process(this)
            assertEquals(MoveSpeed.Stationary, moveSpeed)
        }
    }

    @Test
    fun GameTestState.`telejump takes player to destination`() = runGameTest {
        withPlayer {
            val dest = CoordGrid(0, 50, 50, 9, 10)
            coords = CoordGrid(0, 50, 50, 10, 10)
            check(!isAccessProtected)
            var jumped = false
            withProtectedAccess {
                jumped = true
                telejump(dest, collision)
                assertEquals(dest, coords)
            }
            assertTrue(jumped)
        }
    }

    @Test
    fun GameTestState.`telejump flags player with stationary movespeed`() = runGameTest {
        withPlayer {
            val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory)
            val dest = CoordGrid(0, 50, 50, 9, 10)
            coords = CoordGrid(0, 50, 50, 10, 10)
            check(!isAccessProtected)
            var jumped = false
            withProtectedAccess {
                jumped = true
                telejump(dest, collision)
                assertEquals(dest, coords)
                assertEquals(MoveSpeed.Stationary, moveSpeed)
            }
            assertTrue(jumped)
            // After a cycle, the player will still have their move speed as stationary.
            movement.process(this)
            assertEquals(MoveSpeed.Stationary, moveSpeed)
        }
    }
}
