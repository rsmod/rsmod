package gg.rsmod.game.message

interface ServerPacket

interface ClientPacket

interface MessageListener {

    fun write(packet: ServerPacket)

    fun flush()
}

sealed class PacketLength {
    object Fixed : PacketLength()
    object Byte : PacketLength()
    object Short : PacketLength()
}
