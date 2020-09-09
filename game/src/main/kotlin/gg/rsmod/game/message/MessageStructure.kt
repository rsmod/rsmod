package gg.rsmod.game.message

import io.netty.buffer.ByteBuf

private const val UNINITIALIZED_OPCODE = -1

private typealias PacketWriter<T> = T.(ByteBuf) -> Unit
private typealias PacketReader<T> = ByteBuf.() -> T

class ServerPacketStructure<T : ServerPacket>(
    val opcode: Int,
    val length: PacketLength,
    val write: PacketWriter<T>
)

class ClientPacketStructure<T : ClientPacket>(
    val opcode: Int,
    val length: Int,
    val read: PacketReader<T>
)

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class ServerPacketBuilder<T : ServerPacket> {

    private lateinit var packetWriter: PacketWriter<T>

    var opcode = UNINITIALIZED_OPCODE

    var length: PacketLength = PacketLength.Fixed

    fun write(write: PacketWriter<T>) {
        this.packetWriter = write
    }

    fun build(): ServerPacketStructure<T> {
        if(opcode == UNINITIALIZED_OPCODE) {
            error("Server packet structure opcode has not set.")
        } else if (!::packetWriter.isInitialized) {
            error("Server packet structure writer has not set.")
        }
        return ServerPacketStructure(
            opcode = opcode,
            length = length,
            write = packetWriter
        )
    }
}

@BuilderDslMarker
class ClientPacketBuilder<T : ClientPacket> {

    private lateinit var packetReader: PacketReader<T>

    var opcodes = mutableSetOf<Int>()

    var length: Int? = null

    fun read(reader: PacketReader<T>) {
        this.packetReader = reader
    }

    fun build(): List<ClientPacketStructure<T>> {
        if (opcodes.isEmpty()) {
            error("Client packet structure opcode has not set.")
        } else if (!::packetReader.isInitialized) {
            error("Client packet structure reader has not set.")
        }
        val length = length ?: error("Client packet structure length has not set.")
        return opcodes.map { opcode ->
            ClientPacketStructure(
                opcode = opcode,
                length = length,
                read = packetReader
            )
        }
    }
}
