package gg.rsmod.game.model.client

import gg.rsmod.game.action.ActionHandler
import gg.rsmod.game.model.mob.Player

class Client(
    val player: Player,
    val machine: ClientMachine,
    var settings: ClientSettings,
    val pendingHandlers: MutableList<ActionHandler<*>> = mutableListOf()
)

data class ClientSettings(
    val width: Int,
    val height: Int,
    val flags: Int
) {
    val inResizableMode: Boolean
        get() = (flags shr 1) == 1

    val inLowMemoryMode: Boolean
        get() = (flags shr 2) == 1
}

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
}

sealed class JavaVendor {
    object Sun : JavaVendor()
    object Microsoft : JavaVendor()
    object Apple : JavaVendor()
    object Other : JavaVendor()
    object Oracle : JavaVendor()
}

data class JavaVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
)
