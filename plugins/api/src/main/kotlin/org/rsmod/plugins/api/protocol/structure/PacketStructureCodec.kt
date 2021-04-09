package org.rsmod.plugins.api.protocol.structure

import javax.inject.Inject
import com.google.inject.Injector
import org.rsmod.game.message.ClientPacketStructureMap
import org.rsmod.game.message.ServerPacketStructureMap
import org.rsmod.game.update.mask.UpdateMaskPacketMap
import org.rsmod.plugins.api.protocol.Device

class PacketStructureCodec(
    val server: ServerPacketStructureMap,
    val client: ClientPacketStructureMap,
    val playerUpdate: UpdateMaskPacketMap,
    val npcUpdate: UpdateMaskPacketMap
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

    fun playerUpdate(device: Device) = getCodec(device).playerUpdate

    fun npcUpdate(device: Device) = getCodec(device).npcUpdate

    fun getCodec(device: Device): PacketStructureCodec = when (device) {
        Device.Desktop -> desktop
        Device.Ios -> ios
        Device.Android -> android
    }

    companion object {

        private fun codec(injector: Injector) = PacketStructureCodec(
            ServerPacketStructureMap(),
            ClientPacketStructureMap(injector),
            UpdateMaskPacketMap(),
            UpdateMaskPacketMap()
        )
    }
}
