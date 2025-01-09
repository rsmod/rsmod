package org.rsmod.routefinder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.routefinder.collision.buildCollisionMap
import org.rsmod.routefinder.collision.flag
import org.rsmod.routefinder.flag.BlockAccessFlag.EAST
import org.rsmod.routefinder.flag.BlockAccessFlag.NORTH
import org.rsmod.routefinder.flag.BlockAccessFlag.SOUTH
import org.rsmod.routefinder.flag.BlockAccessFlag.WEST
import org.rsmod.routefinder.flag.CollisionFlag
import org.rsmod.routefinder.flag.CollisionFlag.WALL_EAST
import org.rsmod.routefinder.flag.CollisionFlag.WALL_NORTH
import org.rsmod.routefinder.flag.CollisionFlag.WALL_SOUTH
import org.rsmod.routefinder.flag.CollisionFlag.WALL_WEST

class RouteFindingTest {
    @Test
    fun `ensure route waypoints match request level`() {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = 3201 to 3200
        val map = buildCollisionMap(srcX, srcZ, destX, destZ)
        val routeFinding = RouteFinding(map)
        routeFinding.findRoute(level = 0, srcX, srcZ, destX, destZ).let { route ->
            check(route.success)
            assertTrue(route.all { it.level == 0 })
        }
        routeFinding.findRoute(level = 1, srcX, srcZ, destX, destZ).let { route ->
            check(route.success)
            assertTrue(route.all { it.level == 1 })
        }
        routeFinding.findRoute(level = 2, srcX, srcZ, destX, destZ).let { route ->
            check(route.success)
            assertTrue(route.all { it.level == 2 })
        }
        routeFinding.findRoute(level = 3, srcX, srcZ, destX, destZ).let { route ->
            check(route.success)
            assertTrue(route.all { it.level == 3 })
        }
    }

    @Test
    fun `return alternate route when surrounded by locs with moveNear flag`() {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = 3205 to 3200
        val map =
            buildCollisionMap(srcX, srcZ, destX, destZ)
                .flag(srcX - 1, srcZ - 1, width = 3, length = 3, CollisionFlag.LOC)
        map[srcX, srcZ, 0] = 0 // Remove collision flag from source tile
        val routeFinding = RouteFinding(map)
        routeFinding.findRoute(level = 0, srcX, srcZ, destX, destZ, moveNear = true).let { route ->
            assertTrue(route.alternative)
            assertTrue(route.isEmpty())
        }
    }

    @Test
    fun `fail route when surrounded by locs without moveNear flag`() {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = 3205 to 3200
        val map =
            buildCollisionMap(srcX, srcZ, destX, destZ)
                .flag(srcX - 1, srcZ - 1, width = 3, length = 3, CollisionFlag.LOC)
        map[srcX, srcZ, 0] = 0 // Remove collision flag from source tile
        val routeFinding = RouteFinding(map)
        routeFinding.findRoute(level = 0, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
        }
    }

    @Test
    fun `maneuver around through single exit point`() {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = 3200 to 3205
        val map =
            buildCollisionMap(srcX, srcZ, destX, destZ)
                .flag(srcX - 1, srcZ - 1, width = 3, length = 3, CollisionFlag.LOC)
        map[srcX, srcZ, 0] = 0 // Remove collision flag from source tile
        map[srcX, srcZ - 1, 0] = 0 // Remove collision flag from tile south of source tile.
        val routeFinding = RouteFinding(map)
        routeFinding.findRoute(level = 0, srcX, srcZ, destX, destZ).let { route ->
            assertTrue(route.success)
            assertEquals(4, route.waypoints.size)
            assertEquals(RouteCoordinates(3200, 3198), route.waypoints[0])
            assertEquals(RouteCoordinates(3198, 3198), route.waypoints[1])
            assertEquals(RouteCoordinates(3198, 3203), route.waypoints[2])
            assertEquals(RouteCoordinates(destX, destZ), route.waypoints.last())
        }
    }

