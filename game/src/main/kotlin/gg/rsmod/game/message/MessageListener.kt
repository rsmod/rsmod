package gg.rsmod.game.message

interface MessageListener {

    fun write(packet: ServerPacket)

    fun flush()
}
