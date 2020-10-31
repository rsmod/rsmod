package gg.rsmod.plugins.api.protocol.codec.game

import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.message.ServerPacketListener
import io.netty.channel.Channel

class ChannelMessageListener(
    private val channel: Channel
) : ServerPacketListener {

    override fun write(packet: ServerPacket) {
        channel.write(packet)
    }

    override fun flush() {
        channel.flush()
    }
}
