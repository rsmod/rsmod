package gg.rsmod.plugins.core.update.player

import com.google.inject.Inject
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.game.model.mob.update.UpdateTask
import gg.rsmod.plugins.core.protocol.packet.server.PlayerUpdate
import io.guthix.buffer.toBitMode
import io.netty.buffer.Unpooled

class PlayerUpdateTask @Inject constructor(
    private val playerList: PlayerList
) : UpdateTask {

    override fun execute() {
        playerList.forEach { player ->
            if (player == null) {
                return@forEach
            }
            val buf = Unpooled.buffer()

            val bitBuf = buf.toBitMode()
            bitBuf.writeBits(0, 1)
            bitBuf.writeBits(3, 2)
            bitBuf.writeBits(0, 11)

            repeat(2047) {
                bitBuf.writeBits(0, 1)
                bitBuf.writeBits(0, 2)
            }

            val gpi = PlayerUpdate(buf)
            player.write(gpi)
        }
    }
}