    @Test
    fun `return empty and successful route when standing on final route coordinate`() {
        val (srcX, srcZ) = 3200 to 3200
        val (locX, locZ) = 3200 to 3201
        val map = buildCollisionMap(srcX, srcZ, locX, locZ)
        val routeFinding = RouteFinding(map)
        map[locX, locZ, 0] = WALL_NORTH or WALL_SOUTH or WALL_WEST or WALL_EAST
        routeFinding.findRoute(level = 0, srcX, srcZ, locX, locZ).let { route ->
            assertTrue(route.success)
            assertTrue(route.alternative)
            assertTrue(route.isEmpty())
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `find valid route towards direction`(dir: Direction) {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = (srcX + dir.offX) to (srcZ + dir.offZ)
        val map = buildCollisionMap(srcX, srcZ, destX, destZ)
        val routeFinding = RouteFinding(map)
        routeFinding.findRoute(level = 0, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        routeFinding.findRoute(level = 1, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        routeFinding.findRoute(level = 2, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
        routeFinding.findRoute(level = 3, srcX, srcZ, destX, destZ).let { route ->
            assertEquals(1, route.size)
            assertEquals(destX, route.last().x)
            assertEquals(destZ, route.last().z)
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `fail route towards direction when blocked`(dir: Direction) {
        val (srcX, srcZ) = 3200 to 3200
        val (destX, destZ) = (srcX + dir.offX) to (srcZ + dir.offZ)
        val map =
            buildCollisionMap(srcX, srcZ, destX, destZ)
                .flag(destX, destZ, width = 1, length = 1, mask = CollisionFlag.BLOCK_WALK)
        val routeFinding = RouteFinding(map)
        routeFinding.findRoute(level = 0, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
            assertFalse(route.alternative)
        }
        routeFinding.findRoute(level = 1, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
            assertFalse(route.alternative)
        }
        routeFinding.findRoute(level = 2, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
            assertFalse(route.alternative)
        }
        routeFinding.findRoute(level = 3, srcX, srcZ, destX, destZ, moveNear = false).let { route ->
            assertTrue(route.failed)
            assertTrue(route.isEmpty())
            assertFalse(route.alternative)
        }
    }

    /**
     * Test that loc angles are taken into account within [RouteFinding.findRoute] and do not rely
     * on external modifications.
     *
     * For example, given the parameters of a loc in coordinates (3203, 3203) with a dimension of 3
     * x 1 (width x length), the following test should pass:
     *
     * Loc angles of [0] or [2]. (normal)
     *
     * ```
     * --------
     * --------
     * --------
     * ---ooo--
     * --o   o-
     * ---ooo--
     * --------
     * --------
     * ```
     *
     * Where:
     * - Area starts from bottom-left and makes its way to top-right. (3200,3200 - 3207,3207)
     * - ' ' (whitespace) are the tiles occupied by the rotated loc.
     * - 'o' are the valid tiles that the tail of [Route.waypoints] may return.
     * - '-' represents every other tile in the area. (in this case a zone, or 8x8 tile area)
     */
    @ParameterizedTest
    @ArgumentsSource(RotatedLocProvider::class)
    fun `validate route path against loc with angle 0 and 2`(
        locX: Int,
        locZ: Int,
        dimension: Dimension,
    ) {
        val (width, length) = dimension
        val (minX, minZ) = RouteCoordinates(locX - 16, locZ - 16)
        val (maxX, maxZ) = RouteCoordinates(locX + 16, locZ + 16)
        val map =
            buildCollisionMap(minX, minZ, maxX, maxZ)
                .flag(locX, locZ, width = width, length = length, mask = CollisionFlag.LOC)
        val routeFinding = RouteFinding(map)
        fun route(srcX: Int, srcZ: Int, angle: Int, blockAccessFlags: Int = 0): Route {
            return routeFinding.findRoute(
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = locX,
                destZ = locZ,
                destWidth = width,
                destLength = length,
                locAngle = angle,
                locShape = -2, //  Use rectangular exclusive strategy
                blockAccessFlags = blockAccessFlags,
            )
        }
        for (x in 0 until width) {
            // Test coming from south tiles.
            route(srcX = locX + x, srcZ = locZ - 3, angle = 0).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + x, locZ - 1), route.last())
            }
            route(srcX = locX + x, srcZ = locZ - 3, angle = 2).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + x, locZ - 1), route.last())
            }
            // Test coming from north tiles.
            route(srcX = locX + x, srcZ = locZ + length + 3, angle = 0).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + x, locZ + length), route.last())
            }
            route(srcX = locX + x, srcZ = locZ + length + 3, angle = 2).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + x, locZ + length), route.last())
            }
            // Test coming from south tiles with access blocked.
            route(srcX = locX + x, srcZ = locZ - 3, angle = 0, blockAccessFlags = SOUTH).let { route
                ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertNotEquals(locZ - 1, route.last().z)
            }
            route(srcX = locX + x, srcZ = locZ - 3, angle = 2, blockAccessFlags = NORTH).let { route
                ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertNotEquals(locZ - 1, route.last().z)
            }
            // Test coming from north tiles with access blocked.
            route(srcX = locX + x, srcZ = locZ + length + 3, angle = 0, blockAccessFlags = NORTH)
                .let { route ->
                    assertTrue(route.success)
                    assertFalse(route.alternative)
                    assertNotEquals(locZ + length, route.last().z)
                }
            route(srcX = locX + x, srcZ = locZ + length + 3, angle = 2, blockAccessFlags = SOUTH)
                .let { route ->
                    assertTrue(route.success)
                    assertFalse(route.alternative)
                    assertNotEquals(locZ + length, route.last().z)
                }
        }
        for (z in 0 until length) {
            // Test coming from west tiles.
            route(srcX = locX - 3, srcZ = locZ + z, angle = 0).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX - 1, locZ + z), route.last())
            }
            route(srcX = locX - 3, srcZ = locZ + z, angle = 2).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX - 1, locZ + z), route.last())
            }
            // Test coming from east tiles.
            route(srcX = locX + width + 3, srcZ = locZ + z, angle = 0).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + width, locZ + z), route.last())
            }
            route(srcX = locX + width + 3, srcZ = locZ + z, angle = 2).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + width, locZ + z), route.last())
            }
            // Test coming from west tiles with access blocked.
            route(srcX = locX - 3, srcZ = locZ + z, angle = 0, blockAccessFlags = WEST).let { route
                ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertNotEquals(locX - 1, route.last().x)
            }
            route(srcX = locX - 3, srcZ = locZ + z, angle = 2, blockAccessFlags = EAST).let { route
                ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertNotEquals(locX - 1, route.last().x)
            }
            // Test coming from east tiles with access blocked.
            route(srcX = locX + width + 3, srcZ = locZ + z, angle = 0, blockAccessFlags = EAST)
                .let { route ->
                    assertTrue(route.success)
                    assertFalse(route.alternative)
                    assertNotEquals(locX + width, route.last().x)
                }
            route(srcX = locX + width + 3, srcZ = locZ + z, angle = 2, blockAccessFlags = WEST)
                .let { route ->
                    assertTrue(route.success)
                    assertFalse(route.alternative)
                    assertNotEquals(locX + width, route.last().x)
                }
        }
    }

    /**
     * Test that loc angles are taken into account within [RouteFinding.findRoute] and do not rely
     * on external modifications.
     *
     * For example, given the parameters of a loc in coordinates (3203, 3203) with a dimension of 3
     * x 1 (width x length), the following test should pass:
     *
     * Loc angles of [1] or [3]. (flipped)
     *
     * ```
     * --------
     * ---o----
     * --o o---
     * --o o---
     * --o o---
     * ---o----
     * --------
     * --------
     * ```
     *
     * Where:
     * - Area starts from bottom-left and makes its way to top-right. (3200,3200 - 3207,3207)
     * - ' ' (whitespace) are the tiles occupied by the rotated loc.
     * - 'o' are the valid tiles that the tail of [Route.waypoints] may return.
     * - '-' represents every other tile in the area. (in this case a zone, or 8x8 tile area)
     */
    @ParameterizedTest
    @ArgumentsSource(RotatedLocProvider::class)
    fun `validate route path against loc with angle 1 and 3`(
        locX: Int,
        locZ: Int,
        dimension: Dimension,
    ) {
        val (width, length) = dimension
        val (minX, minZ) = RouteCoordinates(locX - 16, locZ - 16)
        val (maxX, maxZ) = RouteCoordinates(locX + 16, locZ + 16)
        val map =
            buildCollisionMap(minX, minZ, maxX, maxZ)
                .flag(locX, locZ, width = length, length = width, mask = CollisionFlag.LOC)
        val routeFinding = RouteFinding(map)
        fun route(srcX: Int, srcZ: Int, angle: Int, blockAccessFlags: Int = 0): Route {
            return routeFinding.findRoute(
                level = 0,
                srcX = srcX,
                srcZ = srcZ,
                destX = locX,
                destZ = locZ,
                destWidth = width,
                destLength = length,
                locAngle = angle,
                locShape = -2, //  Use rectangular exclusive strategy
                blockAccessFlags = blockAccessFlags,
            )
        }
        for (x in 0 until length) {
            // Test coming from south tiles.
            route(srcX = locX + x, srcZ = locZ - 3, angle = 1).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + x, locZ - 1), route.last())
            }
            route(srcX = locX + x, srcZ = locZ - 3, angle = 3).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + x, locZ - 1), route.last())
            }
            // Test coming from north tiles.
            route(srcX = locX + x, srcZ = locZ + width + 3, angle = 1).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + x, locZ + width), route.last())
            }
            route(srcX = locX + x, srcZ = locZ + width + 3, angle = 3).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + x, locZ + width), route.last())
            }
            // Test coming from south tiles with access blocked.
            route(srcX = locX + x, srcZ = locZ - 3, angle = 1, blockAccessFlags = EAST).let { route
                ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertNotEquals(locZ - 1, route.last().z)
            }
            route(srcX = locX + x, srcZ = locZ - 3, angle = 3, blockAccessFlags = WEST).let { route
                ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertNotEquals(locZ - 1, route.last().z)
            }
            // Test coming from north tiles with access blocked.
            route(srcX = locX + x, srcZ = locZ + width + 3, angle = 1, blockAccessFlags = WEST)
                .let { route ->
                    assertTrue(route.success)
                    assertFalse(route.alternative)
                    assertNotEquals(locZ + width, route.last().z)
                }
            route(srcX = locX + x, srcZ = locZ + width + 3, angle = 3, blockAccessFlags = EAST)
                .let { route ->
                    assertTrue(route.success)
                    assertFalse(route.alternative)
                    assertNotEquals(locZ + width, route.last().z)
                }
        }
        for (z in 0 until width) {
            // Test coming from west tiles.
            route(srcX = locX - 3, srcZ = locZ + z, angle = 1).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX - 1, locZ + z), route.last())
            }
            route(srcX = locX - 3, srcZ = locZ + z, angle = 3).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX - 1, locZ + z), route.last())
            }
            // Test coming from east tiles.
            route(srcX = locX + length + 3, srcZ = locZ + z, angle = 1).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + length, locZ + z), route.last())
            }
            route(srcX = locX + length + 3, srcZ = locZ + z, angle = 3).let { route ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertEquals(RouteCoordinates(locX + length, locZ + z), route.last())
            }
            // Test coming from west tiles with access blocked.
            route(srcX = locX - 3, srcZ = locZ + z, angle = 1, blockAccessFlags = SOUTH).let { route
                ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertNotEquals(locX - 1, route.last().x)
            }
            route(srcX = locX - 3, srcZ = locZ + z, angle = 3, blockAccessFlags = NORTH).let { route
                ->
                assertTrue(route.success)
                assertFalse(route.alternative)
                assertNotEquals(locX - 1, route.last().x)
            }
            // Test coming from east tiles with access blocked.
            route(srcX = locX + length + 3, srcZ = locZ + z, angle = 1, blockAccessFlags = NORTH)
                .let { route ->
                    assertTrue(route.success)
                    assertFalse(route.alternative)
                    assertNotEquals(locX + length, route.last().x)
                }
            route(srcX = locX + length + 3, srcZ = locZ + z, angle = 3, blockAccessFlags = SOUTH)
                .let { route ->
                    assertTrue(route.success)
                    assertFalse(route.alternative)
                    assertNotEquals(locX + length, route.last().x)
                }
        }
    }
}
