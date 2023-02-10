@file:Suppress("DuplicatedCode")

package org.rsmod.game.pathfinder

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.collision.CollisionStrategies
import org.rsmod.game.pathfinder.collision.CollisionStrategy
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_AND_SOUTH_EAST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_AND_SOUTH_EAST_ROUTE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_EAST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_EAST_AND_WEST_ROUTE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_WEST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_WEST_ROUTE_BLOCKER
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_SOUTH_EAST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_SOUTH_EAST_AND_WEST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_SOUTH_EAST_ROUTE_BLOCKER
import org.rsmod.game.pathfinder.flag.DirectionFlag
import org.rsmod.game.pathfinder.reach.ReachStrategy
import java.util.Arrays
import kotlin.collections.ArrayDeque

private const val DEFAULT_RESET_ON_SEARCH = true
private const val DEFAULT_SEARCH_MAP_SIZE = 128
private const val DEFAULT_RING_BUFFER_SIZE = 4096
private const val DEFAULT_DISTANCE_VALUE = 99_999_999
private const val DEFAULT_SRC_DIRECTION_VALUE = 99
private const val MAX_ALTERNATIVE_ROUTE_LOWEST_COST = 1000
private const val MAX_ALTERNATIVE_ROUTE_SEEK_RANGE = 100
private const val MAX_ALTERNATIVE_ROUTE_DISTANCE_FROM_DESTINATION = 10
private const val DEFAULT_USE_ROUTE_BLOCKER_FLAGS = false

private const val DEFAULT_SRC_SIZE = 1
private const val DEFAULT_DEST_WIDTH = 1
private const val DEFAULT_DEST_HEIGHT = 1
private const val DEFAULT_MAX_TURNS = 25
private const val DEFAULT_OBJ_ROT = 10
private const val DEFAULT_OBJ_SHAPE = -1
private const val DEFAULT_ACCESS_BITMASK = 0
private const val DEFAULT_MOVE_NEAR_FLAG = true

