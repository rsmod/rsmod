package gg.rsmod.plugins.core.protocol

sealed class Device {
    object Ios : Device()
    object Android : Device()
    object Desktop : Device()
    override fun toString(): String = javaClass.simpleName
}
