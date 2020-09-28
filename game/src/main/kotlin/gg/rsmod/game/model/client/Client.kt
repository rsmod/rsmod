package gg.rsmod.game.model.client

import com.github.michaelbull.logging.InlineLogger
import com.google.common.base.MoreObjects
import com.google.inject.Inject
import gg.rsmod.game.action.Action
import gg.rsmod.game.action.ActionMessage
import gg.rsmod.game.model.mob.Player
import java.util.LinkedList

private val logger = InlineLogger()

class Client(
    val player: Player,
    val machine: ClientMachine,
    var settings: ClientSettings,
    val encryptedPass: String,
    val loginXteas: IntArray,
    val pendingActions: LinkedList<ActionMessage<out Action>> = LinkedList()
) {

    fun pollActions(actionLimit: Int) {
        for (i in 0 until actionLimit) {
            val message = pendingActions.poll() ?: break
            val handler = message.handler
            val action = message.action
            handler.handle(this, player, action)
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
    private val active: MutableList<Client> = mutableListOf()
) : List<Client> by active {

    @Inject
    constructor() : this(mutableListOf())

    fun register(client: Client) {
        if (active.any { it.player.id == client.player.id }) {
            logger.error { "Client is already registered (player=${client.player})" }
            return
        }
        logger.debug { "Registered to client list (player=${client.player})" }
        active.add(client)
    }

    fun remove(client: Client) {
        if (active.none { it.player.id == client.player.id }) {
            logger.error { "Client is not registered (player=${client.player})" }
            return
        }
        logger.debug { "Remove from client list (player=${client.player})" }
        active.remove(client)
    }
}

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

data class JavaVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
)
