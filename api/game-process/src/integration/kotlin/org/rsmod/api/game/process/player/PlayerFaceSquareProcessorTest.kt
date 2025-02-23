package org.rsmod.api.game.process.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.game.map.Direction
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey

class PlayerFaceSquareProcessorTest {
    @Test
    fun GameTestState.`face angle is delayed until no movement`() = runBasicGameTest {
        withCollisionState {
            val start = CoordGrid(0, 0, 0, 10, 10)
            val moveDest = CoordGrid(0, 0, 0, 10, 15)
            val faceTarget = CoordGrid(0, 0, 0, 15, 15)
            it.allocateCollision(MapSquareKey(0, 0))
            withPlayer(start) {
                val move = PlayerMovementProcessor(it.collision, it.routeFactory, it.stepFactory)
                val face = PlayerFaceSquareProcessor()
                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    move.process(this)
                    face.process(this)
                }

                fun processUntilArrival(dest: CoordGrid, tracker: Int = 0) {
                    check(tracker < 1000)
                    if (coords != dest) {
                        process()
                        processUntilArrival(dest, tracker + 1)
                    }
                }

                walk(moveDest)
                faceSquare(faceTarget, targetWidth = 1, targetLength = 1)
                check(pendingFaceAngle == EntityFaceAngle.NULL)

                process()
                // After processing, the player should have moved. This means the face angle should
                // not be calculated yet, and pending face square should remain as its initial
                // value.
                check(routeDestination.lastOrNull() == moveDest)
                check(hasMovedThisCycle)
                assertEquals(EntityFaceAngle.NULL, pendingFaceAngle)
                assertEquals(faceTarget, pendingFaceSquare)

                processUntilArrival(moveDest)

                // Now that the player has arrived, we have to wait 1 cycle (process) before the
                // face
                // angle is set due to the `hasMovedThisTick` condition.
                process()

                assertEquals(Direction.East.angle, pendingFaceAngle.intValue)
                assertEquals(CoordGrid.NULL, pendingFaceSquare)
            }
        }
    }

    @Test
    fun GameTestState.`send default face angle on player init`() = runBasicGameTest {
        withPlayer {
            val facing = PlayerFaceSquareProcessor()
            fun process() {
                previousCoords = coords
                currentMapClock++
                processedMapClock++
                facing.process(this)
            }
            check(pendingFaceAngle == EntityFaceAngle.NULL)
            check(pendingFaceSquare == CoordGrid.ZERO)

            process()
            assertEquals(EntityFaceAngle.ZERO, pendingFaceAngle)
        }
    }

    @Test
    fun GameTestState.`reset pending face square after setting face angle`() = runBasicGameTest {
        withPlayer {
            val facing = PlayerFaceSquareProcessor()
            fun process() {
                previousCoords = coords
                currentMapClock++
                processedMapClock++
                facing.process(this)
            }
            check(pendingFaceSquare != CoordGrid.NULL)

            faceSquare(CoordGrid(0, 0, 0, 1, 1), targetWidth = 1, targetLength = 1)

            process()
            assertNotEquals(EntityFaceAngle.NULL, pendingFaceAngle)
            assertEquals(CoordGrid.NULL, pendingFaceSquare)
        }
    }

    @TestWithArgs(DirectionProvider::class)
    fun `calculate correct angle based on direction`(dir: Direction, state: GameTestState) =
        with(state) {
            runBasicGameTest {
                val start = CoordGrid(0, 0, 0, 16, 16)
                val target = start.translate(dir.xOff * 5, dir.zOff * 5)
                withPlayer(start) {
                    val facing = PlayerFaceSquareProcessor()
                    fun process() {
                        previousCoords = coords
                        currentMapClock++
                        processedMapClock++
                        facing.process(this)
                    }
                    check(pendingFaceAngle == EntityFaceAngle.NULL)

                    // Set the _pending_ face square to target.
                    faceSquare(target, targetWidth = 1, targetLength = 1)
                    check(pendingFaceAngle == EntityFaceAngle.NULL)

                    process()

                    assertEquals(dir.angle, pendingFaceAngle.intValue)
                    assertEquals(CoordGrid.NULL, pendingFaceSquare)
                }
            }
        }

    private object DirectionProvider : TestArgsProvider {
        override fun args(): List<TestArgs> = Direction.entries.map { TestArgs(it) }
    }
}
