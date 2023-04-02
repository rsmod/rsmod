package org.rsmod.plugins.api.move

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.route.RouteRequestCoordinates
import org.rsmod.plugins.testing.GameTestState

class MovementProcessTest {

    @Test
    fun GameTestState.testLogInWalkEmulation() = runGameTest {
        val process = MovementProcess(playerList, routeFactory, stepFactory)
        withPlayer {
            coords = Coordinates(3357, 3141)
            routeRequest = RouteRequestCoordinates(
                destination = Coordinates(3384, 3155),
                speed = MoveSpeed.Walk,
                async = true
            )
            assertEquals(Coordinates(3357, 3141), coords)
            val expectedSteps = 82
            repeat(expectedSteps) {
                process.execute()
            }
            assertEquals(Coordinates(3275, 3059), coords)
            /*
             * Though the next tile in the path is blocked, the
             * destination waypoint should not be cleared until
             * properly "interrupted," or reached.
             */
            repeat(16) {
                assertFalse(movement.isEmpty())
                process.execute()
            }
        }
    }
}
