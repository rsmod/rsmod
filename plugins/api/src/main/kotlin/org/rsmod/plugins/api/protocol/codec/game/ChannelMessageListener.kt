package org.rsmod.plugins.api.protocol.codec.game

import io.netty.channel.Channel
import org.rsmod.game.message.ServerPacket
import org.rsmod.game.message.ServerPacketListener

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
