package org.rsmod.plugins.info.player

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.plugins.info.BitBuffer
import org.rsmod.plugins.info.player.PlayerInfo.Companion.MAX_PLAYER_CAPACITY
import org.rsmod.plugins.info.player.PlayerInfo.Companion.getRunDirOpcode
import org.rsmod.plugins.info.player.PlayerInfo.Companion.getWalkDirOpcode
import org.rsmod.plugins.info.player.extended.ExtendedInfoSizes.TOTAL_BYTE_SIZE
import org.rsmod.plugins.info.player.extended.ExtendedMetadata
import java.nio.ByteBuffer
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PlayerInfoTest {

    private val buffers = Array(MAX_PLAYER_CAPACITY) { ByteBuffer.allocate(40_000) }

    private val appearanceData = ByteArray(152)

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun putFullyWithOnlySelfUpdateCoordsLogIn(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        Assertions.assertTrue(info.clients[index].highRes[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(0, 0))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(158, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val hasExtendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            val displacement = bitBuf.getBits(13)
            Assertions.assertTrue(updateHighRes)
            Assertions.assertTrue(hasExtendedInfo)
            Assertions.assertEquals(3, coordsDisplaceType)
            Assertions.assertEquals(0, displacement)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun putFullyWithOnlySelfUpdateCoordsNoMovement(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        Assertions.assertTrue(info.clients[index].highRes[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(3, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val skipCountOpcode = bitBuf.getBits(2)
            Assertions.assertFalse(updateHighRes)
            Assertions.assertEquals(info.playerCount - 1, skipCountOpcode)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun putFullyWithOnlySelfUpdateCoordsWalkDistance(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        Assertions.assertTrue(info.clients[index].highRes[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3201))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(3, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val hasExtendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            val dirOpcode = bitBuf.getBits(3)
            Assertions.assertTrue(updateHighRes)
            Assertions.assertFalse(hasExtendedInfo)
            Assertions.assertEquals(1, coordsDisplaceType)
            Assertions.assertEquals(getWalkDirOpcode(3200 - 3200, 3200 - 3201), dirOpcode)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun putFullyWithOnlySelfUpdateCoordsRunDistance(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        Assertions.assertTrue(info.clients[index].highRes[index])

        info.add(index, currCoords = coords(3202, 3200), prevCoords = coords(3200, 3200))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(3, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val hasExtendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            val dirOpcode = bitBuf.getBits(4)
            Assertions.assertTrue(updateHighRes)
            Assertions.assertFalse(hasExtendedInfo)
            Assertions.assertEquals(2, coordsDisplaceType)
            Assertions.assertEquals(getRunDirOpcode(3202 - 3200, 3200 - 3200), dirOpcode)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun putFullyWithOnlySelfUpdateCoordsSmallTeleport(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        Assertions.assertTrue(info.clients[index].highRes[index])

        info.add(index, currCoords = coords(3210, 3210), prevCoords = coords(3203, 3205))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(5, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val hasExtendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            val isLargeTeleport = bitBuf.getBoolean()
            val deltaLevel = bitBuf.getBits(2)
            val deltaX = bitBuf.getBits(5)
            val deltaY = bitBuf.getBits(5)
            Assertions.assertTrue(updateHighRes)
            Assertions.assertFalse(hasExtendedInfo)
            Assertions.assertEquals(3, coordsDisplaceType)
            Assertions.assertFalse(isLargeTeleport)
            Assertions.assertEquals(0, deltaLevel)
            Assertions.assertEquals(3210 - 3203, deltaX)
            Assertions.assertEquals(3210 - 3205, deltaY)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun putFullyWithOnlySelfUpdateCoordsLargeTeleport(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        Assertions.assertTrue(info.clients[index].highRes[index])

        info.add(index, currCoords = coords(4800, 2600, 1), prevCoords = coords(3200, 3200, 0))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(7, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val hasExtendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            val isLargeTeleport = bitBuf.getBoolean()
            val deltaLevel = bitBuf.getBits(2)
            val deltaX = bitBuf.getBits(14)
            val deltaY = bitBuf.getBits(14)
            Assertions.assertTrue(updateHighRes)
            Assertions.assertFalse(hasExtendedInfo)
            Assertions.assertEquals(3, coordsDisplaceType)
            Assertions.assertTrue(isLargeTeleport)
            Assertions.assertEquals(1, deltaLevel)
            Assertions.assertEquals(4800 - 3200, deltaX)
            Assertions.assertEquals((2600 - 3200) and 0x3FFF, deltaY)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testSkipCountWithOnlySelfCoordsLogIn(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(0, 0))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(158, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val hasExtendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            val displacement = bitBuf.getBits(13)
            Assertions.assertTrue(updateHighRes)
            Assertions.assertTrue(hasExtendedInfo)
            Assertions.assertEquals(3, coordsDisplaceType)
            Assertions.assertEquals(0, displacement)

            /* amount of bits read from high-res portion */
            val highResBitCount = 17
            val nextByteBitCount = 24
            val leftOverBits = nextByteBitCount - highResBitCount
            /* skip leftover bits */
            bitBuf.position(bitBuf.position() + leftOverBits)
            Assertions.assertEquals(nextByteBitCount, bitBuf.position())

            val updateLowRes = bitBuf.getBoolean()
            val skipCountOpcode = bitBuf.getBits(2)
            val skipCount = bitBuf.getBits(11)
            Assertions.assertFalse(updateLowRes)
            Assertions.assertEquals(3, skipCountOpcode)
            Assertions.assertEquals(MAX_PLAYER_CAPACITY - info.playerCount - 1, skipCount)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testSwitchLowResToHighRes(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        val otherIndex = index + 5
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        info.initialize(otherIndex, 0x40, appearanceData)
        Assertions.assertTrue(info.clients[index].highRes[index])
        Assertions.assertFalse(info.clients[index].highRes[otherIndex])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(3205, 3208), prevCoords = coords(3205, 3208))
        Assertions.assertEquals(2, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(160, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val skipCountOpcode = bitBuf.getBits(2)
            Assertions.assertFalse(updateHighRes)
            Assertions.assertEquals(0, skipCountOpcode)

            /* amount of bits read from high-res portion */
            val highResBitCount = 3
            val nextByteBitCount = 8
            val leftOverBits = nextByteBitCount - highResBitCount
            /* skip leftover bits */
            bitBuf.position(bitBuf.position() + leftOverBits)
            Assertions.assertEquals(nextByteBitCount, bitBuf.position())

            val updateLowRes = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            val updateCoords = bitBuf.getBoolean()
            val x = bitBuf.getBits(13)
            val y = bitBuf.getBits(13)
            val extendedInfo = bitBuf.getBoolean()
            Assertions.assertTrue(updateLowRes)
            Assertions.assertEquals(0, coordsDisplaceType)
            Assertions.assertFalse(updateCoords)
            Assertions.assertEquals(3205, x)
            Assertions.assertEquals(3208, y)
            Assertions.assertTrue(extendedInfo)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testSwitchHighResToLowRes(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        val otherIndex = index + 5
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        info.initialize(otherIndex, 0x40, appearanceData)
        Assertions.assertTrue(info.clients[index].highRes[index])
        Assertions.assertFalse(info.clients[index].highRes[otherIndex])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        Assertions.assertEquals(2, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(160, buf.position())
        Assertions.assertTrue(info.clients[index].highRes[otherIndex])

        /* adding from low-res to high-res requires at least one full iteration beforehand */
        info.clear()
        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(4800, 5200), prevCoords = coords(3205, 3208))

        buf.clear()
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(4, buf.position())
        BitBuffer(buf.flip()).use { bitBuf ->
            val updateSelf = bitBuf.getBoolean()
            val skipCountOpcode = bitBuf.getBits(2)
            Assertions.assertFalse(updateSelf)
            Assertions.assertEquals(0, skipCountOpcode)

            val updateOther = bitBuf.getBoolean()
            val extendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            Assertions.assertTrue(updateOther)
            Assertions.assertFalse(extendedInfo)
            Assertions.assertEquals(0, coordsDisplaceType)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testHighResNoActivityFlagShifts(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        val otherIndex = 10
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        info.initialize(otherIndex, 0x40, appearanceData)
        val client = info.clients[index]
        Assertions.assertTrue(client.highRes[index])
        Assertions.assertEquals(0, client.activityFlags[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        Assertions.assertEquals(2, info.playerCount)
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(160, buf.position())
        Assertions.assertEquals(0x1, client.activityFlags[index])

        /* adding from low-res to high-res requires at least one full iteration beforehand */
        info.clear()
        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        Assertions.assertEquals(2, info.playerCount)

        buf.clear()
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(3, buf.position())
        Assertions.assertEquals(0x1, client.activityFlags[index])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndex])
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testHighResCoordsChangeFlagShifts(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        val otherIndexes = intArrayOf(index + 10, index + 12, index + 13, index + 14, index + 15)
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        otherIndexes.forEach { info.initialize(it, 0x40, appearanceData) }

        val client = info.clients[index]
        Assertions.assertTrue(client.highRes[index])
        Assertions.assertEquals(0, client.activityFlags[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3202, 3200))
        otherIndexes.forEach { otherIndex ->
            info.add(otherIndex, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        }
        Assertions.assertEquals(otherIndexes.size, info.playerCount - 1)
        Assertions.assertEquals(0, info.highResAvatarsToSkip(1, false, client))

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(788, buf.position())
        /* previous coords change for self - did not mark as inactive */
        Assertions.assertEquals(0, client.activityFlags[index])

        /* adding from low-res to high-res requires at least one full iteration beforehand */
        info.clear()
        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndexes[0], currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndexes[1], currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndexes[2], currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndexes[3], currCoords = coords(3205, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndexes[4], currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        Assertions.assertEquals(otherIndexes.size, info.playerCount - 1)

        /* can skip the next 3 players as they have not changed */
        Assertions.assertEquals(3, info.highResAvatarsToSkip(1, false, client))

        buf.clear()
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(7, buf.position())
        /* all players except otherIndexes[3] have stayed inactive (0x1) */
        Assertions.assertEquals(0x1, client.activityFlags[index])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[0]])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[1]])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[2]])
        Assertions.assertEquals(0, client.activityFlags[otherIndexes[3]])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[4]])
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testLowResNoActivityFlagShifts(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        val otherIndex = 10
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        info.initialize(otherIndex, 0x40, appearanceData)
        val client = info.clients[index]
        Assertions.assertTrue(client.highRes[index])
        Assertions.assertEquals(0, client.activityFlags[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(9000, 9000), prevCoords = coords(9000, 9000))
        Assertions.assertEquals(2, info.playerCount)
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(3, buf.position())
        Assertions.assertEquals(0x1, client.activityFlags[index])

        /* adding from low-res to high-res requires at least one full iteration beforehand */
        info.clear()
        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(9000, 9000), prevCoords = coords(9000, 9000))
        Assertions.assertEquals(2, info.playerCount)

        buf.clear()
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(4, buf.position())
        Assertions.assertEquals(0x1, client.activityFlags[index])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndex])
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testLowResCoordsChangeFlagShifts(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        val otherIndexes = intArrayOf(index + 10, index + 12, index + 13, index + 14, index + 15)
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.initialize(index, 0x40, appearanceData)
        otherIndexes.forEach { info.initialize(it, 0x40, appearanceData) }

        val client = info.clients[index]
        Assertions.assertTrue(client.highRes[index])
        Assertions.assertEquals(0, client.activityFlags[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3202, 3200))
        otherIndexes.forEach { otherIndex ->
            info.add(otherIndex, currCoords = coords(4800, 4800), prevCoords = coords(4800, 4800))
        }
        Assertions.assertEquals(otherIndexes.size, info.playerCount - 1)
        Assertions.assertEquals(0, info.highResAvatarsToSkip(1, false, client))

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(3, buf.position())
        /* previous coords change for self - did not mark as inactive */
        Assertions.assertEquals(0, client.activityFlags[index])

        /* adding from low-res to high-res requires at least one full iteration beforehand */
        info.clear()
        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndexes[0], currCoords = coords(4800, 4800), prevCoords = coords(4800, 4800))
        info.add(otherIndexes[1], currCoords = coords(4800, 4800), prevCoords = coords(4800, 4800))
        info.add(otherIndexes[2], currCoords = coords(9000, 9000), prevCoords = coords(4800, 4800))
        info.add(otherIndexes[3], currCoords = coords(9000, 9000), prevCoords = coords(4800, 4800))
        info.add(otherIndexes[4], currCoords = coords(4800, 4800), prevCoords = coords(4800, 4800))
        Assertions.assertEquals(otherIndexes.size, info.playerCount - 1)

        /* can skip every low-res player as none have changed (aside from otherIndexes) */
        BitBuffer(buf).use { bitBuf ->
            val skipCount = info.lowResAvatarsToSkip(
                bitBuf,
                index,
                false,
                client,
                info.avatars[index],
                ExtendedMetadata()
            )
            Assertions.assertEquals(MAX_PLAYER_CAPACITY - otherIndexes.size - 1, skipCount)
        }

        buf.clear()
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(10, buf.position())
        /* all players except otherIndexes[3]/[4] have stayed inactive (0x1) */
        Assertions.assertEquals(0x1, client.activityFlags[index])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[0]])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[1]])
        Assertions.assertEquals(0, client.activityFlags[otherIndexes[2]])
        Assertions.assertEquals(0, client.activityFlags[otherIndexes[3]])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[4]])
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testPutExtendedInfo(index: Int, info: PlayerInfo) {
        val buf = buffers[index]

        // mock data
        val sequenceFlag = 0x8
        val sequenceId = 4200
        val sequenceDelay = 200
        val spotanimFlag = 0x2000
        val spotanimId = 2400
        val spotanimDelay = 10
        val spotanimHeight = 100
        val spotanimAttribs = (spotanimHeight shl 16) or spotanimDelay

        val flags = sequenceFlag or spotanimFlag

        // in this case flags >= 0xFF so write as short
        buf.put((flags and 0xFF).toByte())
        buf.put((flags shr 8).toByte())

        buf.putShort(sequenceId.toShort())
        buf.put(sequenceDelay.toByte())

        buf.putShort(spotanimId.toShort())
        buf.putInt(spotanimAttribs)

        val extended = ByteArray(buf.position())
        buf.flip()
        buf.get(extended, 0, extended.size)
        buf.clear()

        info.initialize(index, appearanceFlag = 0x40, appearanceData = appearanceData)
        info.add(index, coords(3200, 3200), coords(3200, 3200))
        info.setExtendedInfo(index, flags, extended)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        // 1 byte for high-res skip count
        // 2 bytes for low-res skip count
        Assertions.assertEquals(3 + extended.size, buf.position())

        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val hasExtendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            Assertions.assertTrue(updateHighRes)
            Assertions.assertTrue(hasExtendedInfo)
            Assertions.assertEquals(0, coordsDisplaceType)

            /* amount of bits read from high-res portion */
            val highResBitCount = 4
            val nextByteBitCount = 8
            val leftOverBits = nextByteBitCount - highResBitCount
            /* skip leftover bits */
            bitBuf.position(bitBuf.position() + leftOverBits)
            Assertions.assertEquals(nextByteBitCount, bitBuf.position())

            val updateLowRes = bitBuf.getBoolean()
            val lowResSkipCountOpcode = bitBuf.getBits(2)
            val skipCount = bitBuf.getBits(11)
            Assertions.assertFalse(updateLowRes)
            Assertions.assertEquals(3, lowResSkipCountOpcode)
            Assertions.assertEquals(MAX_PLAYER_CAPACITY - info.playerCount - 1, skipCount)
        }

        val flags1 = buf.get().toInt() and 0xFF
        val flags2 = buf.get().toInt() and 0xFF
        val extendedFlags = flags1 or (flags2 shl 8)
        Assertions.assertEquals(flags, extendedFlags)

        val bufSequenceId = buf.short.toInt() and 0xFFFF
        val bufSequenceDelay = buf.get().toInt() and 0xFF
        Assertions.assertEquals(sequenceId, bufSequenceId)
        Assertions.assertEquals(sequenceDelay, bufSequenceDelay)

        val bufSpotanimId = buf.short.toInt() and 0xFFFF
        val bufSpotanimAttribs = buf.int
        val bufSpotanimHeight = bufSpotanimAttribs shr 16
        val bufSpotanimDelay = bufSpotanimAttribs and 0xFFFF
        Assertions.assertEquals(spotanimId, bufSpotanimId)
        Assertions.assertEquals(spotanimHeight, bufSpotanimHeight)
        Assertions.assertEquals(spotanimDelay, bufSpotanimDelay)

        Assertions.assertEquals(buf.position(), buf.limit())
    }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun testPutExtendedInfoAppearanceOnly(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        // mock example appearance data
        val displayName = "test-name"
        val gender = 1.toByte()
        val skull = 2.toByte()
        val prayer = (-1).toByte()
        val appLength = 47 + displayName.length + 1
        buf.put(0.toByte())
        buf.put(gender)
        buf.put(skull)
        buf.put(prayer)
        repeat(12) { buf.putShort((0x100 + it).toShort()) }
        repeat(5) { buf.put(it.toByte()) }
        repeat(7) { buf.putShort((800 + it).toShort()) }
        buf.put(displayName.toByteArray()).put(0)
        buf.put(0, buf.position().toByte())

        Assertions.assertEquals(appLength, buf.position())

        val appearanceData = ByteArray(buf.position())
        buf.flip()
        buf.get(appearanceData, 0, appearanceData.size)
        buf.clear()

        val appearanceFlag = 0x40
        info.initialize(index, appearanceFlag = appearanceFlag, appearanceData = appearanceData)
        info.add(index, coords(3200, 3200), coords(0, 0))

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        // 3 bytes for local high-res player
        // 3 bytes for low-res skip count
        Assertions.assertEquals(6 + appLength, buf.position())

        BitBuffer(buf.flip()).use { bitBuf ->
            val updateHighRes = bitBuf.getBoolean()
            val hasExtendedInfo = bitBuf.getBoolean()
            val coordsDisplaceType = bitBuf.getBits(2)
            val isLargeTeleport = bitBuf.getBoolean()
            // coordinate deltas should all be 0 on log-in
            val deltaLevel = bitBuf.getBits(2)
            val deltaX = bitBuf.getBits(5)
            val deltaY = bitBuf.getBits(5)
            Assertions.assertTrue(updateHighRes)
            Assertions.assertTrue(hasExtendedInfo)
            Assertions.assertEquals(3, coordsDisplaceType)
            Assertions.assertFalse(isLargeTeleport)
            Assertions.assertEquals(0, deltaLevel)
            Assertions.assertEquals(0, deltaX)
            Assertions.assertEquals(0, deltaY)

            /* amount of bits read from high-res portion */
            val highResBitCount = 17
            val nextByteBitCount = 24
            val leftOverBits = nextByteBitCount - highResBitCount
            /* skip leftover bits */
            bitBuf.position(bitBuf.position() + leftOverBits)
            Assertions.assertEquals(nextByteBitCount, bitBuf.position())

            val updateLowRes = bitBuf.getBoolean()
            val skipCountOpcode = bitBuf.getBits(2)
            val skipCount = bitBuf.getBits(11)
            Assertions.assertFalse(updateLowRes)
            Assertions.assertEquals(3, skipCountOpcode)
            Assertions.assertEquals(MAX_PLAYER_CAPACITY - info.playerCount - 1, skipCount)
        }

        val extendedFlags = buf.get().toInt()
        val extendedLength = buf.limit() - buf.position()
        Assertions.assertEquals(appearanceFlag, extendedFlags)
        Assertions.assertEquals(appearanceData.size, extendedLength)
        for (i in 0 until extendedLength) {
            Assertions.assertEquals(appearanceData[i], buf.get())
        }
        Assertions.assertEquals(buf.position(), buf.limit())
    }

    @Test
    fun testFullCapacityBufferLimit() {
        val info = PlayerInfo(2047)
        val index = 1
        val buf = buffers[index]
        val client = info.clients[index]
        val indexes = IntArray(info.playerCapacity) { it }
        require(info.playerCount == 0)
        require(buf.position() == 0)

        indexes.forEach { info.initialize(it, 0x40, appearanceData) }
        indexes.forEach { info.add(it, coords(4800, 4800), coords(4800, 4800)) }

        Assertions.assertEquals(indexes.size, info.playerCount)
        BitBuffer(buf).use { bitBuf ->
            val skipCount = info.lowResAvatarsToSkip(
                dest = bitBuf,
                startIndex = index,
                activeFlags = false,
                client = client,
                avatar = info.avatars[index],
                extended = ExtendedMetadata()
            )
            Assertions.assertEquals(0, skipCount)
        }

        val extended = ExtendedMetadata()
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index, extended)
        Assertions.assertEquals(224, extended.count)
        Assertions.assertEquals(35143, buf.position())
    }

    @Test
    fun testSinglePlayerFullExtendedInfo() {
        val info = PlayerInfo(2047)
        val index = 5
        val extended = ByteArray(TOTAL_BYTE_SIZE - 1)
        val buf = buffers[index].clear()
        val metadata = ExtendedMetadata()
        require(info.playerCount == 0)
        require(buf.position() == 0)
        info.initialize(playerIndex = index, appearanceFlag = 0x40, appearanceData = appearanceData)
        info.add(playerIndex = index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.setExtendedInfo(playerIndex = index, maskFlags = 0x2000, data = extended)
        info.putFully(buf, playerIndex = index, metadata)
        Assertions.assertEquals(1, metadata.count)
        Assertions.assertEquals(extended.size, metadata.length)
        // 3 bytes for local high-res player portion
        Assertions.assertEquals(extended.size + 3, buf.position())
    }

    private object PlayerInfoCapacityVarProvider : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(DUMMY_INDEX, PlayerInfo(250)),
                Arguments.of(DUMMY_INDEX, PlayerInfo(500)),
                Arguments.of(DUMMY_INDEX, PlayerInfo(1000)),
                Arguments.of(DUMMY_INDEX, PlayerInfo(2047))
            )
        }
    }

    private companion object {

        private const val DUMMY_INDEX = 5

        private fun coords(x: Int, y: Int, level: Int = 0): Int {
            return (y and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)
        }
    }
}
