package org.rsmod.plugins.api.info

import io.netty.buffer.Unpooled
import org.openrs2.buffer.setByteC
import org.openrs2.buffer.writeByteA
import org.openrs2.buffer.writeShortLEA
import org.openrs2.buffer.writeString
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.task.PlayerInfoTask
import org.rsmod.plugins.api.net.downstream.PlayerInfoPacket
import org.rsmod.plugins.info.PlayerInfo
import org.rsmod.plugins.info.model.coord.HighResCoord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class SingleThreadedPlayerInfoTask @Inject constructor(
    private val players: PlayerList,
    private val info: PlayerInfo
) : PlayerInfoTask {

    private var gameClock = 0

    private val buffers = ReusableByteBufferMap(
        playerCapacity = players.capacity,
        singleBufferCapacity = 40_000
    )

    public fun initialize(player: Player) {
        info.register(player.index)
        info.updateExtendedInfo(player.index, player.extendedInfo(0x40))
        info.cacheDynamicExtendedInfo(player.index, gameClock, player.extendedInfo(0x40))
        info.cacheStaticExtendedInfo(player.index, player.extendedInfo(0x40 or 0x8 or 0x1))
    }

    override fun execute() {
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
        // TODO: if (player.loggedOut) info.unregister(player.index)
    }

    private fun Player.extendedInfo(flags: Int): ByteArray {
        val buf = Unpooled.buffer()

        if (flags < 0xFF) {
            buf.writeByte(flags)
        } else {
            val extendedFlags = flags or 0x4
            buf.writeByte(extendedFlags and 0xFF)
            buf.writeByte(extendedFlags shr 8)
        }

        if ((flags and 0x40) != 0) {
            val appStartPos = buf.writerIndex()
            buf.writeZero(1)
            buf.writeByte(0)
            buf.writeByte(-1)
            buf.writeByte(-1)
            for (i in 0 until 12) {
                if (TRANSLATION_TABLE_BACK[i] != -1) {
                    buf.writeShort(0x100 + DEFAULT_LOOKS[TRANSLATION_TABLE_BACK[i]])
                } else {
                    buf.writeByte(0)
                }
            }
            intArrayOf(0, 3, 2, 0, 0).forEach { color ->
                buf.writeByte(color)
            }
            intArrayOf(808, 823, 819, 820, 821, 822, 824).forEach { bas ->
                buf.writeShort(bas)
            }
            buf.writeString(displayName)
            buf.writeByte(0)
            buf.writeShort(0)
            buf.writeByte(0)
            buf.writeShort(0)
            for (i in 0 until 3) {
                buf.writeString("")
            }
            buf.writeByte(0)

            val appLength = buf.writerIndex() - appStartPos - 1
            buf.setByteC(appStartPos, appLength)
        }

        if ((flags and 0x8) != 0) {
            buf.writeShortLEA(-1)
            buf.writeByteA(0)
        }

        if ((flags and 0x1) != 0) {
            buf.writeShort(0)
        }

        return ByteArray(buf.readableBytes()).apply { buf.readBytes(this) }
    }

    private fun Coordinates.toHighResCoords(): HighResCoord {
        return HighResCoord(x, y, level)
    }

    private companion object {

        private val TRANSLATION_TABLE_BACK = intArrayOf(-1, -1, -1, -1, 2, -1, 3, 5, 0, 4, 6, 1)
        private val DEFAULT_LOOKS = intArrayOf(9, 14, 109, 26, 33, 36, 42)
    }
}
