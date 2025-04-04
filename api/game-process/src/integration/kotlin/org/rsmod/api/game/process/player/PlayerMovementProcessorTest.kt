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
import org.rsmod.game.map.collision.add
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.flag.CollisionFlag

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

                // Cache the coords where the player was before the route was changed.
                val recalcSpot = coords

                // Before the player reaches the destination, let's move the target so that the
                // route has to be recalculated. This should still keep the temp move speed intact.
                target.coords = CoordGrid(0, 0, 0, 0, 0)

                repeat(3) { index ->
                    val steps = 1 + index
                    process()
                    // Destination should be right above the target's new position.
                    assertEquals(target.coords.translateZ(1), routeDestination.peekLast())
                    // Temp move speed should not have been reset as the waypoint queue never
                    // reached the point of being empty.
                    assertEquals(MoveSpeed.Walk, tempMoveSpeed)
                    // The player should be walking towards new destination.
                    assertEquals(recalcSpot.translateZ(-steps), coords)
                }

                // After this process, the player should have reached the waypoint destination
                // and be next to the target.
                process()
                assertEquals(target.coords.translateZ(1), coords)
                assertTrue(routeDestination.isEmpty())
                // Now the temp move speed should have been reset.
                assertNull(tempMoveSpeed)
            }
        }
    }

    @Test
    fun GameTestState.`only recalc while not busy`() = runBasicGameTest {
        withCollisionState {
            val movement =
                PlayerMovementProcessor(it.collision, it.routeFactory, it.stepFactory, eventBus)
            val sourceCoords = CoordGrid(0, 0, 0, 0, 0)
            val targetCoords = CoordGrid(0, 0, 0, 0, 7)
            val target = entityFactory.createAvatar(targetCoords)
            it.allocateCollision(ZoneKey(0, 0, 0))
            withPlayer {
                coords = sourceCoords
                varMoveSpeed = MoveSpeed.Walk
                check(target.coords == targetCoords)
                check(routeDestination.isEmpty())

                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    movement.process(this)
                }

                move(target)

                // Go over the next few steps to assert route is iterating as expected.
                repeat(3) { index ->
                    val steps = 1 + index
                    process()
                    assertTrue(routeDestination.isNotEmpty())
                    assertEquals(sourceCoords.translateZ(steps), coords)
                    assertEquals(targetCoords.translateZ(-1), routeDestination.peekLast())
                    // Clear waypoints as they should be restored by the recalculated request upon
                    // every step in this waypoint.
                    routeDestination.clear()
                }

                // Take the 4th step to assert that the route destination waypoints get restored
                // from the recalculated route.
                process()
                assertTrue(routeDestination.isNotEmpty())
                assertEquals(targetCoords.translateZ(-1), routeDestination.peekLast())
                assertEquals(sourceCoords.translateZ(4), coords)

                // Now let's delay the player and clear the route destination waypoints.
                delay(2)
                routeDestination.clear()

                // We move the target now for future assert conditions.
                target.coords = CoordGrid(0, 0, 0, 0, 0)

                // This 5th step should be null-and-void as the `delay` will make the `isBusy`
                // property return true. The busy flag should pause recalculations from occurring.
                process()
                assertTrue(routeDestination.isEmpty())
                assertEquals(sourceCoords.translateZ(4), coords)

                // The player should no longer be delayed after 1 clock has passed.
                process()
                // Route should have now recalculated based on the target's new position.
                assertTrue(routeDestination.isNotEmpty())
                // The new route destination should end up just one coord above the target.
                assertEquals(target.coords.translateZ(1), routeDestination.peekLast())
                // Player should have also moved one tile this clock.
                assertEquals(sourceCoords.translateZ(3), coords)
            }
        }
    }

    /**
     * Test that routes with [org.rsmod.game.movement.RouteRequest.recalc] enabled will recalculate
     * based on the target's latest position, but only during the last waypoint before reaching the
     * initial destination.
     *
     * Scenario in this test where blue tiles are blocked and red tiles are the source and target:
     *
     * @see <img width=240 height=239 src="https://i.imgur.com/JUEY1Uv.png">
     */
    @Test
    fun GameTestState.`recalculate route during last waypoint stretch`() = runBasicGameTest {
        withCollisionState {
            val sourceStart = CoordGrid(0, 0, 0, 7, 7)
            val targetStart = CoordGrid(0, 0, 0, 0, 7)
            val targetEnd = CoordGrid(0, 0, 0, 7, 7)
            val target = entityFactory.createAvatar(targetStart)
            it.allocateCollision(CoordGrid(0, 0, 0))
            for (dx in 1..6) {
                it.collision.add(CoordGrid(dx, z = 1), CollisionFlag.LOC)
                it.collision.add(CoordGrid(dx, z = 7), CollisionFlag.LOC)
            }
            for (dz in 1..7) {
                it.collision.add(CoordGrid(x = 1, dz), CollisionFlag.LOC)
                it.collision.add(CoordGrid(x = 6, dz), CollisionFlag.LOC)
            }
            val movement =
                PlayerMovementProcessor(it.collision, it.routeFactory, it.stepFactory, eventBus)
            withPlayer {
                coords = sourceStart
                varMoveSpeed = MoveSpeed.Walk
                check(routeDestination.isEmpty())
                check(target.coords == targetStart)

                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    movement.process(this)
                }

                move(target)

                // On the first process, our route request should have filled the player's route
                // destination, and should contain a total of 3 waypoints to reach the target.
                process()
                assertEquals(3, routeDestination.size)

                // As the waypoints have been calculated, we can now move the target.
                target.coords = targetEnd

                // Iterate over the next 5 steps, asserting the player's coords and current
                // route destination waypoint count.
                repeat(5) { i ->
                    // We take into account the first step taken, and then the steps that will be
                    // taken after these process calls.
                    val travelled = 1 + (i + 1)
                    process()
                    assertEquals(3, routeDestination.size)
                    assertEquals(sourceStart.translateZ(-travelled), coords)
                }

                // On the 7th step, the player will have reached the first waypoint.
                process()
                assertEquals(2, routeDestination.size)
                assertEquals(CoordGrid(0, 0, 0, 0, 0), routeDestination.peekFirst())
                assertEquals(CoordGrid(0, 0, 0, 7, 0), coords)

                // Iterate over the next 6 steps, asserting the player's coords and current
                // route destination waypoint count.
                repeat(6) { i ->
                    val travelled = i + 1
                    process()
                    assertEquals(2, routeDestination.size)
                    assertEquals(sourceStart.translate(-travelled, -7), coords)
                }

                // After 6 steps, the destination from the initial route should still be valid.
                assertEquals(CoordGrid(0, 0, 0, 0, 0), routeDestination.peekFirst())

                // Take the 7th step now, which should lead to the current waypoint being removed.
                process()
                assertEquals(1, routeDestination.size)
                assertEquals(CoordGrid(0, 0, 0, 0, 0), coords)

                // Process the next step, which should now insert all new waypoints to reach the
                // updated target position.
                process()
                assertEquals(CoordGrid(0, 0, 0, 7, 0), routeDestination.peekFirst())
                assertEquals(CoordGrid(0, 0, 0, 7, 6), routeDestination.peekLast())
                assertEquals(2, routeDestination.size)

                // At this point, we _could_ assert that the rest of steps are completed... but that
                // is out of scope of this test. By now, we have made sure the path was recalculated
                // and only done so during the final waypoint "line."
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
