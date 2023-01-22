package org.rsmod.plugins.net.game.client

sealed class OperatingSystem {

    object Windows : OperatingSystem()
    object Mac : OperatingSystem()
    object Linux : OperatingSystem()
    object Other : OperatingSystem()
}
