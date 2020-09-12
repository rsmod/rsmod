package gg.rsmod.game.message

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import kotlin.reflect.KClass

private val logger = InlineLogger()

private typealias ServerPacketStructures = MutableMap<KClass<out ServerPacket>, ServerPacketStructure<*>>
private typealias ClientPacketStructures = MutableMap<Int, ClientPacketStructure<*>>

class ServerPacketStructureMap(
    val structures: ServerPacketStructures
) : Map<KClass<out ServerPacket>, ServerPacketStructure<*>> by structures {

    @Inject
    constructor() : this(mutableMapOf())

    inline fun <reified T : ServerPacket> register(
        init: ServerPacketBuilder<T>.() -> Unit
    ) {
        val builder = ServerPacketBuilder<T>().apply(init)
        val structure = builder.build()

        if (structures.containsKey(T::class)) {
            error("Server packet type already has a structure (packet=${T::class.simpleName}).")
        }

        logger.debug {
            "Register server packet structure (packet=${T::class.simpleName}, " +
                "opcode=${structure.opcode}, length=${structure.length::class.simpleName})"
        }
        structures[T::class] = structure
    }

    @Suppress("UNCHECKED_CAST")
    inline operator fun <reified T : ServerPacket> get(packet: T) =
        structures[packet::class] as? ServerPacketStructure<T>

    companion object {
        val logger = InlineLogger()
    }
}

class ClientPacketStructureMap(
    private val structures: ClientPacketStructures
) : Map<Int, ClientPacketStructure<*>> by structures {

    @Inject
    constructor() : this(mutableMapOf())

    fun <T : ClientPacket> register(
        init: ClientPacketBuilder<T>.() -> Unit
    ) {
        val builder = ClientPacketBuilder<T>().apply(init)
        val structureList = builder.build()

        logger.debug {
            "Register client packet structure " +
                "(opcodes=${structureList.map { it.opcode }}, length=${structureList[0].length})"
        }
        structureList.forEach { structure ->
            if (structures.containsKey(structure.opcode)) {
                error("Client packet opcode already has a structure (opcode=${structure.opcode}).")
            }

            structures[structure.opcode] = structure
        }
    }
}
