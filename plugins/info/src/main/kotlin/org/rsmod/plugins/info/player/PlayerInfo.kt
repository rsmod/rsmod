package org.rsmod.plugins.info.player

import org.rsmod.plugins.info.BitBuffer
import org.rsmod.plugins.info.player.extended.ExtendedInfo
import org.rsmod.plugins.info.player.extended.ExtendedMetadata
import org.rsmod.plugins.info.player.extended3.ExtendedInfoStructure
import org.rsmod.plugins.info.player.extended3.ExtendedInfoStructureBuilder
import org.rsmod.plugins.info.player.extended3.ExtendedInfoStructureMap
import org.rsmod.plugins.info.player.model.Avatar
import org.rsmod.plugins.info.player.model.InfoClient
import java.nio.ByteBuffer
import java.util.Arrays

public class PlayerInfo(public val playerCapacity: Int) {

    init { require(playerCapacity <= MAX_PLAYER_CAPACITY) }

    /* current amount of online players to iterate */
    public var playerCount: Int = 0

    /* mapped to player index */
    public val clients: Array<InfoClient> = Array(playerCapacity) { InfoClient(playerCapacity) }

    /* ring buffer */
    public val avatars: Array<Avatar> = Array(playerCapacity) { Avatar() }

    private val extended: ExtendedInfoStructureMap = ExtendedInfoStructureMap()

    /* ring buffer */
    public val extendedInfo: Array<ExtendedInfo> = Array(playerCapacity) { ExtendedInfo() }

    public fun registerClient(playerIndex: Int) {
        val client = clients[playerIndex]
        client.viewDistance = DEFAULT_VIEW_DISTANCE.toByte()
        Arrays.fill(client.highRes, false)
        Arrays.fill(client.activityFlags, 0)
        client.highRes[playerIndex] = true
    }

    public fun add(playerIndex: Int, currCoords: Int, prevCoords: Int) {
        val ringBufIndex = playerCount++
        clients[playerIndex].ringBufIndex = ringBufIndex
        val avatar = avatars[ringBufIndex]
        avatar.playerIndex = playerIndex.toShort()
        avatar.currCoords = currCoords
        avatar.prevCoords = prevCoords
    }

    public fun clear() {
        playerCount = 0
    }

    public fun read(dest: ByteBuffer, playerIndex: Int) {
        dest.clear()
        putFully(dest, playerIndex)
        dest.flip()
    }

    public fun putFully(dest: ByteBuffer, playerIndex: Int) {
        val client = clients[playerIndex]
        val avatar = avatars[client.ringBufIndex]
        val extended = ExtendedMetadata()
        BitBuffer(dest).use { putHighResAvatars(it, true, client, avatar, extended) }
        BitBuffer(dest).use { putHighResAvatars(it, false, client, avatar, extended) }
        BitBuffer(dest).use { putLowResAvatars(it, true, client, avatar, extended) }
        BitBuffer(dest).use { putLowResAvatars(it, false, client, avatar, extended) }
        putExtendedInfo(dest, client, avatar, extended)
        shiftActivityFlags(client.activityFlags)
    }

