package gg.rsmod.game.model.client

import gg.rsmod.game.action.ActionHandler
import gg.rsmod.game.model.mob.Player

class Client(
    val player: Player,
    val machine: ClientMachine,
    var settings: ClientSettings,
    val encryptedPass: String,
    val loginXteas: IntArray,
    val pendingHandlers: MutableList<ActionHandler<*>> = mutableListOf()
)

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
