package gg.rsmod.plugins.core.protocol.structure

import com.google.inject.Inject
import com.google.inject.Injector
import gg.rsmod.game.message.ClientPacketStructureMap
import gg.rsmod.game.message.ServerPacketStructureMap
import gg.rsmod.game.update.mask.UpdateMaskPacketMap
import gg.rsmod.plugins.core.protocol.Device

class PacketStructureCodec(
    val server: ServerPacketStructureMap,
    val client: ClientPacketStructureMap,
    val update: UpdateMaskPacketMap
)

class DevicePacketStructureMap(
    private val desktop: PacketStructureCodec,
    private val ios: PacketStructureCodec,
    private val android: PacketStructureCodec
) {

    @Inject
    constructor(injector: Injector) : this(
        desktop = codec(injector),
        ios = codec(injector),
        android = codec(injector)
    )

    fun client(device: Device) = getCodec(device).client

    fun server(device: Device) = getCodec(device).server

    fun update(device: Device) = getCodec(device).update

    fun getCodec(device: Device): PacketStructureCodec = when (device) {
        Device.Desktop -> desktop
        Device.Ios -> ios
        Device.Android -> android
    }

    companion object {

        private fun codec(injector: Injector) = PacketStructureCodec(
            ServerPacketStructureMap(),
            ClientPacketStructureMap(injector),
            UpdateMaskPacketMap()
        )
    }
}
