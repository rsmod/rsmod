package org.rsmod.plugins.api.protocol

import org.rsmod.game.model.client.ClientDevice

sealed class Device : ClientDevice {
    object Ios : Device()
    object Android : Device()
    object Desktop : Device()
    override fun toString(): String = javaClass.simpleName
}
