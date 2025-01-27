package org.rsmod.api.game.process.player

import net.rsprot.protocol.game.outgoing.misc.player.SetMapFlag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.capture.attachClientCapture
import org.rsmod.api.testing.factory.collisionFactory
import org.rsmod.api.testing.factory.playerFactory
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.map.CoordGrid

class PlayerMovementMapFlagTest {
    @Test
    fun GameTestState.`empty route resets map flag`() = runBasicGameTest {
        val player = playerFactory.create()
        val captured = player.attachClientCapture()

        val movement = createMovementProcessor()
        fun process() {
            captured.clearOutgoing()
            player.previousCoords = player.coords
            player.currentMapClock++
            player.processedMapClock++
            movement.process(player)
        }

        player.walk(player.coords)
        process()

        assertEquals(1, captured.countOf<SetMapFlag>())
        assertTrue(captured.allOf(SetMapFlag::isReset))
    }

    @Test
    fun GameTestState.`map flag is reset on last step consumption`() = runBasicGameTest {
        val collision = collisionFactory.borrowSharedMap()
        val routeFactory = RouteFactory(collision)
        val stepFactory = StepFactory(collision)
        collision.allocateIfAbsent(0, 0, 0)

        // Move 1 tile north.
        withPlayer {
            val startCoords = CoordGrid(0, 0, 0, 0, 0)
            val captured = attachClientCapture()

            val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory)
            fun process() {
                captured.clearOutgoing()
                previousCoords = coords
                currentMapClock++
                processedMapClock++
                movement.process(this)
            }

            // Walk to destination.
            varMoveSpeed = MoveSpeed.Walk

            val expectedDestination = startCoords.translateZ(1)
            walk(expectedDestination)

            process()

            assertEquals(expectedDestination, coords)
            assertEquals(2, captured.countOf<SetMapFlag>())
            assertTrue(captured.anyOf(SetMapFlag::isNotReset))
            assertTrue(captured.anyOf(SetMapFlag::isReset))

            // Run to destination.
            coords = startCoords
            varMoveSpeed = MoveSpeed.Run

            walk(expectedDestination)

            process()

            assertEquals(expectedDestination, coords)
            assertEquals(2, captured.countOf<SetMapFlag>())
            assertTrue(captured.anyOf(SetMapFlag::isNotReset))
            assertTrue(captured.anyOf(SetMapFlag::isReset))
        }

        // Move 2 tile norths.
        withPlayer {
            val startCoords = CoordGrid(0, 0, 0, 0, 0)
            val captured = attachClientCapture()

            val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory)
            fun process() {
                captured.clearOutgoing()
                previousCoords = coords
                currentMapClock++
                processedMapClock++
                movement.process(this)
            }

            // Walk to destination.
            varMoveSpeed = MoveSpeed.Walk

            val expectedDestination = startCoords.translateZ(2)
            walk(expectedDestination)

            process()