    public fun putHighResAvatars(
        dest: BitBuffer,
        activeFlag: Boolean,
        client: InfoClient,
        avatar: Avatar,
        extended: ExtendedMetadata
    ) {
        var skipCount = 0
        for (i in 0 until playerCount) {
            val other = avatars[i]
            val index = other.playerIndex.toInt()
            if (!client.highRes[index]) continue
            val flaggedInactive = (client.activityFlags[index].toInt() and 0x1) != 0
            if (activeFlag == flaggedInactive) continue
            if (skipCount > 0) {
                skipCount--
                client.activityFlags[index] = (client.activityFlags[index].toInt() or 0x2).toByte()
                continue
            }
            val extendedInfoFlags = other.extendedInfoFlags
            val hasExtendedInfo = (extendedInfoFlags.toInt() != 0 || isNewLogin(other.prevCoords)) &&
                extended.count < playerCapacity && !isFull(dest, extended.length)
            if (hasExtendedInfo) {
                val count = extended.count++
                extended.length += extendedInfoLength(avatar, i)
                client.extendedInfoRingBufIndexes[count] = i.toShort()
            }
            val updateCoords = other.currCoords != other.prevCoords
            val updateHighRes = updateCoords || hasExtendedInfo
            dest.putBoolean(updateHighRes)
            if (
                !inViewDistance(avatar.currCoords, other.currCoords, client.viewDistance.toInt()) ||
                isLoggedOut(other.currCoords, other.prevCoords)
            ) {
                val otherCurrCoordsLow = toLowResCoords(other.currCoords)
                val otherPrevCoordsLow = toLowResCoords(other.prevCoords)
                dest.putBoolean(false) /* extended info */
                putLowResCoords(dest, otherCurrCoordsLow, otherPrevCoordsLow)
                client.highRes[index] = false
                continue
            }
            if (!updateHighRes) {
                val skip = highResAvatarsToSkip(i + 1, activeFlag, client)
                putSkipCount(dest, skip)
                skipCount += skip
                client.activityFlags[index] =
                    (client.activityFlags[index].toInt() or 0x2).toByte()
                continue
            }
            dest.putBoolean(hasExtendedInfo)
            putHighResCoords(dest, other.currCoords, other.prevCoords)
        }
    }

    public fun highResAvatarsToSkip(startIndex: Int, activeFlags: Boolean, client: InfoClient): Int {
        var skipCount = 0
        for (i in startIndex until playerCount) {
            val other = avatars[i]
            val index = other.playerIndex.toInt()
            if (!client.highRes[index]) continue
            val flaggedInactive = (client.activityFlags[index].toInt() and 0x1) != 0
            if (activeFlags == flaggedInactive) continue
            val loggedOut = isLoggedOut(other.currCoords, other.prevCoords)
            val extendedInfo = other.extendedInfoFlags.toInt() != 0 || isNewLogin(other.prevCoords)
            val updateCoords = other.currCoords != other.prevCoords
            val updateHighRes = updateCoords || extendedInfo || loggedOut
            if (updateHighRes) break
            skipCount++
        }
        return skipCount
    }

    public fun putLowResAvatars(
        dest: BitBuffer,
        activeFlag: Boolean,
        client: InfoClient,
        avatar: Avatar,
        extended: ExtendedMetadata
    ) {
        var skipCount = 0
        for (i in 0 until playerCapacity) {
            val other = avatars[i]
            val index = other.playerIndex.toInt()
            if (client.highRes[index]) continue
            val flaggedInactive = (client.activityFlags[index].toInt() and 0x1) == 0
            if (activeFlag == flaggedInactive) continue
            if (skipCount > 0) {
                skipCount--
                client.activityFlags[index] = (client.activityFlags[index].toInt() or 0x2).toByte()
                continue
            }
            val inHighResDistance = inViewDistance(
                avatar.currCoords,
                other.currCoords,
                client.viewDistance.toInt()
            )
            val lowResCurrCoords = toLowResCoords(other.currCoords)
            val lowResPrevCoords = toLowResCoords(other.prevCoords)
            val updateCoords = lowResCurrCoords != lowResPrevCoords
            val updateLowRes = (updateCoords || inHighResDistance) &&
                extended.count < playerCapacity && !isFull(dest, extended.length)
            dest.putBoolean(updateLowRes)
            if (!updateLowRes) {
                val skip = lowResAvatarsToSkip(dest, i + 1, activeFlag, client, avatar, extended)
                putSkipCount(dest, skip)
                skipCount += skip
                client.activityFlags[index] =
                    (client.activityFlags[index].toInt() or 0x2).toByte()
                continue
            }
            if (updateCoords && !inHighResDistance) {
                putLowResCoords(dest, other.currCoords, other.prevCoords)
                continue
            }
            dest.putBits(len = 2, value = 0)
            dest.putBoolean(updateCoords)
            if (updateCoords) {
                putLowResCoords(dest, other.currCoords, other.prevCoords)
            }
            dest.putBits(len = 13, value = (other.currCoords shr 14) and 0x3FFF)
            dest.putBits(len = 13, value = other.currCoords and 0x3FFF)
            dest.putBit(1)
            val extendedCount = extended.count++
            extended.length += extendedInfoLength(avatar, i)
            client.extendedInfoRingBufIndexes[extendedCount] = i.toShort()
            client.highRes[index] = true
            client.activityFlags[index] = (client.activityFlags[index].toInt() or 0x2).toByte()
        }
    }

