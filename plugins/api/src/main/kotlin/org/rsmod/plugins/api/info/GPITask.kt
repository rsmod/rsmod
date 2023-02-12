package org.rsmod.plugins.api.info

import io.netty.buffer.Unpooled
import org.openrs2.buffer.setByteC
import org.openrs2.buffer.writeString
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.task.PlayerInfoTask
import org.rsmod.plugins.api.net.downstream.PlayerInfoPacket
import org.rsmod.plugins.info.player.PlayerInfo
import javax.inject.Inject

public class GPITask @Inject constructor(
    private val players: PlayerList,
    private val info: PlayerInfo
) : PlayerInfoTask {

    private val buffers = ReusableByteBufferMap(
        playerCapacity = players.capacity,
        singleBufferCapacity = 40_000
    )

    public fun initialize(player: Player) {
        info.initialize(player.index, 0x40, player.appearanceData())
    }

    override fun execute() {
        info.prepare()
        players.forEach { it?.prepareGpi() }
        players.forEach { it?.executeGpi() }
        players.forEach { it?.finalizeGpi() }
    }

    private fun Player.prepareGpi() {
        info.add(index, coords.packed, prevCoords.packed)
        //TODO: info.setExtendedInfo(index, updateMaskFlags, updateMaskData)
    }

    private fun Player.executeGpi() {
        val buf = buffers[index].clear()
        info.read(buf, index)
        downstream += PlayerInfoPacket(buf.array(), buf.limit())
    }

    private fun Player.finalizeGpi() {
        // should _not_ be handled in gpi task, but for testing purposes.
        prevCoords = coords
        // TODO: if (player.loggedOut) info.finalize(player.index)
    }

    private fun Player.appearanceData(): ByteArray {
        val buf = Unpooled.buffer()
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
        buf.setByteC(0, buf.readableBytes() - 1)
        return ByteArray(buf.readableBytes())
            .apply { buf.readBytes(this) }
    }

    private companion object {

        private val TRANSLATION_TABLE_BACK = intArrayOf(-1, -1, -1, -1, 2, -1, 3, 5, 0, 4, 6, 1)
        private val DEFAULT_LOOKS = intArrayOf(9, 14, 109, 26, 33, 36, 42)
    }
}
