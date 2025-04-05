package org.rsmod.api.game.process.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.entityFactory
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

class PlayerMovementProcessorTest {
    @Test
    fun GameTestState.`reset temp speed only when destination queue is empty`() = runBasicGameTest {
        withCollisionState {
            val movement =
                PlayerMovementProcessor(it.collision, it.routeFactory, it.stepFactory, eventBus)
            val sourceCoords = CoordGrid(0, 0, 0, 0, 0)
            val targetCoords = CoordGrid(0, 0, 0, 0, 7)
            val target = entityFactory.createAvatar(targetCoords)
            it.allocateCollision(ZoneKey(0, 0, 0))
            withPlayer {
                coords = sourceCoords
                varMoveSpeed = MoveSpeed.Run
                check(target.coords == targetCoords)
                check(routeDestination.isEmpty())

                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    movement.process(this)
                }

                move(target)
                tempMoveSpeed = MoveSpeed.Walk

                // Go over the next few steps to assert route is iterating as expected.
                repeat(5) { index ->
                    val steps = 1 + index
                    process()
                    assertEquals(targetCoords.translateZ(-1), routeDestination.peekLast())
                    // Process should only be allowing 1 step at a time due to the temp move speed
                    // set to "Walk."
                    assertEquals(sourceCoords.translateZ(steps), coords)
                    // Temp move speed flag should not be reset under these circumstances.
                    assertEquals(MoveSpeed.Walk, tempMoveSpeed)
                }

                // After this process, the player should have reached the waypoint destination
                // and be next to the target.
                process()
                assertEquals(target.coords.translateZ(-1), coords)
                assertTrue(routeDestination.isEmpty())
                // Now the temp move speed should have been reset.
                assertNull(tempMoveSpeed)
            }
        }
    }

    @Test
    fun GameTestState.`walk towards waypoints`() = runBasicGameTest {
        val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory, eventBus)
        val start = CoordGrid(3194, 3199)
        val waypoints =
            listOf(
                start.translate(xOffset = 1, zOffset = 0),
                start.translate(xOffset = 1, zOffset = 1),
                start.translate(xOffset = 2, zOffset = 1),
                start.translate(xOffset = 2, zOffset = 2),
                start.translate(xOffset = 3, zOffset = 2),
            )
        withPlayer {
            coords = start
            check(routeDestination.isEmpty())
            moveSpeed = MoveSpeed.Walk
            routeDestination += waypoints
            waypoints.forEachIndexed { index, waypoint ->
                assertEquals(waypoints.size - index, routeDestination.size)
                movement.process(this)
                assertEquals(waypoint, coords)
                assertEquals(waypoints.size - (index + 1), routeDestination.size)
            }
            assertTrue(routeDestination.isEmpty())
        }
    }

    /**
     * Waypoint in player's routeDestination queue should not be removed until they have fully
     * reached it.
     *
     * There was a scenario where after reaching a waypoint, the next one was instantly consumed and
     * removed from the routeDestination queue. If the final destination was X tiles away from said
     * waypoint initial [CoordGrid], then the rest of the route's tiles were never processed.
     *
     * X = further away than tiles the player could process in that single game cycle.
     */
    @Test
    fun GameTestState.`consume waypoints only on arrival`() = runBasicGameTest {
        val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory, eventBus)
        // North of Lumbridge fountain.
        val start = CoordGrid(3221, 3228)
        val dest = CoordGrid(3220, 3226)
        val waypoints = listOf(start.translate(xOffset = -1, zOffset = 0), dest)
        withPlayer {
            coords = start
            check(routeDestination.isEmpty())
            moveSpeed = MoveSpeed.Run
            routeDestination += waypoints
            repeat(waypoints.size) { movement.process(this) }
            assertEquals(dest, coords)
            assertTrue(routeDestination.isEmpty())
        }
    }

    /**
     * When a player is "running" to a destination that's within an odd-tile of distance away, they
     * will complete the route by walking the last step.
     */
    @Test
    fun GameTestState.`finish off walking when coords are odd-tiles away`() = runBasicGameTest {
        val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory, eventBus)
        val start = CoordGrid(3200, 3200)
        /* verify running > 1 tile does _not_ queue extended-info update */
        withPlayer {
            coords = start
            routeRequest = RouteRequestCoord(start.translateX(2))
            varMoveSpeed = MoveSpeed.Run
            assertEquals(MoveSpeed.Stationary, moveSpeed)
            movement.process(this)
            assertEquals(MoveSpeed.Run, moveSpeed)
            assertEquals(start.translateX(2), coords)
        }
        /* now verify running within one-tile distance */
        withPlayer {
            coords = start
            routeRequest = RouteRequestCoord(start.translateX(1))
            varMoveSpeed = MoveSpeed.Run
            assertEquals(MoveSpeed.Stationary, moveSpeed)
            movement.process(this)
            assertEquals(MoveSpeed.Walk, moveSpeed)
            assertEquals(start.translateX(1), coords)
        }
    }

    @TestWithArgs(StandardMoveSpeedProvider::class)
    fun `correct move speed steps per process`(speed: MoveSpeed, state: GameTestState) =
        state.runBasicGameTest {
            // Utilize a new collision state so that collision flags do not
            // interfere with test.
            withCollisionState {
                val startCoords = CoordGrid(3200, 3200, 0)
                // Allocate an empty zone with no collision flags.
                it.collision.allocateIfAbsent(startCoords.x, startCoords.z, startCoords.level)
                val eventBus = state.eventBus
                val movement =
                    PlayerMovementProcessor(it.collision, it.routeFactory, it.stepFactory, eventBus)
                // If we want to support more than 7 steps, allocate
                // neighbouring zones.
                check(speed.steps < 8)
                withPlayer {
                    val expectedCoords = startCoords.translateZ(speed.steps)
                    coords = startCoords
                    check(routeDestination.isEmpty())
                    moveSpeed = speed
                    varMoveSpeed = speed
                    routeDestination += coords.translateZ(7)
                    movement.process(this)
                    assertEquals(expectedCoords, coords)
                }
                withPlayer {
                    val expectedCoords = startCoords.translateX(speed.steps)
                    coords = startCoords
                    moveSpeed = speed
                    varMoveSpeed = speed
                    check(routeDestination.isEmpty())
                    routeDestination += coords.translateX(7)
                    movement.process(this)
                    assertEquals(expectedCoords, coords)
                }
                withPlayer {
                    val expectedCoords = startCoords.translate(speed.steps, speed.steps)
                    coords = startCoords
                    moveSpeed = speed
                    varMoveSpeed = speed
                    check(routeDestination.isEmpty())
                    routeDestination += coords.translate(7, 7)
                    movement.process(this)
                    assertEquals(expectedCoords, coords)
                }
            }
        }

    private object StandardMoveSpeedProvider : TestArgsProvider {
        override fun args(): List<TestArgs> = testArgsOfSingleParam(MoveSpeed.Walk, MoveSpeed.Run)
    }
}