    public fun lowResAvatarsToSkip(
        dest: BitBuffer,
        startIndex: Int,
        activeFlags: Boolean,
        client: InfoClient,
        avatar: Avatar,
        extended: ExtendedMetadata
    ): Int {
        var skipCount = 0
        for (i in startIndex until MAX_PLAYER_CAPACITY) {
            val fakeAvatar = i >= playerCapacity
            val other = if (fakeAvatar) Avatar.ZERO else avatars[i]
            val index = other.playerIndex.toInt()
            if (client.highRes[index] && !fakeAvatar) continue
            val isInactive = (client.activityFlags[index].toInt() and 0x1) == 0
            if (activeFlags == isInactive) continue
            if (fakeAvatar) {
                skipCount++
                continue
            }
            val inHighResDistance = inViewDistance(
                avatar.currCoords,
                other.currCoords,
                client.viewDistance.toInt()
            )
            val lowResCurrCoords = toLowResCoords(other.currCoords)
            val lowResPrevCoords = toLowResCoords(other.prevCoords)
            val updateCoords = lowResCurrCoords != lowResPrevCoords
            val updateLowRes = (updateCoords || inHighResDistance) &&
                extended.count < playerCapacity && !isFull(dest, extended.length)
            if (updateLowRes) break
            skipCount++
        }
        return skipCount
    }

    // TODO: test for this (make sure correct app is being written, etc)
    public fun putExtendedInfo(
        dest: ByteBuffer,
        client: InfoClient,
        avatar: Avatar,
        extended: ExtendedMetadata
    ) {
        for (i in 0 until extended.count) {
            val ringBufIndex = client.extendedInfoRingBufIndexes[i].toInt()
            val other = avatars[ringBufIndex]

            // TODO: these flags should be defined in this api.
            // then accessed by impl through a sealed class.
            // info.setAvatarExtendedInfo(player.index, ExtendedInfo.Appearance, byteData)
            // require(byteData.length <= flag.allocatedLength)
            var flags = other.extendedInfoFlags.toInt()
            if (isNewLogin(other.prevCoords) || isNewLogin(avatar.prevCoords)) flags = flags or 0x40

            dest.put(flags.toByte())

            if ((flags and 0x40) != 0) {
                val data = extendedInfo[ringBufIndex].appearance
                val rawLength = data[0]
                for (dataIndex in 0 until rawLength) {
                    dest.put(data[1 + dataIndex])
                }
            }
        }
    }

    private fun extendedInfoLength(avatar: Avatar, ringBufIndex: Int): Int {
        val other = avatars[ringBufIndex]
        var flags = other.extendedInfoFlags.toInt()
        if (isNewLogin(other.prevCoords) || isNewLogin(avatar.prevCoords)) {
            flags = flags or 0x40
        }
        if (flags == 0) {
            return 0
        }
        var length = Byte.SIZE_BYTES
        if ((flags and 0x40) != 0) {
            val data = extendedInfo[ringBufIndex].appearance
            length += data[0]
        }
        return length
    }

    // TODO: replace with generic method to set any/most ExtendedInfo data blocks
    public fun setAppearance(playerIndex: Int, data: ByteArray, length: Int) {
        require(length < ExtendedInfo.APPEARANCE_MAX_BYTE_SIZE)
        val client = clients[playerIndex]
        val extended = extendedInfo[client.ringBufIndex]
        extended.appearance[0] = length.toByte()
        for (i in 0 until length) {
            extended.appearance[1 + i] = data[i]
        }
    }