            assertEquals(startCoords.translateZ(1), coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isNotReset))

            process()

            assertEquals(expectedDestination, coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isReset))

            // Run to destination.
            coords = startCoords
            varMoveSpeed = MoveSpeed.Run

            walk(expectedDestination)

            process()

            assertEquals(expectedDestination, coords)
            assertEquals(2, captured.countOf<SetMapFlag>())
            assertTrue(captured.anyOf(SetMapFlag::isNotReset))
            assertTrue(captured.anyOf(SetMapFlag::isReset))
        }

        // Move 3 tile norths.
        withPlayer {
            val startCoords = CoordGrid(0, 0, 0, 0, 0)
            val captured = attachClientCapture()

            val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory)
            fun process() {
                captured.clearOutgoing()
                previousCoords = coords
                currentMapClock++
                processedMapClock++
                movement.process(this)
            }

            // Walk to destination.
            varMoveSpeed = MoveSpeed.Walk

            val expectedDestination = startCoords.translateZ(3)
            walk(expectedDestination)

            process()

            assertEquals(startCoords.translateZ(1), coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isNotReset))

            process()

            // No set map flag message is sent in this step.
            assertEquals(startCoords.translateZ(2), coords)
            assertEquals(0, captured.countOf<SetMapFlag>())

            process()

            assertEquals(expectedDestination, coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isReset))

            // Run to destination.
            coords = startCoords
            varMoveSpeed = MoveSpeed.Run

            walk(expectedDestination)

            process()

            assertEquals(startCoords.translateZ(2), coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isNotReset))

            process()

            assertEquals(expectedDestination, coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isReset))
        }

        // Move 4 tile norths.
        withPlayer {
            val startCoords = CoordGrid(0, 0, 0, 0, 0)
            val captured = attachClientCapture()

            val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory)
            fun process() {
                captured.clearOutgoing()
                previousCoords = coords
                currentMapClock++
                processedMapClock++
                movement.process(this)
            }

            // Walk to destination.
            varMoveSpeed = MoveSpeed.Walk

            val expectedDestination = startCoords.translateZ(4)
            walk(expectedDestination)

            process()

            assertEquals(startCoords.translateZ(1), coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isNotReset))

            process()

            // No set map flag message is sent in this step.
            assertEquals(startCoords.translateZ(2), coords)
            assertEquals(0, captured.countOf<SetMapFlag>())

            process()

            // No set map flag message is sent in this step.
            assertEquals(startCoords.translateZ(3), coords)
            assertEquals(0, captured.countOf<SetMapFlag>())

            process()

            assertEquals(expectedDestination, coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isReset))

            // Run to destination.
            coords = startCoords
            varMoveSpeed = MoveSpeed.Run

            walk(expectedDestination)

            process()

            assertEquals(startCoords.translateZ(2), coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isNotReset))

            process()

            assertEquals(expectedDestination, coords)
            assertEquals(1, captured.countOf<SetMapFlag>())
            assertTrue(captured.singlePredicate(SetMapFlag::isReset))
        }
    }

    @Test
    fun GameTestState.`emulate map flags in lumbridge castle route`() = runBasicGameTest {
        val expectedCoordinates =
            listOf(
                CoordGrid(0, 50, 50, 10, 12),
                CoordGrid(0, 50, 50, 9, 12),
                CoordGrid(0, 50, 50, 8, 12),
                CoordGrid(0, 50, 50, 8, 11),
                CoordGrid(0, 50, 50, 9, 11),
                CoordGrid(0, 50, 50, 10, 11),
                CoordGrid(0, 50, 50, 11, 11),
                CoordGrid(0, 50, 50, 12, 11),
                CoordGrid(0, 50, 50, 13, 11),
                CoordGrid(0, 50, 50, 14, 11),
                CoordGrid(0, 50, 50, 15, 11),
            )

        val player = playerFactory.create(expectedCoordinates.first())
        val captured = player.attachClientCapture()
        player.buildArea = CoordGrid(0, 49, 49, 32, 32)
        player.varMoveSpeed = MoveSpeed.Walk

        val movement = PlayerMovementProcessor(collision, routeFactory, stepFactory)
        fun process() {
            captured.clearOutgoing()
            player.previousCoords = player.coords
            player.currentMapClock++
            player.processedMapClock++
            movement.process(player)
        }

        fun SetMapFlag.isExpectedDestination(): Boolean {
            return xInBuildArea == 47 && zInBuildArea == 43
        }

        // Route player to destination.
        player.walk(expectedCoordinates.last())

        // Ensure the map flag is set on the first process.
        process()
        assertEquals(expectedCoordinates[1], player.coords)
        assertEquals(1, captured.countOf<SetMapFlag>())
        assertTrue(captured.allOf(SetMapFlag::isExpectedDestination))

        // None of these coordinates should transmit a map flag message.
        for (i in 2 until expectedCoordinates.size - 1) {
            process()
            assertEquals(expectedCoordinates[i], player.coords)
            assertEquals(0, captured.countOf<SetMapFlag>())
        }

        // The map flag should be reset once the last step has been consumed.
        process()
        assertEquals(expectedCoordinates.last(), player.coords)
        assertEquals(1, captured.countOf<SetMapFlag>())
        assertTrue(captured.allOf(SetMapFlag::isReset))
    }

    private fun createMovementProcessor(): PlayerMovementProcessor {
        val collision = collisionFactory.create()
        collision.allocateIfAbsent(0, 0, 0)
        val routeFactory = RouteFactory(collision)
        val stepFactory = StepFactory(collision)
        return PlayerMovementProcessor(collision, routeFactory, stepFactory)
    }
}

private fun SetMapFlag.isNotReset(): Boolean = !isReset()

private fun SetMapFlag.isReset(): Boolean = xInBuildArea == 255 && zInBuildArea == 255
