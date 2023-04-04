package org.rsmod.plugins.api.move

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.route.RouteRequestCoordinates
import org.rsmod.plugins.api.displace
import org.rsmod.plugins.api.net.info.ExtendedPlayerInfo
import org.rsmod.plugins.testing.GameTestState
import org.rsmod.plugins.testing.verify
import org.rsmod.plugins.testing.verifyNull

class MovementProcessTest {

    @Test
    fun GameTestState.testWaypoints() = runGameTest {
        val process = MovementProcess(playerList, routeFactory, stepFactory)
        val start = Coordinates(3194, 3199)
        val waypoints = listOf(
            start.translate(xOffset = 1, zOffset = 0),
            start.translate(xOffset = 1, zOffset = 1),
            start.translate(xOffset = 2, zOffset = 1),
            start.translate(xOffset = 2, zOffset = 2),
            start.translate(xOffset = 3, zOffset = 2)
        )
        withPlayer {
            coords = start
            movement.speed = MoveSpeed.Walk
            check(movement.isEmpty())
            movement += waypoints
            waypoints.forEachIndexed { index, waypoint ->
                assertEquals(waypoints.size - index, movement.size)
                process.execute()
                assertEquals(waypoint, coords)
                assertEquals(waypoints.size - (index + 1), movement.size)
            }
            assertTrue(movement.isEmpty())
        }
    }

    /**
     * When a player is "running" to a destination that's within one-tile of
     * distance - they will queue the walk "temp movement" extended-info.
     */
    @Test
    fun GameTestState.testOneTileRun() = runGameTest {
        val process = MovementProcess(playerList, routeFactory, stepFactory)
        val start = Coordinates(3200, 3200)
        /* verify running > 1 tile does _not_ queue extended-info update */
        withPlayer {
            coords = start
            routeRequest = RouteRequestCoordinates(
                destination = start.translateX(2),
                speed = null,
                async = true
            )
            movement.speed = MoveSpeed.Run
            verifyNull<ExtendedPlayerInfo.MoveSpeedTemp>()
            process.execute()
            verifyNull<ExtendedPlayerInfo.MoveSpeedTemp>()
            assertEquals(start.translateX(2), coords)
        }
        /* now verify running within one-tile distance */
        withPlayer {
            coords = start
            routeRequest = RouteRequestCoordinates(
                destination = start.translateX(1),
                speed = null,
                async = true
            )
            movement.speed = MoveSpeed.Run
            /* should not have pending "move speed temp" extended-info */
            verifyNull<ExtendedPlayerInfo.MoveSpeedTemp>()
            process.execute()
            verify<ExtendedPlayerInfo.MoveSpeedTemp> { it.type == WALK_INFO_TYPE }
            assertEquals(start.translateX(1), coords)
        }
    }

    @Test
    fun GameTestState.testLogInWalkEmulation() = runGameTest {
        val process = MovementProcess(playerList, routeFactory, stepFactory)
        val startCoords = Coordinates(3357, 3141)
        val destination = Coordinates(3384, 3155)
        withPlayer {
            coords = startCoords
            routeRequest = RouteRequestCoordinates(
                destination = destination,
                speed = null,
                async = true
            )
            // TODO: set perm speed to "run" as the mechanic _should_ force us to "walk"
            /* `lastStep` should be zero on log-in */
            check(movement.lastStep == Coordinates.ZERO)
            assertEquals(startCoords, coords)
            /* path blocked by a plant! */
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

    private companion object {

        private const val WALK_INFO_TYPE: Int = 1
    }
}
