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

import org.rsmod.game.pathfinder.LinePathFinder.Companion.HALF_TILE
import org.rsmod.game.pathfinder.LinePathFinder.Companion.SIGHT_BLOCKED_EAST
import org.rsmod.game.pathfinder.LinePathFinder.Companion.SIGHT_BLOCKED_NORTH
import org.rsmod.game.pathfinder.LinePathFinder.Companion.SIGHT_BLOCKED_SOUTH
import org.rsmod.game.pathfinder.LinePathFinder.Companion.SIGHT_BLOCKED_WEST
import org.rsmod.game.pathfinder.LinePathFinder.Companion.WALK_BLOCKED_EAST
import org.rsmod.game.pathfinder.LinePathFinder.Companion.WALK_BLOCKED_NORTH
import org.rsmod.game.pathfinder.LinePathFinder.Companion.WALK_BLOCKED_SOUTH
import org.rsmod.game.pathfinder.LinePathFinder.Companion.WALK_BLOCKED_WEST
import org.rsmod.game.pathfinder.LinePathFinder.Companion.coordinate
import org.rsmod.game.pathfinder.LinePathFinder.Companion.scaleDown
import org.rsmod.game.pathfinder.LinePathFinder.Companion.scaleUp
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag.OBJECT
import org.rsmod.game.pathfinder.flag.CollisionFlag.OBJECT_PROJECTILE_BLOCKER
import kotlin.math.abs

public class LineValidator(private val flags: CollisionFlagMap) {

    public fun hasLineOfSight(
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0,
        extraFlag: Int = 0
    ): Boolean {
        return rayCast(
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
    }

    public fun hasLineOfWalk(
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0,
        extraFlag: Int = 0
    ): Boolean {
        return rayCast(
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
    }

    @Suppress("DuplicatedCode")
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
    ): Boolean {
        val startX = coordinate(srcX, destX, srcWidth)
        val startZ = coordinate(srcZ, destZ, srcHeight)

        if (los && flags.isFlagged(startX, startZ, level, flagObject)) {
            return false
        }

        val endX = coordinate(destX, srcX, destWidth)
        val endZ = coordinate(destZ, srcZ, destHeight)

        if (startX == endX && startZ == endZ) {
            return true
        }

        val deltaX = endX - startX
        val deltaZ = endZ - startZ

        val travelEast = deltaX >= 0
        val travelNorth = deltaZ >= 0

        var xFlags = if (travelEast) flagWest else flagEast
        var zFlags = if (travelNorth) flagSouth else flagNorth

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
                    return false
                }

                scaledZ += tangent

                val nextZ = scaleDown(scaledZ)
                if (los && currX == endX && nextZ == endZ) zFlags = zFlags and flagProjectileBlocker.inv()
                if (nextZ != currZ && flags.isFlagged(currX, nextZ, level, zFlags)) {
                    return false
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
                    return false
                }

                scaledX += tangent

                val nextX = scaleDown(scaledX)
                if (los && nextX == endX && currZ == endZ) xFlags = xFlags and flagProjectileBlocker.inv()
                if (nextX != currX && flags.isFlagged(nextX, currZ, level, xFlags)) {
                    return false
                }
            }
        }
        return true
    }

    private companion object {

        private fun CollisionFlagMap.isFlagged(
            x: Int,
            z: Int,
            level: Int,
            flags: Int
        ): Boolean = (this[x, z, level] and flags) != 0
    }
}
