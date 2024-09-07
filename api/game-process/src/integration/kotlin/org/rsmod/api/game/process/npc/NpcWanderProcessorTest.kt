package org.rsmod.api.game.process.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.random.DefaultGameRandom
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcFactory
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam
import org.rsmod.api.testing.random.SequenceRandom
import org.rsmod.game.map.Direction
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey

class NpcWanderProcessorTest {
    @Test
    fun GameTestState.`wander to max range destinations`() = runGameTest {
        withCollisionState {
            val random = SequenceRandom()
            val spawn = CoordGrid(0, 0, 0, 25, 25)
            val type = npcTypeFactory.create { wanderRange = 16 }
            val npc = npcFactory.create(type, CoordGrid(0, 0, 0, 50, 50))
            it.allocateCollision(MapSquareKey(0, 0))
            withNpc(npc) {
                val movement = NpcMovementProcessor(it.collision, it.stepFactory)
                val mode = NpcWanderModeProcessor(random, it.collision)
                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    mode.process(this)
                    movement.process(this)
                }

                fun processUntilArrival(dest: CoordGrid, tracker: Int = 0) {
                    check(tracker < 1000) {
                        "Exiting early! Npc on `$coords` could not arrive to destination `$dest`."
                    }
                    if (coords != dest) {
                        previousCoords = coords
                        currentMapClock++
                        movement.process(this)
                        processUntilArrival(dest, tracker + 1)
                    }
                }

                // Set the npc spawn coord to the appropriate coordgrid.
                spawnCoords = spawn

                random.next = Int.MAX_VALUE
                process()
                assertFalse(hasMovedThisTick)
                assertEquals(lastMovement, -1)
                assertTrue(routeDestination.isEmpty())

                random.next = 0 // Trigger `randomBoolean` to begin walk.
                random.then = 4 // Set output for x-coordinate movement.
                random.then = -2 // Set output for z-coordinate movement.
                val expectedDest1 = spawn.translate(4, -2)
                process()
                assertTrue(hasMovedThisTick)
                assertEquals(expectedDest1, routeDestination.lastOrNull())

                coords = expectedDest1
                abortRoute()

                Direction.SouthWest.let { (x, z) ->
                    val deltaX = x * type.wanderRange
                    val deltaZ = z * type.wanderRange
                    val expected = spawn.translate(deltaX, deltaZ)
                    random.next = 0
                    random.then = deltaX
                    random.then = deltaZ
                    process()
                    assertTrue(hasMovedThisTick)
                    assertEquals(expected, routeDestination.lastOrNull())

                    processUntilArrival(expected)
                    assertTrue(routeDestination.isEmpty())
                    assertEquals(expected, coords)
                }

                Direction.NorthWest.let { (x, z) ->
                    val deltaX = x * type.wanderRange
                    val deltaZ = z * type.wanderRange
                    val expected = spawn.translate(deltaX, deltaZ)
                    random.next = 0
                    random.then = deltaX
                    random.then = deltaZ
                    process()
                    assertTrue(hasMovedThisTick)
                    assertEquals(expected, routeDestination.lastOrNull())

                    processUntilArrival(expected)
                    assertTrue(routeDestination.isEmpty())
                    assertEquals(expected, coords)
                }

                Direction.NorthEast.let { (x, z) ->
                    val deltaX = x * type.wanderRange
                    val deltaZ = z * type.wanderRange
                    val expected = spawn.translate(deltaX, deltaZ)
                    random.next = 0
                    random.then = deltaX
                    random.then = deltaZ
                    process()
                    assertTrue(hasMovedThisTick)
                    assertEquals(expected, routeDestination.lastOrNull())

                    processUntilArrival(expected)
                    assertTrue(routeDestination.isEmpty())
                    assertEquals(expected, coords)
                }

                Direction.SouthEast.let { (x, z) ->
                    val deltaX = x * type.wanderRange
                    val deltaZ = z * type.wanderRange
                    val expected = spawn.translate(deltaX, deltaZ)
                    random.next = 0
                    random.then = deltaX
                    random.then = deltaZ
                    process()
                    assertTrue(hasMovedThisTick)
                    assertEquals(expected, routeDestination.lastOrNull())

                    processUntilArrival(expected)
                    assertTrue(routeDestination.isEmpty())
                    assertEquals(expected, coords)
                }

                val expectedDestSpawn = spawn.translate(0, 0)
                random.next = 0
                random.then = 0
                random.then = 0
                process()
                assertTrue(hasMovedThisTick)
                assertEquals(expectedDestSpawn, routeDestination.lastOrNull())

                processUntilArrival(expectedDestSpawn)
                assertTrue(routeDestination.isEmpty())
            }
        }
    }

    @Test
    fun GameTestState.`retreat back to spawn point when wander range is 0`() = runGameTest {
        withCollisionState {
            val spawn = CoordGrid(0, 0, 0, 25, 25)
            val type = npcTypeFactory.create { wanderRange = 0 }
            val npc = npcFactory.create(type, spawn)
            it.allocateCollision(MapSquareKey(0, 0))
            withNpc(npc) {
                val movement = NpcMovementProcessor(it.collision, it.stepFactory)
                val mode = NpcWanderModeProcessor(DefaultGameRandom(), it.collision)
                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    mode.process(this)
                    movement.process(this)
                }

                fun processUntilArrival(dest: CoordGrid, tracker: Int = 0) {
                    check(tracker < 1000) {
                        "Exiting early! Npc on `$coords` could not arrive to destination `$dest`."
                    }
                    if (coords != dest) {
                        previousCoords = coords
                        currentMapClock++
                        movement.process(this)
                        processUntilArrival(dest, tracker + 1)
                    }
                }

                coords = CoordGrid(0, 0, 0, 50, 50)
                check(routeDestination.isEmpty())

                process()
                assertTrue(hasMovedThisTick)
                assertEquals(spawn, routeDestination.lastOrNull())

                processUntilArrival(spawn)

                assertTrue(routeDestination.isEmpty())
                assertEquals(spawn, coords)
            }
        }
    }

    @TestWithArgs(WanderRangeProvider::class)
    fun `teleport to spawn when idle`(wanderRange: Int, state: GameTestState) =
        state.runGameTest {
            withCollisionState {
                val spawn = CoordGrid(0, 0, 0, 25, 25)
                val current = CoordGrid(0, 0, 0, 50, 50)
                val type = npcTypeFactory.create { this.wanderRange = wanderRange }
                val npc = npcFactory.create(type, current)
                it.blockDirections(current, Direction.CARDINAL)
                withNpc(npc) {
                    val movement = NpcMovementProcessor(it.collision, it.stepFactory)
                    val mode = NpcWanderModeProcessor(DefaultGameRandom(), it.collision)
                    fun process() {
                        previousCoords = coords
                        currentMapClock++
                        mode.process(this)
                        movement.process(this)
                    }

                    // Set the npc spawn point.
                    spawnCoords = spawn

                    val startClock = currentMapClock

                    // Start off with last movement set.
                    lastMovement = currentMapClock

                    val respawnDelay = NpcWanderModeProcessor.RESPAWN_IDLE_REQUIREMENT
                    repeat(respawnDelay) {
                        process()
                        assertEquals(current, coords)
                    }

                    // There is still a one tick delay before the npc is actually teleported away.
                    process()

                    assertEquals(spawn, coords)
                    assertEquals(respawnDelay + 1, currentMapClock - startClock)
                }
            }
        }

    private object WanderRangeProvider : TestArgsProvider {
        override fun args(): List<TestArgs> = testArgsOfSingleParam(0, 7)
    }
}
