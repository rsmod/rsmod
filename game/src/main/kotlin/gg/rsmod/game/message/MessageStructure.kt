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
    val read: PacketReader<T>?
) {

    val suppress: Boolean
        get() = read == null
}

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
        if (opcode == UNINITIALIZED_OPCODE) {
            error("Server packet structure opcode has not been set.")
        } else if (!::packetWriter.isInitialized) {
            error("Server packet structure writer has not been set.")
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

    private var packetReader: PacketReader<T>? = null

    private var opcodes = mutableSetOf<Int>()

    var length: Int? = null

    var opcode: Int = 0
        set(value) { opcodes.add(value) }

    fun opcodes(init: OpcodeBuilder.() -> Unit) {
        OpcodeBuilder(opcodes).apply(init)
    }

    fun read(reader: PacketReader<T>) {
        this.packetReader = reader
    }

    fun build(): List<ClientPacketStructure<T>> {
        if (opcodes.isEmpty()) {
            error("Client packet structure opcode has not been set.")
        }
        val length = length ?: error("Client packet structure length has not been set.")
        return opcodes.map { opcode ->
            ClientPacketStructure(
                opcode = opcode,
                length = length,
                read = packetReader
            )
        }
    }
}

@BuilderDslMarker
class OpcodeBuilder(private val opcodes: MutableSet<Int>) {
    var opcode: Int = 0
        set(value) { opcodes.add(value) }
}
