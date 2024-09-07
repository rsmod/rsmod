package org.rsmod.api.game.process.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.entityFactory
import org.rsmod.api.testing.factory.locFactory
import org.rsmod.api.testing.factory.locTypeFactory
import org.rsmod.game.map.collision.addLoc
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.game.movement.RouteRequestLoc
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.map.CoordGrid

class PlayerRouteRequestTest {
    @Test
    fun GameTestState.`reach coordinate target route destination`() = runGameTest {
        val start = CoordGrid(3200, 3200)
        val dest = CoordGrid(3205, 3202)
        withCollisionState {
            val movement = PlayerMovementProcessor(it.collision, it.routeFactory, it.stepFactory)
            it.collision.allocateIfAbsent(start.x, start.x, start.level)
            it.collision.allocateIfAbsent(dest.x, dest.z, dest.level)
            withPlayer {
                coords = start
                check(routeDestination.isEmpty())
                routeRequest = RouteRequestCoord(dest)
                repeat(16) { movement.process(this) }
                assertTrue(routeDestination.isEmpty())
                assertEquals(dest, coords)
            }
        }
    }

    @Test
    fun GameTestState.`reach pathing entity target route destination`() = runGameTest {
        val start = CoordGrid(3200, 3200)
        val dest = CoordGrid(3203, 3207)
        val target = entityFactory.createAvatar(dest)
        withCollisionState {
            val movement = PlayerMovementProcessor(it.collision, it.routeFactory, it.stepFactory)
            val validator = it.boundValidator
            it.collision.allocateIfAbsent(start.x, start.x, start.level)
            it.collision.allocateIfAbsent(target.coords.x, target.coords.z, target.coords.level)
            withPlayer {
                coords = start
                check(routeDestination.isEmpty())
                check(!validator.touches(avatar, target))
                routeRequest = RouteRequestPathingEntity(target)
                repeat(16) { movement.process(this) }
                assertTrue(routeDestination.isEmpty())
                assertNotEquals(coords, start)
                assertTrue(validator.touches(avatar, target))
                assertFalse(validator.collides(avatar, target))
            }
        }
    }

    @Test
    fun GameTestState.`reach loc target route destination`() = runGameTest {
        val start = CoordGrid(3200, 3200)
        val dest = CoordGrid(3203, 3205)
        val target = locFactory.create(dest)
        val type =
            locTypeFactory.create(target) {
                width = 2
                length = 2
            }
        withCollisionState {
            val movement = PlayerMovementProcessor(it.collision, it.routeFactory, it.stepFactory)
            val validator = it.boundValidator
            it.collision.allocateIfAbsent(start.x, start.x, start.level)
            it.collision.addLoc(target, type)
            withPlayer {
                coords = start
                check(routeDestination.isEmpty())
                check(!validator.touches(avatar, target, type))
                routeRequest = RouteRequestLoc(target, type)
                repeat(16) { movement.process(this) }
                assertTrue(routeDestination.isEmpty())
                assertNotEquals(coords, start)
                assertTrue(validator.touches(avatar, target, type))
                assertFalse(validator.collides(avatar, target, type))
            }
        }
    }
}
