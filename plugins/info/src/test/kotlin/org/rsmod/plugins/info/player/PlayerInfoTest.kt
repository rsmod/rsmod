package org.rsmod.plugins.info.player

import org.junit.jupiter.api.Assertions
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
import java.nio.ByteBuffer
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PlayerInfoTest {

    private val buffers = Array(MAX_PLAYER_CAPACITY) { ByteBuffer.allocate(40_000) }

    @ParameterizedTest
    @ArgumentsSource(PlayerInfoCapacityVarProvider::class)
    fun putFullyWithOnlySelfUpdateCoordsLogIn(index: Int, info: PlayerInfo) {
        val buf = buffers[index]
        require(info.playerCount == 0)
        require(buf.position() == 0)

        info.registerClient(index)
        Assertions.assertTrue(info.clients[index].highRes[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(0, 0))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(6, buf.position())
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

        info.registerClient(index)
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

        info.registerClient(index)
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

        info.registerClient(index)
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

        info.registerClient(index)
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

        info.registerClient(index)
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

        info.registerClient(index)
        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(0, 0))
        Assertions.assertEquals(1, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(6, buf.position())
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
            Assertions.assertEquals(nextByteBitCount, bitBuf.position().toInt())

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

        info.registerClient(index)
        info.registerClient(otherIndex)
        Assertions.assertTrue(info.clients[index].highRes[index])
        Assertions.assertFalse(info.clients[index].highRes[otherIndex])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(3205, 3208), prevCoords = coords(3205, 3208))
        Assertions.assertEquals(2, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(8, buf.position())
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
            Assertions.assertEquals(nextByteBitCount, bitBuf.position().toInt())

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

        info.registerClient(index)
        info.registerClient(otherIndex)
        Assertions.assertTrue(info.clients[index].highRes[index])
        Assertions.assertFalse(info.clients[index].highRes[otherIndex])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        Assertions.assertEquals(2, info.playerCount)

        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(8, buf.position())
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

        info.registerClient(index)
        info.registerClient(otherIndex)
        val client = info.clients[index]
        Assertions.assertTrue(client.highRes[index])
        Assertions.assertEquals(0, client.activityFlags[index])

        info.add(index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.add(otherIndex, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        Assertions.assertEquals(2, info.playerCount)
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(8, buf.position())
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

        info.registerClient(index)
        otherIndexes.forEach { info.registerClient(it) }

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
        Assertions.assertEquals(28, buf.position())
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

        info.registerClient(index)
        info.registerClient(otherIndex)
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

        info.registerClient(index)
        otherIndexes.forEach { info.registerClient(it) }

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

        /* can skip every low-res player as none have changed */
        BitBuffer(buf).use { bitBuf ->
            val skipCount = info.lowResAvatarsToSkip(
                bitBuf, index, false, client,
                info.avatars[index], ExtendedMetadata()
            )
            Assertions.assertEquals(MAX_PLAYER_CAPACITY - otherIndexes.size - 1, skipCount)
        }

        buf.clear()
        Assertions.assertEquals(0, buf.position())
        info.putFully(buf, index)
        Assertions.assertEquals(10, buf.position())
        /* all players except otherIndexes[3] have stayed inactive (0x1) */
        Assertions.assertEquals(0x1, client.activityFlags[index])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[0]])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[1]])
        Assertions.assertEquals(0, client.activityFlags[otherIndexes[2]])
        Assertions.assertEquals(0, client.activityFlags[otherIndexes[3]])
        Assertions.assertEquals(0x1, client.activityFlags[otherIndexes[4]])
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
