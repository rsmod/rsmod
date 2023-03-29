package org.rsmod.plugins.info.player

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.plugins.info.model.coord.HighResCoord
import org.rsmod.plugins.info.player.PlayerInfo.Companion.CACHED_EXT_INFO_BUFFER_SIZE
import org.rsmod.plugins.info.player.PlayerInfo.Companion.DEFAULT_BUFFER_LIMIT
import org.rsmod.plugins.info.player.client.isInvalid
import org.rsmod.plugins.info.player.client.isValid
import java.nio.ByteBuffer

class PlayerInfoTest {

    @Test
    fun testRegistration() {
        val info = PlayerInfo()
        val index = DUMMY_INDEX
        assertTrue(info.avatars[index].isInvalid)
        info.register(index)
        assertTrue(info.avatars[index].isValid)
        info.unregister(index)
        assertTrue(info.avatars[index].isInvalid)
    }

    @Test
    fun testMaxPlayersLowResBufferLimitation() {
        val info = PlayerInfo()
        val buf = ByteBuffer.allocate(DEFAULT_BUFFER_LIMIT)
        val staticExtInfo = ByteArray(CACHED_EXT_INFO_BUFFER_SIZE)
        for (i in info.indices) {
            info.register(i)
            info.cacheStaticExtendedInfo(i, staticExtInfo)
        }
        for (i in info.indices) {
            val metadata = PlayerInfoMetadata()
            info.put(buf, i, metadata)
            assertEquals(394, metadata.extendedInfoCount)
        }
        for (i in info.indices) {
            info.unregister(i)
        }
    }

    @Test
    fun testMaxPlayersLowResNoBufferLimitation() {
        val info = PlayerInfo()
        val buf = ByteBuffer.allocate(200_000)
        val staticExtInfo = ByteArray(CACHED_EXT_INFO_BUFFER_SIZE)
        for (i in info.indices) {
            info.register(i)
            info.cacheStaticExtendedInfo(i, staticExtInfo)
        }
        for (i in info.indices) {
            val metadata = PlayerInfoMetadata()
            info.put(buf, i, metadata)
            assertEquals(info.playerLimit - 1, metadata.extendedInfoCount)
        }
        for (i in info.indices) {
            info.unregister(i)
        }
    }

    @Test
    fun testMultiPlayerLogOut() {
        val info = PlayerInfo()
        val buf = ByteBuffer.allocate(16)
        val playerA = 1
        val playerB = 2
        info.register(playerIndex = playerA)
        info.register(playerIndex = playerB)

        info.avatars[playerA].coords = HighResCoord(3200, 3200)
        info.avatars[playerA].prevCoords = HighResCoord(3200, 3200)

        info.avatars[playerB].coords = HighResCoord(3202, 3202)
        info.avatars[playerB].prevCoords = HighResCoord(3202, 3202)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertTrue(info.clients[playerA].isHighResolution[playerB])
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertTrue(info.clients[playerB].isHighResolution[playerA])
        }

        info.unregister(playerB)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2044, metadata.lowResolutionSkip)
            Assertions.assertFalse(info.clients[playerA].isHighResolution[playerB])
        }

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2044, metadata.lowResolutionSkip)
            Assertions.assertFalse(info.clients[playerA].isHighResolution[playerB])
        }
    }

    @Test
    fun testMultiPlayerLogIn() {
        val info = PlayerInfo()
        val buf = ByteBuffer.allocate(16)
        val playerA = 1
        val playerB = 2
        info.register(playerIndex = playerA)

        info.avatars[playerA].coords = HighResCoord(3200, 3200)
        info.avatars[playerA].prevCoords = HighResCoord(3200, 3200)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2045, metadata.lowResolutionSkip)
        }

        info.register(playerIndex = playerB)
        info.avatars[playerB].coords = HighResCoord(3202, 3202)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertTrue(info.clients[playerA].isHighResolution[playerB])
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertTrue(info.clients[playerB].isHighResolution[playerA])
        }

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertTrue(info.clients[playerA].isHighResolution[playerB])
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertTrue(info.clients[playerB].isHighResolution[playerA])
        }
    }

    private companion object {

        private const val DUMMY_INDEX = 5
    }
}
