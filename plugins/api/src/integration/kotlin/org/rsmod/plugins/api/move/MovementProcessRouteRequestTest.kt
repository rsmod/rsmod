package org.rsmod.plugins.api.move

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.route.RouteRequestCoordinates
import org.rsmod.game.model.route.RouteRequestEntity
import org.rsmod.plugins.api.map.collision.addObject
import org.rsmod.plugins.api.model.route.RouteRequestGameObject
import org.rsmod.plugins.api.pathfinder.BoundValidator
import org.rsmod.plugins.testing.GameTestState

class MovementProcessRouteRequestTest {

    @Test
    fun GameTestState.testCoordinateRequest() = runGameTest {
        val start = Coordinates(3200, 3200)
        val dest = Coordinates(3205, 3202)
        withCollisionState {
            val process = MovementProcess(playerList, it.routeFactory, it.stepFactory)
            it.collision.allocateIfAbsent(start.x, start.x, start.level)
            it.collision.allocateIfAbsent(dest.x, dest.z, dest.level)
            withPlayer {
                coords = start
                check(movement.isEmpty())
                routeRequest = RouteRequestCoordinates(dest)
                repeat(16) { process.execute() }
                assertTrue(movement.isEmpty())
                assertEquals(dest, coords)
            }
        }
    }

    @Test
    fun GameTestState.testEntityRequest() = runGameTest {
        val start = Coordinates(3200, 3200)
        val dest = Coordinates(3203, 3207)
        val target = createEntity().apply { coords = dest }
        withCollisionState {
            val process = MovementProcess(playerList, it.routeFactory, it.stepFactory)
            val validator = BoundValidator(it.collision)
            it.collision.allocateIfAbsent(start.x, start.x, start.level)
            it.collision.allocateIfAbsent(target.coords.x, target.coords.z, target.coords.level)
            withPlayer {
                coords = start
                check(movement.isEmpty())
                check(!validator.touches(entity, target))
                routeRequest = RouteRequestEntity(target)
                repeat(16) { process.execute() }
                assertTrue(movement.isEmpty())
                assertNotEquals(coords, start)
                assertTrue(validator.touches(entity, target))
                assertFalse(validator.collides(entity, target))
            }
        }
    }

    @Test
    fun GameTestState.testGameObjectRequest() = runGameTest {
        val start = Coordinates(3200, 3200)
        val dest = Coordinates(3203, 3205)
        val target = createGameObject(dest) {
            width = 2
            height = 2
        }
        withCollisionState {
            val process = MovementProcess(playerList, it.routeFactory, it.stepFactory)
            val validator = BoundValidator(it.collision)
            it.collision.allocateIfAbsent(start.x, start.x, start.level)
            it.collision.addObject(target)
            withPlayer {
                coords = start
                check(movement.isEmpty())
                check(!validator.touches(entity, target))
                routeRequest = RouteRequestGameObject(target)
                repeat(16) { process.execute() }
                assertTrue(movement.isEmpty())
                assertNotEquals(coords, start)
                assertTrue(validator.touches(entity, target))
                assertFalse(validator.collides(entity, target))
            }
        }
    }
}
