package gg.rsmod.game.message

interface ServerPacket

interface ClientPacket

data class ClientPacketMessage<T : ClientPacket>(
    val packet: T,
    val handler: ClientPacketHandler<ClientPacket>
)

sealed class PacketLength {
    object Fixed : PacketLength()
    object Byte : PacketLength()
    object Short : PacketLength()
}
