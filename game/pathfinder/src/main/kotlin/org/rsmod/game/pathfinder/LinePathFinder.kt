/*
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode")

package org.rsmod.game.pathfinder

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag.FLOOR
import org.rsmod.game.pathfinder.flag.CollisionFlag.FLOOR_DECORATION
import org.rsmod.game.pathfinder.flag.CollisionFlag.OBJECT
import org.rsmod.game.pathfinder.flag.CollisionFlag.OBJECT_PROJECTILE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_EAST
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_NORTH
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_SOUTH
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_WEST
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER
import kotlin.math.abs

public class LinePathFinder(private val flags: CollisionFlagMap) {

    public fun lineOfSight(
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0,
        extraFlag: Int = 0
    ): RayCast = rayCast(
        level = level,
        srcX = srcX,
        srcZ = srcZ,
        destX = destX,
        destZ = destZ,
        srcWidth = srcSize,
        srcHeight = srcSize,
        destWidth = destWidth,
        destHeight = destHeight,
        flagWest = SIGHT_BLOCKED_WEST or extraFlag,
        flagEast = SIGHT_BLOCKED_EAST or extraFlag,
        flagSouth = SIGHT_BLOCKED_SOUTH or extraFlag,
        flagNorth = SIGHT_BLOCKED_NORTH or extraFlag,
        flagObject = OBJECT or extraFlag,
        flagProjectileBlocker = OBJECT_PROJECTILE_BLOCKER or extraFlag,
        los = true
    )

    public fun lineOfWalk(
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0,
        extraFlag: Int = 0
    ): RayCast = rayCast(
        level = level,
        srcX = srcX,
        srcZ = srcZ,
        destX = destX,
        destZ = destZ,
        srcWidth = srcSize,
        srcHeight = srcSize,
        destWidth = destWidth,
        destHeight = destHeight,
        flagWest = WALK_BLOCKED_WEST or extraFlag,
        flagEast = WALK_BLOCKED_EAST or extraFlag,
        flagSouth = WALK_BLOCKED_SOUTH or extraFlag,
        flagNorth = WALK_BLOCKED_NORTH or extraFlag,
        flagObject = OBJECT or extraFlag,
        flagProjectileBlocker = OBJECT_PROJECTILE_BLOCKER or extraFlag,
        los = false
    )

    public fun rayCast(
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcWidth: Int,
        srcHeight: Int,
        destWidth: Int,
        destHeight: Int,
        flagWest: Int,
        flagEast: Int,
        flagSouth: Int,
        flagNorth: Int,
        flagObject: Int,
        flagProjectileBlocker: Int,
        los: Boolean
    ): RayCast {
        val startX = coordinate(srcX, destX, srcWidth)
        val startZ = coordinate(srcZ, destZ, srcHeight)

        if (los && flags.isFlagged(startX, startZ, level, flagObject)) {
            return RayCast.FAILED
        }

        val endX = coordinate(destX, srcX, destWidth)
        val endZ = coordinate(destZ, srcZ, destHeight)

        if (startX == endX && startZ == endZ) {
            return RayCast.EMPTY_SUCCESS
        }

        val deltaX = endX - startX
        val deltaZ = endZ - startZ

        val travelEast = deltaX >= 0
        val travelNorth = deltaZ >= 0

        var xFlags = if (travelEast) flagWest else flagEast
        var zFlags = if (travelNorth) flagSouth else flagNorth

        val coordinates = mutableListOf<RouteCoordinates>()
        if (abs(deltaX) > abs(deltaZ)) {
            val offsetX = if (travelEast) 1 else -1
            val offsetZ = if (travelNorth) 0 else -1

            var scaledZ = scaleUp(startZ) + HALF_TILE + offsetZ
            val tangent = scaleUp(deltaZ) / abs(deltaX)

            var currX = startX
            while (currX != endX) {
                currX += offsetX
                val currZ = scaleDown(scaledZ)

                if (los && currX == endX && currZ == endZ) xFlags = xFlags and flagProjectileBlocker.inv()
                if (flags.isFlagged(currX, currZ, level, xFlags)) {
                    return RayCast(coordinates, alternative = coordinates.isNotEmpty(), success = false)
                }
                coordinates += RouteCoordinates(currX, currZ, level)

                scaledZ += tangent

                val nextZ = scaleDown(scaledZ)
                if (los && currX == endX && nextZ == endZ) zFlags = zFlags and flagProjectileBlocker.inv()
                if (nextZ != currZ) {
                    if (flags.isFlagged(currX, nextZ, level, zFlags)) {
                        return RayCast(coordinates, alternative = coordinates.isNotEmpty(), success = false)
                    }
                    coordinates += RouteCoordinates(currX, nextZ, level)
                }
            }
        } else {
            val offsetX = if (travelEast) 0 else -1
            val offsetZ = if (travelNorth) 1 else -1

            var scaledX = scaleUp(startX) + HALF_TILE + offsetX
            val tangent = scaleUp(deltaX) / abs(deltaZ)

            var currZ = startZ
            while (currZ != endZ) {
                currZ += offsetZ
                val currX = scaleDown(scaledX)
                if (los && currX == endX && currZ == endZ) zFlags = zFlags and flagProjectileBlocker.inv()
                if (flags.isFlagged(currX, currZ, level, zFlags)) {
                    return RayCast(coordinates, alternative = coordinates.isNotEmpty(), success = false)
                }
                coordinates += RouteCoordinates(currX, currZ, level)

                scaledX += tangent

                val nextX = scaleDown(scaledX)
                if (los && nextX == endX && currZ == endZ) xFlags = xFlags and flagProjectileBlocker.inv()
                if (nextX != currX) {
                    if (flags.isFlagged(nextX, currZ, level, xFlags)) {
                        return RayCast(coordinates, alternative = coordinates.isNotEmpty(), success = false)
                    }
                    coordinates += RouteCoordinates(nextX, currZ, level)
                }
            }
        }
        return RayCast(coordinates, alternative = false, success = true)
    }

    public companion object {

        public const val SIGHT_BLOCKED_NORTH: Int = OBJECT_PROJECTILE_BLOCKER or WALL_NORTH_PROJECTILE_BLOCKER
        public const val SIGHT_BLOCKED_EAST: Int = OBJECT_PROJECTILE_BLOCKER or WALL_EAST_PROJECTILE_BLOCKER
        public const val SIGHT_BLOCKED_SOUTH: Int = OBJECT_PROJECTILE_BLOCKER or WALL_SOUTH_PROJECTILE_BLOCKER
        public const val SIGHT_BLOCKED_WEST: Int = OBJECT_PROJECTILE_BLOCKER or WALL_WEST_PROJECTILE_BLOCKER

        public const val WALK_BLOCKED_NORTH: Int = WALL_NORTH or OBJECT or FLOOR_DECORATION or FLOOR
        public const val WALK_BLOCKED_EAST: Int = WALL_EAST or OBJECT or FLOOR_DECORATION or FLOOR
        public const val WALK_BLOCKED_SOUTH: Int = WALL_SOUTH or OBJECT or FLOOR_DECORATION or FLOOR
        public const val WALK_BLOCKED_WEST: Int = WALL_WEST or OBJECT or FLOOR_DECORATION or FLOOR

        internal const val SCALE: Int = 16
        internal val HALF_TILE: Int = scaleUp(tiles = 1) / 2

        internal fun scaleUp(tiles: Int) = tiles shl SCALE

        internal fun scaleDown(tiles: Int) = tiles ushr SCALE

        internal fun coordinate(a: Int, b: Int, size: Int): Int = when {
            a >= b -> a
            a + size - 1 <= b -> a + size - 1
            else -> b
        }

        private fun CollisionFlagMap.isFlagged(
            x: Int,
            z: Int,
            level: Int,
            flags: Int
        ): Boolean = (this[x, z, level] and flags) != 0
    }
}
