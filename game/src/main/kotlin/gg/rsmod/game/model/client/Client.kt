package gg.rsmod.game.model.client

import com.github.michaelbull.logging.InlineLogger
import com.google.common.base.MoreObjects
import com.google.inject.Inject
import gg.rsmod.game.message.ClientPacket
import gg.rsmod.game.message.ClientPacketMessage
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.update.record.UpdateRecord
import io.netty.buffer.ByteBufAllocator
import java.util.LinkedList

private val logger = InlineLogger()

class Client(
    val player: Player,
    val device: ClientDevice,
    val machine: ClientMachine,
    var settings: ClientSettings,
    val encryptedPass: String,
    val loginXteas: IntArray,
    val bufAllocator: ByteBufAllocator,
    val updateRecords: MutableList<UpdateRecord> = mutableListOf(),
    val pendingPackets: LinkedList<ClientPacketMessage<out ClientPacket>> = LinkedList()
) {

    fun pollActions(actionLimit: Int) {
        for (i in 0 until actionLimit) {
            val message = pendingPackets.poll() ?: break
            val handler = message.handler
            val packet = message.packet
            handler.handle(this, player, packet)
        }
    }

    override fun toString(): String = MoreObjects
        .toStringHelper(this)
        .add("player", player)
        .add("machine", machine)
        .add("settings", settings)
        .toString()
}

class ClientList(
    private val active: MutableList<Client>
) : List<Client> by active {

    @Inject
    constructor() : this(mutableListOf())

    fun register(client: Client) {
        if (active.any { it.player.id == client.player.id }) {
            logger.error { "Client is already registered (player=${client.player})" }
            return
        }
        active.add(client)
    }

    fun remove(client: Client) {
        if (active.none { it.player.id == client.player.id }) {
            logger.error { "Client is not registered (player=${client.player})" }
            return
        }
        active.remove(client)
    }
}

sealed class OperatingSystem {
    object Windows : OperatingSystem()
    object Mac : OperatingSystem()
    object Linux : OperatingSystem()
    object Other : OperatingSystem()
    override fun toString(): String = javaClass.simpleName
}

sealed class JavaVendor {
    object Sun : JavaVendor()
    object Microsoft : JavaVendor()
    object Apple : JavaVendor()
    object Other : JavaVendor()
    object Oracle : JavaVendor()
    override fun toString(): String = javaClass.simpleName
}

interface ClientDevice

data class ClientSettings(
    val width: Int,
    val height: Int,
    val flags: Int
)

data class ClientMachine(
    val operatingSystem: OperatingSystem,
    val is64Bit: Boolean,
    val osVersion: Int,
    val javaVendor: JavaVendor,
    val javaVersion: JavaVersion,
    val maxMemory: Int,
    val cpuCount: Int
)

data class JavaVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
)