public class PathFinder(
    private val flags: CollisionFlagMap,
    private val resetOnSearch: Boolean = DEFAULT_RESET_ON_SEARCH,
    private val searchMapSize: Int = DEFAULT_SEARCH_MAP_SIZE,
    private val ringBufferSize: Int = DEFAULT_RING_BUFFER_SIZE,
    private val useRouteBlockerFlags: Boolean = DEFAULT_USE_ROUTE_BLOCKER_FLAGS
) {

    private val directions = IntArray(searchMapSize * searchMapSize)
    private val distances = IntArray(searchMapSize * searchMapSize) { DEFAULT_DISTANCE_VALUE }
    private val validLocalX = IntArray(ringBufferSize)
    private val validLocalY = IntArray(ringBufferSize)
    private var currLocalX = 0
    private var currLocalY = 0
    private var bufReaderIndex = 0
    private var bufWriterIndex = 0

    public fun findPath(
        level: Int,
        srcX: Int,
        srcY: Int,
        destX: Int,
        destY: Int,
        srcSize: Int = DEFAULT_SRC_SIZE,
        destWidth: Int = DEFAULT_DEST_WIDTH,
        destHeight: Int = DEFAULT_DEST_HEIGHT,
        objRot: Int = DEFAULT_OBJ_ROT,
        objShape: Int = DEFAULT_OBJ_SHAPE,
        moveNear: Boolean = DEFAULT_MOVE_NEAR_FLAG,
        accessBitMask: Int = DEFAULT_ACCESS_BITMASK,
        maxTurns: Int = DEFAULT_MAX_TURNS,
        collision: CollisionStrategy = CollisionStrategies.Normal
    ): Route {
        /*
         * Functionality relies on coordinates being within the
         * given boundaries.
         */
        require(srcX in 0..0x7FFF && srcY in 0..0x7FFF)
        require(destX in 0..0x7FFF && destY in 0..0x7FFF)
        require(level in 0..0x3)
        if (resetOnSearch) reset()
        val baseX = srcX - (searchMapSize / 2)
        val baseY = srcY - (searchMapSize / 2)
        val localSrcX = srcX - baseX
        val localSrcY = srcY - baseY
        val localDestX = destX - baseX
        val localDestY = destY - baseY
        appendDirection(localSrcX, localSrcY, DEFAULT_SRC_DIRECTION_VALUE, 0)
        val pathFound: Boolean = if (useRouteBlockerFlags) {
            when (srcSize) {
                1 -> findRouteBlockerPath1(
                    baseX,
                    baseY,
                    level,
                    localDestX,
                    localDestY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask,
                    collision
                )
                2 -> findRouteBlockerPath2(
                    baseX,
                    baseY,
                    level,
                    localDestX,
                    localDestY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask,
                    collision
                )
                else -> findRouteBlockerPathN(
                    baseX,
                    baseY,
                    level,
                    localDestX,
                    localDestY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask,
                    collision
                )
            }
        } else {
            when (srcSize) {
                1 -> findPath1(
                    baseX,
                    baseY,
                    level,
                    localDestX,
                    localDestY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask,
                    collision
                )
                2 -> findPath2(
                    baseX,
                    baseY,
                    level,
                    localDestX,
                    localDestY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask,
                    collision
                )
                else -> findPathN(
                    baseX,
                    baseY,
                    level,
                    localDestX,
                    localDestY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask,
                    collision
                )
            }
        }
        if (!pathFound) {
            if (!moveNear) {
                return FAILED_ROUTE
            }
            if (!findClosestApproachPoint(localSrcX, localSrcY, localDestX, localDestY, destWidth, destHeight)) {
                return FAILED_ROUTE
            }
        }
        val anchors = ArrayDeque<RouteCoordinates>(maxTurns + 1)
        var nextDir = directions[currLocalX, currLocalY]
        var currDir = -1
        for (i in directions.indices) {
            if (currLocalX == localSrcX && currLocalY == localSrcY) {
                break
            }
            if (currDir != nextDir) {
                currDir = nextDir
                if (anchors.size >= maxTurns) anchors.removeLast()
                val coords = RouteCoordinates(baseX + currLocalX, baseY + currLocalY)
                anchors.addFirst(coords)
            }
            if ((currDir and DirectionFlag.EAST) != 0) {
                currLocalX++
            } else if ((currDir and DirectionFlag.WEST) != 0) {
                currLocalX--
            }
            if ((currDir and DirectionFlag.NORTH) != 0) {
                currLocalY++
            } else if ((currDir and DirectionFlag.SOUTH) != 0) {
                currLocalY--
            }
            nextDir = directions[currLocalX, currLocalY]
        }
        return Route(anchors, alternative = !pathFound, success = true)
    }

    private fun findPath1(
        baseX: Int,
        baseY: Int,
        level: Int,
        localDestX: Int,
        localDestY: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        objRot: Int,
        objShape: Int,
        accessBitMask: Int,
        collision: CollisionStrategy
    ): Boolean {
        var x: Int
        var y: Int
        var clipFlag: Int
        var dirFlag: Int
        val relativeSearchSize = searchMapSize - 1
        while (bufWriterIndex != bufReaderIndex) {
            currLocalX = validLocalX[bufReaderIndex]
            currLocalY = validLocalY[bufReaderIndex]
            bufReaderIndex = (bufReaderIndex + 1) and (ringBufferSize - 1)

            if (ReachStrategy.reached(
                    flags,
                    currLocalX + baseX,
                    currLocalY + baseY,
                    level,
                    localDestX + baseX,
                    localDestY + baseY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask
                )
            ) {
                return true
            }

            val nextDistance = distances[currLocalX, currLocalY] + 1

            /* east to west */
            x = currLocalX - 1
            y = currLocalY
            clipFlag = CollisionFlag.BLOCK_WEST
            dirFlag = DirectionFlag.EAST
            if (
                currLocalX > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], clipFlag)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* west to east */
            x = currLocalX + 1
            y = currLocalY
            clipFlag = CollisionFlag.BLOCK_EAST
            dirFlag = DirectionFlag.WEST
            if (
                currLocalX < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], clipFlag)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north to south  */
            x = currLocalX
            y = currLocalY - 1
            clipFlag = CollisionFlag.BLOCK_SOUTH
            dirFlag = DirectionFlag.NORTH
            if (
                currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], clipFlag)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south to north */
            x = currLocalX
            y = currLocalY + 1
            clipFlag = CollisionFlag.BLOCK_NORTH
            dirFlag = DirectionFlag.SOUTH
            if (
                currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], clipFlag)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north-east to south-west */
            x = currLocalX - 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_EAST
            if (
                currLocalX > 0 && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], CollisionFlag.BLOCK_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_SOUTH)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north-west to south-east */
            x = currLocalX + 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], BLOCK_SOUTH_EAST) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], CollisionFlag.BLOCK_EAST) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_SOUTH)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south-east to north-west */
            x = currLocalX - 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_EAST
            if (
                currLocalX > 0 && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], BLOCK_NORTH_WEST) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], CollisionFlag.BLOCK_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_NORTH)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south-west to north-east */
            x = currLocalX + 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], BLOCK_NORTH_EAST) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], CollisionFlag.BLOCK_EAST) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_NORTH)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }
        }
        return false
    }

    private fun findPath2(
        baseX: Int,
        baseY: Int,
        level: Int,
        localDestX: Int,
        localDestY: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        objRot: Int,
        objShape: Int,
        accessBitMask: Int,
        collision: CollisionStrategy
    ): Boolean {
        var x: Int
        var y: Int
        var dirFlag: Int
        val relativeSearchSize = searchMapSize - 2
        while (bufWriterIndex != bufReaderIndex) {
            currLocalX = validLocalX[bufReaderIndex]
            currLocalY = validLocalY[bufReaderIndex]
            bufReaderIndex = (bufReaderIndex + 1) and (ringBufferSize - 1)

            if (ReachStrategy.reached(
                    flags,
                    currLocalX + baseX,
                    currLocalY + baseY,
                    level,
                    localDestX + baseX,
                    localDestY + baseY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask
                )
            ) {
                return true
            }

            val nextDistance = distances[currLocalX, currLocalY] + 1

            /* east to west */
            x = currLocalX - 1
            y = currLocalY
            dirFlag = DirectionFlag.EAST
            if (
                currLocalX > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + 1, level], BLOCK_NORTH_WEST)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* west to east */
            x = currLocalX + 1
            y = currLocalY
            dirFlag = DirectionFlag.WEST
            if (
                currLocalX < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, currLocalX + 2, y, level], BLOCK_SOUTH_EAST) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 2, currLocalY + 1, level], BLOCK_NORTH_EAST)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north to south  */
            x = currLocalX
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH
            if (
                currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 1, y, level], BLOCK_SOUTH_EAST)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south to north */
            x = currLocalX
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH
            if (
                currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + 2, level], BLOCK_NORTH_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 1, currLocalY + 2, level], BLOCK_NORTH_EAST)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north-east to south-west */
            x = currLocalX - 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_EAST
            if (
                currLocalX > 0 && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], BLOCK_NORTH_AND_SOUTH_EAST) &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_NORTH_EAST_AND_WEST)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north-west to south-east */
            x = currLocalX + 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_NORTH_EAST_AND_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 2, y, level], BLOCK_SOUTH_EAST) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 2, currLocalY, level], BLOCK_NORTH_AND_SOUTH_WEST)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south-east to north-west */
            x = currLocalX - 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_EAST
            if (
                currLocalX > 0 && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], BLOCK_NORTH_AND_SOUTH_EAST) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + 2, level], BLOCK_NORTH_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX, currLocalY + 2, level], BLOCK_SOUTH_EAST_AND_WEST)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south-west to north-east */
            x = currLocalX + 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + 2, level], BLOCK_SOUTH_EAST_AND_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 2, currLocalY + 2, level], BLOCK_NORTH_EAST) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 2, y, level], BLOCK_NORTH_AND_SOUTH_WEST)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }
        }
        return false
    }

    private fun findPathN(
        baseX: Int,
        baseY: Int,
        level: Int,
        localDestX: Int,
        localDestY: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        objRot: Int,
        objShape: Int,
        accessBitMask: Int,
        collision: CollisionStrategy
    ): Boolean {
        var x: Int
        var y: Int
        var dirFlag: Int
        val relativeSearchSize = searchMapSize - srcSize
        while (bufWriterIndex != bufReaderIndex) {
            currLocalX = validLocalX[bufReaderIndex]
            currLocalY = validLocalY[bufReaderIndex]
            bufReaderIndex = (bufReaderIndex + 1) and (ringBufferSize - 1)

            if (ReachStrategy.reached(
                    flags,
                    currLocalX + baseX,
                    currLocalY + baseY,
                    level,
                    localDestX + baseX,
                    localDestY + baseY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask
                )
            ) {
                return true
            }

            val nextDistance = distances[currLocalX, currLocalY] + 1

            /* east to west */
            x = currLocalX - 1
            y = currLocalY
            dirFlag = DirectionFlag.EAST
            if (
                currLocalX > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + srcSize - 1, level], BLOCK_NORTH_WEST)
            ) {
                val clipFlag = BLOCK_NORTH_AND_SOUTH_EAST
                val blocked = (1 until srcSize - 1).any {
                    !collision.canMove(flags[baseX, baseY, x, currLocalY + it, level], clipFlag)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* west to east */
            x = currLocalX + 1
            y = currLocalY
            dirFlag = DirectionFlag.WEST
            if (
                currLocalX < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, currLocalX + srcSize, y, level], BLOCK_SOUTH_EAST) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + srcSize, currLocalY + srcSize - 1, level],
                    BLOCK_NORTH_EAST
                )
            ) {
                val clipFlag = BLOCK_NORTH_AND_SOUTH_WEST
                val blocked = (1 until srcSize - 1).any {
                    !collision.canMove(flags[baseX, baseY, currLocalX + srcSize, currLocalY + it, level], clipFlag)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* north to south  */
            x = currLocalX
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH
            if (
                currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST) &&
                collision.canMove(flags[baseX, baseY, currLocalX + srcSize - 1, y, level], BLOCK_SOUTH_EAST)
            ) {
                val clipFlag = CollisionFlag.BLOCK_NORTH_EAST_AND_WEST
                val blocked = (1 until srcSize - 1).any {
                    !collision.canMove(flags[baseX, baseY, currLocalX + it, y, level], clipFlag)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* south to north */
            x = currLocalX
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH
            if (
                currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + srcSize, level], BLOCK_NORTH_WEST) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + srcSize - 1, currLocalY + srcSize, level],
                    BLOCK_NORTH_EAST
                )
            ) {
                val clipFlag = BLOCK_SOUTH_EAST_AND_WEST
                val blocked = (1 until srcSize - 1).any {
                    !collision.canMove(flags[baseX, baseY, x + it, currLocalY + srcSize, level], clipFlag)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* north-east to south-west */
            x = currLocalX - 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_EAST
            if (
                currLocalX > 0 && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST)
            ) {
                val clipFlag1 = BLOCK_NORTH_AND_SOUTH_EAST
                val clipFlag2 = CollisionFlag.BLOCK_NORTH_EAST_AND_WEST
                val blocked = (1 until srcSize).any {
                    !collision.canMove(flags[baseX, baseY, x, currLocalY + it - 1, level], clipFlag1) ||
                        !collision.canMove(flags[baseX, baseY, currLocalX + it - 1, y, level], clipFlag2)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* north-west to south-east */
            x = currLocalX + 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, currLocalX + srcSize, y, level], BLOCK_SOUTH_EAST)
            ) {
                val clipFlag1 = BLOCK_NORTH_AND_SOUTH_WEST
                val clipFlag2 = CollisionFlag.BLOCK_NORTH_EAST_AND_WEST
                val blocked = (1 until srcSize).any {
                    !collision.canMove(
                        flags[baseX, baseY, currLocalX + srcSize, currLocalY + it - 1, level],
                        clipFlag1
                    ) || !collision.canMove(flags[baseX, baseY, currLocalX + it, y, level], clipFlag2)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* south-east to north-west */
            x = currLocalX - 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_EAST
            if (
                currLocalX > 0 && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + srcSize, level], BLOCK_NORTH_WEST)
            ) {
                val clipFlag1 = BLOCK_NORTH_AND_SOUTH_EAST
                val clipFlag2 = BLOCK_SOUTH_EAST_AND_WEST
                val blocked = (1 until srcSize).any {
                    !collision.canMove(flags[baseX, baseY, x, currLocalY + it, level], clipFlag1) ||
                        !collision.canMove(
                            flags[baseX, baseY, currLocalX + it - 1, currLocalY + srcSize, level],
                            clipFlag2
                        )
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* south-west to north-east */
            x = currLocalX + 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + srcSize, currLocalY + srcSize, level],
                    BLOCK_NORTH_EAST
                )
            ) {
                val clipFlag1 = BLOCK_SOUTH_EAST_AND_WEST
                val clipFlag2 = BLOCK_NORTH_AND_SOUTH_WEST
                val blocked = (1 until srcSize).any {
                    !collision.canMove(flags[baseX, baseY, currLocalX + it, currLocalY + srcSize, level], clipFlag1) ||
                        !collision.canMove(flags[baseX, baseY, currLocalX + srcSize, currLocalY + it, level], clipFlag2)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }
        }
        return false
    }

    private fun findRouteBlockerPath1(
        baseX: Int,
        baseY: Int,
        level: Int,
        localDestX: Int,
        localDestY: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        objRot: Int,
        objShape: Int,
        accessBitMask: Int,
        collision: CollisionStrategy
    ): Boolean {
        var x: Int
        var y: Int
        var clipFlag: Int
        var dirFlag: Int
        val relativeSearchSize = searchMapSize - 1
        while (bufWriterIndex != bufReaderIndex) {
            currLocalX = validLocalX[bufReaderIndex]
            currLocalY = validLocalY[bufReaderIndex]
            bufReaderIndex = (bufReaderIndex + 1) and (ringBufferSize - 1)

            if (ReachStrategy.reached(
                    flags,
                    currLocalX + baseX,
                    currLocalY + baseY,
                    level,
                    localDestX + baseX,
                    localDestY + baseY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask
                )
            ) {
                return true
            }

            val nextDistance = distances[currLocalX, currLocalY] + 1

            /* east to west */
            x = currLocalX - 1
            y = currLocalY
            clipFlag = CollisionFlag.BLOCK_WEST_ROUTE_BLOCKER
            dirFlag = DirectionFlag.EAST
            if (
                currLocalX > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], clipFlag)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* west to east */
            x = currLocalX + 1
            y = currLocalY
            clipFlag = CollisionFlag.BLOCK_EAST_ROUTE_BLOCKER
            dirFlag = DirectionFlag.WEST
            if (
                currLocalX < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], clipFlag)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north to south  */
            x = currLocalX
            y = currLocalY - 1
            clipFlag = CollisionFlag.BLOCK_SOUTH_ROUTE_BLOCKER
            dirFlag = DirectionFlag.NORTH
            if (
                currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], clipFlag)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south to north */
            x = currLocalX
            y = currLocalY + 1
            clipFlag = CollisionFlag.BLOCK_NORTH_ROUTE_BLOCKER
            dirFlag = DirectionFlag.SOUTH
            if (
                currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], clipFlag)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north-east to south-west */
            x = currLocalX - 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_EAST
            if (
                currLocalX > 0 && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], CollisionFlag.BLOCK_WEST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_SOUTH_ROUTE_BLOCKER)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north-west to south-east */
            x = currLocalX + 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], BLOCK_SOUTH_EAST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], CollisionFlag.BLOCK_EAST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_SOUTH_ROUTE_BLOCKER)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south-east to north-west */
            x = currLocalX - 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_EAST
            if (
                currLocalX > 0 && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], BLOCK_NORTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], CollisionFlag.BLOCK_WEST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_NORTH_ROUTE_BLOCKER)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south-west to north-east */
            x = currLocalX + 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_NORTH_EAST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY, level], CollisionFlag.BLOCK_EAST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], CollisionFlag.BLOCK_NORTH_ROUTE_BLOCKER)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }
        }
        return false
    }

    private fun findRouteBlockerPath2(
        baseX: Int,
        baseY: Int,
        level: Int,
        localDestX: Int,
        localDestY: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        objRot: Int,
        objShape: Int,
        accessBitMask: Int,
        collision: CollisionStrategy
    ): Boolean {
        var x: Int
        var y: Int
        var dirFlag: Int
        val relativeSearchSize = searchMapSize - 2
        while (bufWriterIndex != bufReaderIndex) {
            currLocalX = validLocalX[bufReaderIndex]
            currLocalY = validLocalY[bufReaderIndex]
            bufReaderIndex = (bufReaderIndex + 1) and (ringBufferSize - 1)

            if (ReachStrategy.reached(
                    flags,
                    currLocalX + baseX,
                    currLocalY + baseY,
                    level,
                    localDestX + baseX,
                    localDestY + baseY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask
                )
            ) {
                return true
            }

            val nextDistance = distances[currLocalX, currLocalY] + 1

            /* east to west */
            x = currLocalX - 1
            y = currLocalY
            dirFlag = DirectionFlag.EAST
            if (
                currLocalX > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + 1, level], BLOCK_NORTH_WEST_ROUTE_BLOCKER)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* west to east */
            x = currLocalX + 1
            y = currLocalY
            dirFlag = DirectionFlag.WEST
            if (
                currLocalX < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, currLocalX + 2, y, level], BLOCK_SOUTH_EAST_ROUTE_BLOCKER) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + 2, currLocalY + 1, level],
                    CollisionFlag.BLOCK_NORTH_EAST_ROUTE_BLOCKER
                )
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north to south  */
            x = currLocalX
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH
            if (
                currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 1, y, level], BLOCK_SOUTH_EAST_ROUTE_BLOCKER)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south to north */
            x = currLocalX
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH
            if (
                currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + 2, level], BLOCK_NORTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + 1, currLocalY + 2, level],
                    CollisionFlag.BLOCK_NORTH_EAST_ROUTE_BLOCKER
                )
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north-east to south-west */
            x = currLocalX - 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_EAST
            if (
                currLocalX > 0 && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(
                    flags[baseX, baseY, x, currLocalY, level],
                    BLOCK_NORTH_AND_SOUTH_EAST_ROUTE_BLOCKER
                ) &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, currLocalX, y, level], BLOCK_NORTH_EAST_AND_WEST_ROUTE_BLOCKER)
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* north-west to south-east */
            x = currLocalX + 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], BLOCK_NORTH_EAST_AND_WEST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, currLocalX + 2, y, level], BLOCK_SOUTH_EAST_ROUTE_BLOCKER) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + 2, currLocalY, level],
                    CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST_ROUTE_BLOCKER
                )
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south-east to north-west */
            x = currLocalX - 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_EAST
            if (
                currLocalX > 0 && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], BLOCK_NORTH_AND_SOUTH_EAST_ROUTE_BLOCKER) &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + 2, level], BLOCK_NORTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX, currLocalY + 2, level],
                    CollisionFlag.BLOCK_SOUTH_EAST_AND_WEST_ROUTE_BLOCKER
                )
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }

            /* south-west to north-east */
            x = currLocalX + 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(
                    flags[baseX, baseY, x, currLocalY + 2, level],
                    CollisionFlag.BLOCK_SOUTH_EAST_AND_WEST_ROUTE_BLOCKER
                ) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + 2, currLocalY + 2, level],
                    CollisionFlag.BLOCK_NORTH_EAST_ROUTE_BLOCKER
                ) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + 2, y, level],
                    CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST_ROUTE_BLOCKER
                )
            ) {
                appendDirection(x, y, dirFlag, nextDistance)
            }
        }
        return false
    }

    private fun findRouteBlockerPathN(
        baseX: Int,
        baseY: Int,
        level: Int,
        localDestX: Int,
        localDestY: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        objRot: Int,
        objShape: Int,
        accessBitMask: Int,
        collision: CollisionStrategy
    ): Boolean {
        var x: Int
        var y: Int
        var dirFlag: Int
        val relativeSearchSize = searchMapSize - srcSize
        while (bufWriterIndex != bufReaderIndex) {
            currLocalX = validLocalX[bufReaderIndex]
            currLocalY = validLocalY[bufReaderIndex]
            bufReaderIndex = (bufReaderIndex + 1) and (ringBufferSize - 1)

            if (ReachStrategy.reached(
                    flags,
                    currLocalX + baseX,
                    currLocalY + baseY,
                    level,
                    localDestX + baseX,
                    localDestY + baseY,
                    destWidth,
                    destHeight,
                    srcSize,
                    objRot,
                    objShape,
                    accessBitMask
                )
            ) {
                return true
            }

            val nextDistance = distances[currLocalX, currLocalY] + 1

            /* east to west */
            x = currLocalX - 1
            y = currLocalY
            dirFlag = DirectionFlag.EAST
            if (
                currLocalX > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(
                    flags[baseX, baseY, x, currLocalY + srcSize - 1, level],
                    BLOCK_NORTH_WEST_ROUTE_BLOCKER
                )
            ) {
                val clipFlag = BLOCK_NORTH_AND_SOUTH_EAST_ROUTE_BLOCKER
                val blocked = (1 until srcSize - 1).any {
                    !collision.canMove(flags[baseX, baseY, x, currLocalY + it, level], clipFlag)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* west to east */
            x = currLocalX + 1
            y = currLocalY
            dirFlag = DirectionFlag.WEST
            if (
                currLocalX < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + srcSize, y, level],
                    BLOCK_SOUTH_EAST_ROUTE_BLOCKER
                ) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + srcSize, currLocalY + srcSize - 1, level],
                    CollisionFlag.BLOCK_NORTH_EAST_ROUTE_BLOCKER
                )
            ) {
                val clipFlag = CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST_ROUTE_BLOCKER
                val blocked = (1 until srcSize - 1).any {
                    !collision.canMove(flags[baseX, baseY, currLocalX + srcSize, currLocalY + it, level], clipFlag)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* north to south  */
            x = currLocalX
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH
            if (
                currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST_ROUTE_BLOCKER) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + srcSize - 1, y, level],
                    BLOCK_SOUTH_EAST_ROUTE_BLOCKER
                )
            ) {
                val clipFlag = BLOCK_NORTH_EAST_AND_WEST_ROUTE_BLOCKER
                val blocked = (1 until srcSize - 1).any {
                    !collision.canMove(flags[baseX, baseY, currLocalX + it, y, level], clipFlag)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* south to north */
            x = currLocalX
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH
            if (
                currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(
                    flags[baseX, baseY, x, currLocalY + srcSize, level],
                    BLOCK_NORTH_WEST_ROUTE_BLOCKER
                ) &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + srcSize - 1, currLocalY + srcSize, level],
                    CollisionFlag.BLOCK_NORTH_EAST_ROUTE_BLOCKER
                )
            ) {
                val clipFlag = CollisionFlag.BLOCK_SOUTH_EAST_AND_WEST_ROUTE_BLOCKER
                val blocked =
                    (1 until srcSize - 1).any {
                        !collision.canMove(flags[baseX, baseY, x + it, currLocalY + srcSize, level], clipFlag)
                    }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* north-east to south-west */
            x = currLocalX - 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_EAST
            if (
                currLocalX > 0 && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, y, level], CollisionFlag.BLOCK_SOUTH_WEST_ROUTE_BLOCKER)
            ) {
                val clipFlag1 = BLOCK_NORTH_AND_SOUTH_EAST_ROUTE_BLOCKER
                val clipFlag2 = BLOCK_NORTH_EAST_AND_WEST_ROUTE_BLOCKER
                val blocked = (1 until srcSize).any {
                    !collision.canMove(flags[baseX, baseY, x, currLocalY + it - 1, level], clipFlag1) ||
                        !collision.canMove(flags[baseX, baseY, currLocalX + it - 1, y, level], clipFlag2)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* north-west to south-east */
            x = currLocalX + 1
            y = currLocalY - 1
            dirFlag = DirectionFlag.NORTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY > 0 && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, currLocalX + srcSize, y, level], BLOCK_SOUTH_EAST_ROUTE_BLOCKER)
            ) {
                val clipFlag1 = CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST_ROUTE_BLOCKER
                val clipFlag2 = BLOCK_NORTH_EAST_AND_WEST_ROUTE_BLOCKER
                val blocked = (1 until srcSize).any {
                    !collision.canMove(
                        flags[baseX, baseY, currLocalX + srcSize, currLocalY + it - 1, level],
                        clipFlag1
                    ) || !collision.canMove(flags[baseX, baseY, currLocalX + it, y, level], clipFlag2)
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* south-east to north-west */
            x = currLocalX - 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_EAST
            if (
                currLocalX > 0 && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(flags[baseX, baseY, x, currLocalY + srcSize, level], BLOCK_NORTH_WEST_ROUTE_BLOCKER)
            ) {
                val clipFlag1 = BLOCK_NORTH_AND_SOUTH_EAST_ROUTE_BLOCKER
                val clipFlag2 = CollisionFlag.BLOCK_SOUTH_EAST_AND_WEST_ROUTE_BLOCKER
                val blocked = (1 until srcSize).any {
                    !collision.canMove(flags[baseX, baseY, x, currLocalY + it, level], clipFlag1) ||
                        !collision.canMove(
                            flags[baseX, baseY, currLocalX + it - 1, currLocalY + srcSize, level],
                            clipFlag2
                        )
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }

            /* south-west to north-east */
            x = currLocalX + 1
            y = currLocalY + 1
            dirFlag = DirectionFlag.SOUTH_WEST
            if (
                currLocalX < relativeSearchSize && currLocalY < relativeSearchSize && directions[x, y] == 0 &&
                collision.canMove(
                    flags[baseX, baseY, currLocalX + srcSize, currLocalY + srcSize, level],
                    CollisionFlag.BLOCK_NORTH_EAST_ROUTE_BLOCKER
                )
            ) {
                val clipFlag1 = CollisionFlag.BLOCK_SOUTH_EAST_AND_WEST_ROUTE_BLOCKER
                val clipFlag2 = CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST_ROUTE_BLOCKER
                val blocked = (1 until srcSize).any {
                    !collision.canMove(flags[baseX, baseY, currLocalX + it, currLocalY + srcSize, level], clipFlag1) ||
                        !collision.canMove(
                            flags[baseX, baseY, currLocalX + srcSize, currLocalY + it, level],
                            clipFlag2
                        )
                }
                if (!blocked) {
                    appendDirection(x, y, dirFlag, nextDistance)
                }
            }
        }
        return false
    }

    private fun findClosestApproachPoint(
        localSrcX: Int,
        localSrcY: Int,
        localDestX: Int,
        localDestY: Int,
        width: Int,
        length: Int
    ): Boolean {
        var lowestCost = MAX_ALTERNATIVE_ROUTE_LOWEST_COST
        var maxAlternativePath = MAX_ALTERNATIVE_ROUTE_SEEK_RANGE
        val alternativeRouteRange = MAX_ALTERNATIVE_ROUTE_DISTANCE_FROM_DESTINATION
        val radiusX = localDestX - alternativeRouteRange..localDestX + alternativeRouteRange
        val radiusY = localDestY - alternativeRouteRange..localDestY + alternativeRouteRange
        for (x in radiusX) {
            for (y in radiusY) {
                if (
                    x !in 0 until searchMapSize ||
                    y !in 0 until searchMapSize ||
                    distances[x, y] >= MAX_ALTERNATIVE_ROUTE_SEEK_RANGE
                ) {
                    continue
                }

                val dx = if (x < localDestX) {
                    localDestX - x
                } else if (x > localDestX + width - 1) {
                    x - (width + localDestX - 1)
                } else {
                    0
                }

                val dy = if (y < localDestY) {
                    localDestY - y
                } else if (y > localDestY + length - 1) {
                    y - (localDestY + length - 1)
                } else {
                    0
                }
                val cost = dx * dx + dy * dy
                if (cost < lowestCost || (cost == lowestCost && maxAlternativePath > distances[x, y])) {
                    currLocalX = x
                    currLocalY = y
                    lowestCost = cost
                    maxAlternativePath = distances[x, y]
                }
            }
        }
        return !(lowestCost == MAX_ALTERNATIVE_ROUTE_LOWEST_COST || localSrcX == currLocalX && localSrcY == currLocalY)
    }

    private fun appendDirection(x: Int, y: Int, direction: Int, distance: Int) {
        val index = (y * searchMapSize) + x
        directions[index] = direction
        distances[index] = distance
        validLocalX[bufWriterIndex] = x
        validLocalY[bufWriterIndex] = y
        bufWriterIndex = (bufWriterIndex + 1) and (ringBufferSize - 1)
    }

    private fun reset() {
        Arrays.fill(directions, 0)
        Arrays.fill(distances, DEFAULT_DISTANCE_VALUE)
        bufReaderIndex = 0
        bufWriterIndex = 0
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline operator fun IntArray.get(x: Int, y: Int): Int {
        val index = (y * searchMapSize) + x
        return this[index]
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline operator fun CollisionFlagMap.get(
        baseX: Int,
        baseY: Int,
        localX: Int,
        localY: Int,
        level: Int
    ): Int {
        val x = baseX + localX
        val y = baseY + localY
        return this[x, y, level]
    }

    public companion object {

        private val FAILED_ROUTE = Route(emptyList(), alternative = false, success = false)

        /**
         * Calculates coordinates for [sourceX]/[sourceY] to move to interact with [targetX]/[targetY]
         * We first determine the cardinal direction of the source relative to the target by comparing if
         * the source lies to the left or right of diagonal \ and anti-diagonal / lines.
         * \ <= North <= /
         *  +------------+  >
         *  |            |  East
         *  +------------+  <
         * / <= South <= \
         * We then further bisect the area into three section relative to the south-west tile (zero):
         * 1. Greater than zero: follow their diagonal until the target side is reached (clamped at the furthest most tile)
         * 2. Less than zero: zero minus the size of the source
         * 3. Equal to zero: move directly towards zero / the south-west coordinate
         *
         * <  \ 0 /   <   /
         *     +---------+
         *     |         |
         *     +---------+
         * This method is equivalent to returning the last coordinate in a sequence of steps towards south-west when moving
         * ordinal then cardinally until entity side comes into contact with another.
         */
        public fun naiveDestination(
            sourceX: Int,
            sourceY: Int,
            sourceWidth: Int,
            sourceHeight: Int,
            targetX: Int,
            targetY: Int,
            targetWidth: Int,
            targetHeight: Int
        ): RouteCoordinates {
            val diagonal = (sourceX - targetX) + (sourceY - targetY)
            val anti = (sourceX - targetX) - (sourceY - targetY)
            val southWestClockwise = anti < 0
            val northWestClockwise = diagonal >= (targetHeight - 1) - (sourceWidth - 1)
            val northEastClockwise = anti > sourceWidth - sourceHeight
            val southEastClockwise = diagonal <= (targetWidth - 1) - (sourceHeight - 1)

            val target = RouteCoordinates(targetX, targetY)
            if (southWestClockwise && !northWestClockwise) {
                val offY = when { // West
                    diagonal >= -sourceWidth -> (diagonal + sourceWidth).coerceAtMost(targetHeight - 1)
                    anti > -sourceWidth -> -(sourceWidth + anti)
                    else -> 0
                }
                return target.translate(-sourceWidth, offY)
            } else if (northWestClockwise && !northEastClockwise) {
                val offX = when { // North
                    anti >= -targetHeight -> (anti + targetHeight).coerceAtMost(targetWidth - 1)
                    diagonal < targetHeight -> (diagonal - targetHeight).coerceAtLeast(-(sourceWidth - 1))
                    else -> 0
                }
                return target.translate(offX, targetHeight)
            } else if (northEastClockwise && !southEastClockwise) {
                val offY = when { // East
                    anti <= targetWidth -> targetHeight - anti
                    diagonal < targetWidth -> (diagonal - targetWidth).coerceAtLeast(-(sourceHeight - 1))
                    else -> 0
                }
                return target.translate(targetWidth, offY)
            } else {
                check(southEastClockwise && !southWestClockwise)
                val offX = when { // South
                    diagonal > -sourceHeight -> (diagonal + sourceHeight).coerceAtMost(targetWidth - 1)
                    anti < sourceHeight -> (anti - sourceHeight).coerceAtLeast(-(sourceHeight - 1))
                    else -> 0
                }
                return target.translate(offX, -sourceHeight)
            }
        }
    }
}