    public fun updateExtendedInfo(
        playerIndex: Int,
        info: org.rsmod.plugins.info.player.extended3.ExtendedInfo
    ) {
        val struct = extended[info] ?: error("Structure not defined for $info. Use `extended { ... }` builder.")
        val client = clients[playerIndex]
        val avatar = avatars[client.ringBufIndex]
        avatar.extendedInfoFlags = (avatar.extendedInfoFlags.toInt() or struct.mask).toShort()
        if (struct.isStatic) {

        }
    }

    public fun extended(init: ExtendedInfoStructureBuilder.() -> Unit) {
        val builder = ExtendedInfoStructureBuilder(order = extended.size).apply(init)
        val struct = builder.build()
        extended[struct.type] = struct
    }

    override fun toString(): String {
        return "PlayerInfo(capacity=$playerCapacity, count=$playerCount)"
    }

    public companion object {

        public const val MAX_PLAYER_CAPACITY: Int = 2047
        public const val DEFAULT_VIEW_DISTANCE: Int = 15

        private const val BUFFER_BYTE_LIMIT = 40_000
        private const val BUFFER_BYTE_SAFETY_LIMIT = BUFFER_BYTE_LIMIT - 5000

        private fun putHighResCoords(dest: BitBuffer, currCoords: Int, prevCoords: Int) {
            val currX = (currCoords shr 14) and 0x3FFF
            val currY = (currCoords and 0x3FFF)
            val currLvl = (currCoords shr 28) and 0x3
            val prevX = (prevCoords shr 14) and 0x3FFF
            val prevY = (prevCoords and 0x3FFF)
            val prevLvl = (prevCoords shr 28) and 0x3
            val dx = currX - prevX
            val dy = currY - prevY
            val dl = (currLvl - prevLvl) and 0x3
            if (dx == 0 && dy == 0 && dl == 0) {
                dest.putBits(len = 2, value = 0)
            } else if (dx in -1..1 && dy in -1..1) {
                dest.putBits(len = 2, value = 1)
                dest.putBits(len = 3, value = getWalkDirOpcode(dx, dy))
            } else if (dx in -2..2 && dy in -2..2) {
                dest.putBits(len = 2, value = 2)
                dest.putBits(len = 4, value = getRunDirOpcode(dx, dy))
            } else if (prevCoords == 0) {
                /* if prev coords are 0 assume it's from log-in */
                dest.putBits(len = 2, value = 3)
                dest.putBits(len = 13, value = 0)
            } else if (dx in -15..15 && dy in -15..15) {
                dest.putBits(len = 2, value = 3)
                dest.putBoolean(false)
                dest.putBits(len = 2, value = dl and 0x3)
                dest.putBits(len = 5, value = dx and 0x1F)
                dest.putBits(len = 5, value = dy and 0x1F)
            } else {
                dest.putBits(len = 2, value = 3)
                dest.putBoolean(true)
                dest.putBits(len = 2, value = dl and 0x3)
                dest.putBits(len = 14, value = dx and 0x3FFF)
                dest.putBits(len = 14, value = dy and 0x3FFF)
            }
        }

        public fun putLowResCoords(dest: BitBuffer, currCoords: Int, prevCoords: Int) {
            val currX = (currCoords shr 8) and 0xFF
            val currY = currCoords and 0xFF
            val currLvl = (currCoords shr 16) and 0x3
            val prevX = (prevCoords shr 8) and 0xFF
            val prevY = prevCoords and 0xFF
            val prevLvl = (prevCoords shr 16) and 0x3
            val dx = currX - prevX
            val dy = currY - prevY
            val dl = (currLvl - prevLvl) and 0x3

            if (dx == 0 && dy == 0 && dl == 0) {
                dest.putBits(len = 2, value = 0)
            } else if (dx == 0 && dy == 0) {
                dest.putBits(len = 2, value = 1)
                dest.putBits(len = 2, value = dl)
            } else if (dx in -1..1 && dy in -1..1) {
                dest.putBits(len = 2, value = 2)
                dest.putBits(len = 2, value = dl)
                dest.putBits(len = 3, value = getWalkDirOpcode(dx, dy))
            } else {
                dest.putBits(len = 2, value = 3)
                dest.putBits(len = 2, value = dl)
                dest.putBits(len = 8, value = dx and 0xFF)
                dest.putBits(len = 8, value = dy and 0xFF)
            }
        }

        public fun putSkipCount(dest: BitBuffer, count: Int) {
            when {
                count == 0 -> dest.putBits(len = 2, value = 0)
                count <= 0x1F -> {
                    dest.putBits(len = 2, value = 1)
                    dest.putBits(len = 5, value = count)
                }
                count <= 0xFF -> {
                    dest.putBits(len = 2, value = 2)
                    dest.putBits(len = 8, value = count)
                }
                else -> {
                    dest.putBits(len = 2, value = 3)
                    dest.putBits(len = 11, value = count)
                }
            }
        }

        public fun inViewDistance(srcCoords: Int, otherCoords: Int, viewDistance: Int): Boolean {
            val srcX = (srcCoords shr 14) and 0x3FFF
            val srcY = (srcCoords and 0x3FFF)
            val srcLvl = (srcCoords shr 28) and 0x3
            val otherX = (otherCoords shr 14) and 0x3FFF
            val otherY = (otherCoords and 0x3FFF)
            val otherLvl = (otherCoords shr 28) and 0x3
            return otherLvl == srcLvl &&
                srcX - otherX in -viewDistance..viewDistance &&
                srcY - otherY in -viewDistance..viewDistance
        }

        public fun toLowResCoords(highRes: Int): Int {
            val x = (highRes shr 14) and 0x3FFF
            val y = (highRes and 0x3FFF)
            val level = (highRes shr 28) and 0x3
            return (y shr 13) or ((x shr 13) shl 8) or ((level and 0x3) shl 16)
        }

        public fun isNewLogin(prevCoords: Int): Boolean {
            return prevCoords == 0
        }

        public fun isLoggedOut(currCoords: Int, prevCoords: Int): Boolean {
            return currCoords == 0 && prevCoords == 0
        }

        public fun isFull(buf: BitBuffer, extendedLength: Int): Boolean {
            val bytePos = buf.position() / Byte.SIZE_BITS
            return bytePos + extendedLength >= BUFFER_BYTE_SAFETY_LIMIT
        }

        private fun shiftActivityFlags(flags: ByteArray) {
            for (i in 1 until flags.size) {
                flags[i] = (flags[i].toInt() shr 1).toByte()
            }
        }

        public fun getWalkDirOpcode(dx: Int, dy: Int): Int {
            if (dx == -1 && dy == -1) return 0
            if (dx == 0 && dy == -1) return 1
            if (dx == 1 && dy == -1) return 2
            if (dx == -1 && dy == 0) return 3
            if (dx == 1 && dy == 0) return 4
            if (dx == -1 && dy == 1) return 5
            if (dx == 0 && dy == 1) return 6
            return if (dx == 1 && dy == 1) 7 else 0
        }

        public fun getRunDirOpcode(dx: Int, dy: Int): Int {
            if (dx == -2 && dy == -2) return 0
            if (dx == -1 && dy == -2) return 1
            if (dx == 0 && dy == -2) return 2
            if (dx == 1 && dy == -2) return 3
            if (dx == 2 && dy == -2) return 4
            if (dx == -2 && dy == -1) return 5
            if (dx == 2 && dy == -1) return 6
            if (dx == -2 && dy == 0) return 7
            if (dx == 2 && dy == 0) return 8
            if (dx == -2 && dy == 1) return 9
            if (dx == 2 && dy == 1) return 10
            if (dx == -2 && dy == 2) return 11
            if (dx == -1 && dy == 2) return 12
            if (dx == 0 && dy == 2) return 13
            if (dx == 1 && dy == 2) return 14
            return if (dx == 2 && dy == 2) 15 else 0
        }

        @Suppress("NOTHING_TO_INLINE")
        private inline infix fun Int.or(struct: ExtendedInfoStructure): Int {
            return this or struct.mask
        }
    }
}
