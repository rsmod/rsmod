package org.rsmod.plugins.api.info.player

import io.netty.buffer.Unpooled
import org.openrs2.buffer.writeBytesA
import org.openrs2.buffer.writeString
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.api.net.downstream.PlayerInfoPacket
import org.rsmod.plugins.info.player.PlayerInfo
import org.rsmod.plugins.info.player.model.ExtendedInfoSizes
import org.rsmod.plugins.info.player.model.coord.HighResCoord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class PlayerInfoTask @Inject constructor(
    private val players: PlayerList,
    private val info: PlayerInfo
) {

    private var gameClock = 0

    private val buffers = ReusableByteBufferMap(
        playerCapacity = players.capacity,
        singleBufferCapacity = 40_000
    )

    public fun initialize(player: Player) {
        info.register(player.index)
        info.updateExtendedInfo(player.index, player.extendedInfo(64))
        info.cacheDynamicExtendedInfo(player.index, gameClock, player.extendedInfo(64))
        info.cacheStaticExtendedInfo(player.index, player.extendedInfo(64))
    }

    public fun finalize(player: Player) {
        info.unregister(player.index)
    }

    public fun execute() {
        gameClock++
        players.forEach { it?.prepareGpi() }
        players.forEach { it?.executeGpi() }
        players.forEach { it?.finalizeGpi() }
    }

    private fun Player.prepareGpi() {
        info.updateCoords(index, coords.toHighResCoords(), prevCoords.toHighResCoords())
        // TODO: info.setExtendedInfo(index, updateMaskFlags, updateMaskData)
    }

    private fun Player.executeGpi() {
        val buf = buffers[index]
        info.put(buf, index)
        downstream += PlayerInfoPacket(buf.array(), buf.limit())
    }

    private fun Player.finalizeGpi() {
        // should _not_ be handled in gpi task, but for testing purposes.
        prevCoords = coords
        info.updateExtendedInfo(index, byteArrayOf())
    }

    private fun Player.extendedInfo(flags: Int): ByteArray {
        val buf = Unpooled.buffer()

        if (flags < 0xFF) {
            buf.writeByte(flags)
        } else {
            val extendedFlags = flags or 2
            buf.writeByte(extendedFlags and 0xFF)
            buf.writeByte(extendedFlags shr 8)
        }

        if ((flags and 64) != 0) {
            val appBuf = buf.alloc().buffer(ExtendedInfoSizes.APPEARANCE_MAX_BYTE_SIZE)
            appBuf.let { buffer ->
                buffer.writeByte(0)
                buffer.writeByte(-1)
                buffer.writeByte(-1)
                for (i in 0 until 12) {
                    if (TRANSLATION_TABLE_BACK[i] != -1) {
                        buffer.writeShort(0x100 + DEFAULT_LOOKS[TRANSLATION_TABLE_BACK[i]])
                    } else {
                        buffer.writeByte(0)
                    }
                }
                intArrayOf(0, 3, 2, 0, 0).forEach { color ->
                    buffer.writeByte(color)
                }
                intArrayOf(808, 823, 819, 820, 821, 822, 824).forEach { bas ->
                    buffer.writeShort(bas)
                }
                buffer.writeString(displayName)
                buffer.writeByte(0)
                buffer.writeShort(0)
                buffer.writeByte(0)
                buffer.writeShort(0)
                for (i in 0 until 3) {
                    buffer.writeString("")
                }
                buffer.writeByte(0)
            }
            buf.writeByte(appBuf.readableBytes())
            buf.writeBytesA(appBuf)
        }

        return ByteArray(buf.readableBytes()).apply { buf.readBytes(this) }
    }

    private fun Coordinates.toHighResCoords(): HighResCoord {
        return HighResCoord(x, z, level)
    }

    private companion object {

        private val TRANSLATION_TABLE_BACK = intArrayOf(-1, -1, -1, -1, 2, -1, 3, 5, 0, 4, 6, 1)
        private val DEFAULT_LOOKS = intArrayOf(9, 14, 109, 26, 33, 36, 42)
    }
}
