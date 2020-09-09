package gg.rsmod.game.message

interface ServerPacket

interface ClientPacket

sealed class PacketLength {
    object Fixed : PacketLength()
    object Byte : PacketLength()
    object Short : PacketLength()
}
