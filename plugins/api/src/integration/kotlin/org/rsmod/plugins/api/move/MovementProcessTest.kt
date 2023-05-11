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
import org.rsmod.plugins.testing.assertions.verify
import org.rsmod.plugins.testing.assertions.verifyNull

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
     * Waypoint in player's movement queue should not be removed until they
     * have fully reached it.
     *
     * There was a scenario where after reaching a waypoint, the next one was
     * instantly consumed and removed from the movement queue. If the final
     * destination was X tiles away from said waypoint initial coordinates,
     * then the rest of the route's tiles were never processed.
     *
     * X = further away than tiles the player could process in that single game cycle.
     */
    @Test
    fun GameTestState.testPrematureWaypointRemoval() = runGameTest {
        val process = MovementProcess(playerList, routeFactory, stepFactory)
        // North of lumbridge fountain.
        val start = Coordinates(3221, 3228)
        val dest = Coordinates(3220, 3226)
        val waypoints = listOf(
            start.translate(xOffset = -1, zOffset = 0),
            dest
        )
        withPlayer {
            coords = start
            movement.speed = MoveSpeed.Run
            check(movement.isEmpty())
            movement += waypoints
            repeat(waypoints.size) {
                process.execute()
            }
            assertEquals(dest, coords)
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
                speed = MoveSpeed.Walk,
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
                process.execute()
                assertEquals(expectedDest, coords)
                assertFalse(movement.isEmpty())
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

    @Test
    fun GameTestState.testMoveSpeeds() = runGameTest {
        // Utilize a new collision state so that collision flags do not
        // interfere with test.
        withCollisionState {
            val startCoords = Coordinates(3200, 3200, 0)
            // Allocate an empty zone with no collision flags.
            it.collision.allocateIfAbsent(startCoords.x, startCoords.z, startCoords.level)
            val process = MovementProcess(playerList, it.routeFactory, it.stepFactory)
            MoveSpeed.values.forEach { speed ->
                // If we want to support more than 7 steps, allocate
                // neighbouring zones.
                check(speed.steps < 8)
                withPlayer {
                    val expectedCoords = startCoords.translateZ(speed.steps)
                    coords = startCoords
                    movement.speed = speed
                    check(movement.isEmpty())
                    movement += coords.translateZ(7)
                    process.execute()
                    assertEquals(expectedCoords, coords) {
                        "Unexpected end coordinates for speed $speed"
                    }
                }
                withPlayer {
                    val expectedCoords = startCoords.translateX(speed.steps)
                    coords = startCoords
                    movement.speed = speed
                    check(movement.isEmpty())
                    movement += coords.translateX(7)
                    process.execute()
                    assertEquals(expectedCoords, coords) {
                        "Unexpected end coordinates for speed $speed"
                    }
                }
                withPlayer {
                    val expectedCoords = startCoords.translate(speed.steps, speed.steps)
                    coords = startCoords
                    movement.speed = speed
                    check(movement.isEmpty())
                    movement += coords.translate(7, 7)
                    process.execute()
                    assertEquals(expectedCoords, coords) {
                        "Unexpected end coordinates for speed $speed"
                    }
                }
            }
        }
    }

    private companion object {

        private const val WALK_INFO_TYPE: Int = 1
    }
}
