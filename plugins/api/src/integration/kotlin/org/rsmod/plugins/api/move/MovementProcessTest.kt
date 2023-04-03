package org.rsmod.plugins.api.move

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.route.RouteRequestCoordinates
import org.rsmod.plugins.api.displace
import org.rsmod.plugins.testing.GameTestState

class MovementProcessTest {

    @Test
    fun GameTestState.testLogInWalkEmulation() = runGameTest {
        val process = MovementProcess(playerList, routeFactory, stepFactory)
        val startCoords = Coordinates(3357, 3141)
        val destination = Coordinates(3384, 3155)
        withPlayer {
            coords = startCoords
            routeRequest = RouteRequestCoordinates(
                destination = destination,
                speed = MoveSpeed.Walk,
                async = true
            )
            /* `lastStep` should be zero on log-in */
            check(movement.lastStep == Coordinates.ZERO)
            assertEquals(startCoords, coords)
            /* path blocked by a cactus! */
            val expectedDest = Coordinates(3275, 3059)
            val expectedSteps = 82
            repeat(expectedSteps) {
                process.execute()
            }
            assertEquals(expectedDest, coords)
            /*
             * Though the next tile in the path is blocked, the
             * destination waypoint should not be cleared until
             * properly "interrupted," or reached.
             */
            repeat(8) {
                assertEquals(expectedDest, coords)
                assertFalse(movement.isEmpty())
                process.execute()
            }
        }
        /* mechanic should only be valid on first ever step after log-in */
        withPlayer {
            coords = startCoords
            routeRequest = RouteRequestCoordinates(
                destination = destination,
                speed = MoveSpeed.Walk,
                async = true
            )
            /* set `lastStep` to a valid coordinate */
            movement.lastStep = coords
            assertEquals(startCoords, coords)
            repeat(8) {
                process.execute()
                assertTrue(movement.isEmpty())
            }
            /* player should not of have moved */
            assertEquals(startCoords, coords)
        }
        withPlayer {
            coords = startCoords
            routeRequest = RouteRequestCoordinates(
                destination = destination,
                speed = MoveSpeed.Walk,
                async = true
            )
            /* calling `displace` should also set `lastStep` */
            displace(startCoords)
            assertEquals(startCoords, coords)
            repeat(8) {
                process.execute()
                assertTrue(movement.isEmpty())
            }
            /* player should not of have moved */
            assertEquals(startCoords, coords)
        }
    }
}
