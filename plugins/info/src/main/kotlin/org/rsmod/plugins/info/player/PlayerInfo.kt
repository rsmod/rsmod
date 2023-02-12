package org.rsmod.plugins.info.player

import org.rsmod.plugins.info.BitBuffer
import org.rsmod.plugins.info.player.extended.ExtendedBuffer
import org.rsmod.plugins.info.player.extended.ExtendedInfoSizes.APPEARANCE_MAX_BYTE_SIZE
import org.rsmod.plugins.info.player.extended.ExtendedMetadata
import org.rsmod.plugins.info.player.model.Avatar
import org.rsmod.plugins.info.player.model.InfoClient
import java.nio.ByteBuffer
import java.util.Arrays

public class PlayerInfo(
    public val playerCapacity: Int,
    public val bufferByteLimitPerClient: Int = DEFAULT_BUFFER_BYTE_LIMIT
) {

    init { require(playerCapacity <= MAX_PLAYER_CAPACITY) }

    /* current amount of online players to iterate */
    public var playerCount: Int = 0

    private var appearanceFlag = 0

    /* mapped to player index */
    public val clients: Array<InfoClient> = Array(playerCapacity) { InfoClient(playerCapacity) }

    /* mapped to player index */
    public val appearance: Array<ExtendedBuffer> = Array(playerCapacity) {
        /* + 1 byte from mask flag byte header */
        ExtendedBuffer(ByteArray(APPEARANCE_CACHE_BYTES + 1))
    }

    /* ring buffer */
    public val avatars: Array<Avatar> = Array(playerCapacity) { Avatar() }

    /* ring buffer */
    public val extBuffers: Array<ExtendedBuffer> = Array(playerCapacity) { ExtendedBuffer() }

    public fun initialize(playerIndex: Int, appearanceFlag: Int, appearanceData: ByteArray) {
        val client = clients[playerIndex]
        client.viewDistance = DEFAULT_VIEW_DISTANCE.toByte()
        Arrays.fill(client.highRes, false)
        Arrays.fill(client.activityFlags, 0)
        client.highRes[playerIndex] = true
        this.appearanceFlag = appearanceFlag
        this.appearance[playerIndex].reset().putBytes(appearanceData)
    }

    public fun finalize(playerIndex: Int) {
        appearance[playerIndex].reset()
    }

    public fun add(playerIndex: Int, currCoords: Int, prevCoords: Int) {
        val ringBufIndex = playerCount++
        clients[playerIndex].ringBufIndex = ringBufIndex

        val avatar = avatars[ringBufIndex]
        avatar.extendedInfoLength = 0
        avatar.extendedInfoFlags = 0
        avatar.playerIndex = playerIndex.toShort()
        avatar.currCoords = currCoords
        avatar.prevCoords = prevCoords

        extBuffers[ringBufIndex].offset = 0
    }

    public fun clear() {
        playerCount = 0
    }

    public fun read(
        dest: ByteBuffer,
        playerIndex: Int,
        extended: ExtendedMetadata = ExtendedMetadata()
    ) {
        dest.clear()
        putFully(dest, playerIndex, extended)
        dest.flip()
    }

    public fun putFully(
        dest: ByteBuffer,
        playerIndex: Int,
        extended: ExtendedMetadata = ExtendedMetadata()
    ) {
        val client = clients[playerIndex]
        val avatar = avatars[client.ringBufIndex]
        BitBuffer(dest).use { putHighResAvatars(it, true, client, avatar, extended) }
        BitBuffer(dest).use { putHighResAvatars(it, false, client, avatar, extended) }
        BitBuffer(dest).use { putLowResAvatars(it, true, client, avatar, extended) }
        BitBuffer(dest).use { putLowResAvatars(it, false, client, avatar, extended) }
        putExtendedInfo(dest, client, extended)
        shiftActivityFlags(client.activityFlags)
    }

    public fun putHighResAvatars(
        dest: BitBuffer,
        activeFlag: Boolean,
        client: InfoClient,
        avatar: Avatar,
        extended: ExtendedMetadata
    ) {
        /*
         * We want to put all high-res appearance masks on first log in. This does mean
         * that the only high-res player that will be force-sent said extended info would
         * be our "local" client/player. This is because the only high-res avatar will
         * be our local avatar on first their first gpi tick.
         *
         * With current `isNewLogin` logic - this may not always be the case (player is
         * located in coords 0,0,0). We may change this condition to use a flag within
         * [InfoClient] instead.
         */
        val loggedIn = isNewLogIn(avatar.prevCoords)
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
            val hasExtendedInfo = loggedIn || (other.extendedInfoFlags.toInt() != 0 &&
                extended.count < playerCapacity && !isFull(dest, extended.length))
            if (hasExtendedInfo) {
                val count = extended.count++
                val length = if (loggedIn) {
                    (APPEARANCE_CACHE_BYTES + 1).toShort()
                } else {
                    other.extendedInfoLength
                }
                extended.length += length
                client.setExtendedInfoRingBufIndex(count, i, appearanceOnly = loggedIn)
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
            val loggedIn = isNewLogIn(other.prevCoords)
            val extendedInfo = loggedIn || other.extendedInfoFlags.toInt() != 0
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

            // TODO: client caches appearance - should keep track of that
            // to avoid re-sending appearance data unnecessarily.
            val putAppearance = true
            dest.putBoolean(putAppearance)
            if (putAppearance) {
                val extendedCount = extended.count++
                extended.length += APPEARANCE_CACHE_BYTES + 1
                client.setExtendedInfoRingBufIndex(extendedCount, i, appearanceOnly = true)
            }
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

    public fun putExtendedInfo(
        dest: ByteBuffer,
        client: InfoClient,
        extended: ExtendedMetadata
    ) {
        check(appearanceFlag != 0) { "Appearance flag must be set via `cacheAppearance` function." }
        for (i in 0 until extended.count) {
            val appearanceOnly = client.isExtendedInfoRingBufIndexAppearanceOnly(i)
            val ringBufIndex = client.getExtendedInfoRingBufIndex(i)
            if (appearanceOnly) {
                val other = avatars[ringBufIndex]
                val buffer = appearance[other.playerIndex.toInt()]
                check(buffer.offset > 0) { "Cached appearance not found for player." }
                /*
                 * Short appearance flag is not supported as of now. Can't recall
                 * if appearance flag is ever that high a value. If it can be - we
                 * will need to ask the implementation layer for the packed flags
                 * "extended" bitmask (the flag they use to tell the client to
                 * read the flags as a short instead of a byte).
                 */
                check(appearanceFlag < 0xFF) { "Short appearance flag not supported." }
                dest.put((appearanceFlag and 0xFF).toByte())
                dest.put(buffer.data, 0, buffer.offset)
                continue
            }
            val buffer = extBuffers[ringBufIndex]
            check(dest.position() + buffer.offset < dest.limit()) {
                "Dest limit reached: ${(dest.position())} + ${buffer.offset} >= ${dest.limit()}"
            }
            dest.put(buffer.data, 0, buffer.offset)
        }
    }

    public fun setExtendedInfo(playerIndex: Int, maskFlags: Int, data: ByteArray) {
        val ringBufIndex = clients[playerIndex].ringBufIndex
        val buffer = extBuffers[ringBufIndex]
        if (data.size >= buffer.data.size) {
            throw ArrayIndexOutOfBoundsException(
                "Extended info data too long. " +
                    "(capacity=${buffer.data.size}, received=${data.size})"
            )
        }
        System.arraycopy(data, 0, buffer.data, 0, data.size)
        buffer.offset = data.size

        val avatar = avatars[ringBufIndex]
        avatar.extendedInfoFlags = maskFlags.toShort()
        avatar.extendedInfoLength = data.size.toShort()
    }

    public fun cacheAppearance(playerIndex: Int, data: ByteArray) {
        appearance[playerIndex].reset().putBytes(data)
    }

    public fun isFull(buf: BitBuffer, extendedLength: Int): Boolean {
        val safetyLimit = bufferByteLimitPerClient - SAFETY_BUFFER_BYTE_TRIM
        val bytePos = buf.position() / Byte.SIZE_BITS
        return bytePos + extendedLength >= safetyLimit
    }

    override fun toString(): String {
        return "PlayerInfo(capacity=$playerCapacity, count=$playerCount)"
    }

    public companion object {

        public const val MAX_PLAYER_CAPACITY: Int = 2047
        public const val DEFAULT_VIEW_DISTANCE: Int = 15

        private const val DEFAULT_BUFFER_BYTE_LIMIT: Int = 40_000
        private const val SAFETY_BUFFER_BYTE_TRIM: Int = 5000

        /* give some wiggle room just in case */
        /* could always make these constants configurable */
        public const val APPEARANCE_CACHE_BYTES: Int = APPEARANCE_MAX_BYTE_SIZE + 50

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

        public fun isNewLogIn(prevCoords: Int): Boolean {
            return prevCoords == 0
        }

        public fun isLoggedOut(currCoords: Int, prevCoords: Int): Boolean {
            return currCoords == 0 && prevCoords == 0
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
    }
}
