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
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.game.pathfinder.flag.CollisionFlag.OBJECT_PROJECTILE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER
import kotlin.math.abs

public class LineValidator(private val flags: CollisionFlagMap) {

    public fun hasLineOfSight(
        level: Int,
        srcX: Int,
        srcY: Int,
        destX: Int,
        destY: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0
    ): Boolean {
        return rayCast(
            level = level,
            srcX = srcX,
            srcY = srcY,
            destX = destX,
            destY = destY,
            srcSize = srcSize,
            destWidth = destWidth,
            destHeight = destHeight,
            flagWest = SIGHT_BLOCKED_WEST,
            flagEast = SIGHT_BLOCKED_EAST,
            flagSouth = SIGHT_BLOCKED_SOUTH,
            flagNorth = SIGHT_BLOCKED_NORTH,
            los = true
        )
    }

    public fun hasLineOfWalk(
        level: Int,
        srcX: Int,
        srcY: Int,
        destX: Int,
        destY: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0
    ): Boolean {
        return rayCast(
            level = level,
            srcX = srcX,
            srcY = srcY,
            destX = destX,
            destY = destY,
            srcSize = srcSize,
            destWidth = destWidth,
            destHeight = destHeight,
            flagWest = WALK_BLOCKED_WEST,
            flagEast = WALK_BLOCKED_EAST,
            flagSouth = WALK_BLOCKED_SOUTH,
            flagNorth = WALK_BLOCKED_NORTH,
            los = false
        )
    }

    private fun rayCast(
        level: Int,
        srcX: Int,
        srcY: Int,
        destX: Int,
        destY: Int,
        srcSize: Int,
        destWidth: Int,
        destHeight: Int,
        flagWest: Int,
        flagEast: Int,
        flagSouth: Int,
        flagNorth: Int,
        los: Boolean
    ): Boolean {
        val startX = coordinate(srcX, destX, srcSize)
        val startY = coordinate(srcY, destY, srcSize)

        if (los && flags.isFlagged(startX, startY, level, CollisionFlag.OBJECT)) {
            return false
        }

        val endX = coordinate(destX, srcX, destWidth)
        val endY = coordinate(destY, srcY, destHeight)

        if (startX == endX && startY == endY) {
            return true
        }

        val deltaX = endX - startX
        val deltaY = endY - startY

        val travelEast = deltaX >= 0
        val travelNorth = deltaY >= 0

        var xFlags = if (travelEast) flagWest else flagEast
        var yFlags = if (travelNorth) flagSouth else flagNorth

        if (abs(deltaX) > abs(deltaY)) {
            val offsetX = if (travelEast) 1 else -1
            val offsetY = if (travelNorth) 0 else -1

            var scaledY = scaleUp(startY) + HALF_TILE + offsetY
            val tangent = scaleUp(deltaY) / abs(deltaX)

            var currX = startX
            while (currX != endX) {
                currX += offsetX
                val currY = scaleDown(scaledY)

                if (los && currX == endX && currY == endY) xFlags = xFlags and OBJECT_PROJECTILE_BLOCKER.inv()
                if (flags.isFlagged(currX, currY, level, xFlags)) {
                    return false
                }

                scaledY += tangent

                val nextY = scaleDown(scaledY)
                if (los && currX == endX && nextY == endY) yFlags = yFlags and OBJECT_PROJECTILE_BLOCKER.inv()
                if (nextY != currY && flags.isFlagged(currX, nextY, level, yFlags)) {
                    return false
                }
            }
        } else {
            val offsetX = if (travelEast) 0 else -1
            val offsetY = if (travelNorth) 1 else -1

            var scaledX = scaleUp(startX) + HALF_TILE + offsetX
            val tangent = scaleUp(deltaX) / abs(deltaY)

            var currY = startY
            while (currY != endY) {
                currY += offsetY
                val currX = scaleDown(scaledX)
                if (los && currX == endX && currY == endY) yFlags = yFlags and OBJECT_PROJECTILE_BLOCKER.inv()
                if (flags.isFlagged(currX, currY, level, yFlags)) {
                    return false
                }

                scaledX += tangent

                val nextX = scaleDown(scaledX)
                if (los && nextX == endX && currY == endY) xFlags = xFlags and OBJECT_PROJECTILE_BLOCKER.inv()
                if (nextX != currX && flags.isFlagged(nextX, currY, level, xFlags)) {
                    return false
                }
            }
        }
        return true
    }

    private fun coordinate(a: Int, b: Int, size: Int): Int {
        return when {
            a >= b -> a
            a + size - 1 <= b -> a + size - 1
            else -> b
        }
    }

    private fun CollisionFlagMap.isFlagged(
        x: Int,
        y: Int,
        level: Int,
        flags: Int
    ): Boolean = (this[x, y, level] and flags) != 0

    private companion object {

        private const val SIGHT_BLOCKED_NORTH = OBJECT_PROJECTILE_BLOCKER or WALL_NORTH_PROJECTILE_BLOCKER
        private const val SIGHT_BLOCKED_EAST = OBJECT_PROJECTILE_BLOCKER or WALL_EAST_PROJECTILE_BLOCKER
        private const val SIGHT_BLOCKED_SOUTH = OBJECT_PROJECTILE_BLOCKER or WALL_SOUTH_PROJECTILE_BLOCKER
        private const val SIGHT_BLOCKED_WEST = OBJECT_PROJECTILE_BLOCKER or WALL_WEST_PROJECTILE_BLOCKER

        private const val WALK_BLOCKED_NORTH =
            CollisionFlag.WALL_NORTH or CollisionFlag.OBJECT or CollisionFlag.FLOOR_DECORATION or CollisionFlag.FLOOR
        private const val WALK_BLOCKED_EAST =
            CollisionFlag.WALL_EAST or CollisionFlag.OBJECT or CollisionFlag.FLOOR_DECORATION or CollisionFlag.FLOOR
        private const val WALK_BLOCKED_SOUTH =
            CollisionFlag.WALL_SOUTH or CollisionFlag.OBJECT or CollisionFlag.FLOOR_DECORATION or CollisionFlag.FLOOR
        private const val WALK_BLOCKED_WEST =
            CollisionFlag.WALL_WEST or CollisionFlag.OBJECT or CollisionFlag.FLOOR_DECORATION or CollisionFlag.FLOOR

        private const val SCALE = 16
        private val HALF_TILE = scaleUp(tiles = 1) / 2

        private fun scaleUp(tiles: Int) = tiles shl SCALE

        private fun scaleDown(tiles: Int) = tiles ushr SCALE
    }
}
