package org.rsmod.plugins.api.protocol.packet.client

import org.rsmod.game.action.ActionBus
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.appearance.Equipment
import org.rsmod.plugins.api.model.mob.player.equipItem
import org.rsmod.plugins.api.protocol.packet.ItemAction
import javax.inject.Inject

data class OpHeld1(val item: Int, val component: Int, val slot: Int) : ClientPacket
data class OpHeld2(val item: Int, val component: Int, val slot: Int) : ClientPacket
data class OpHeld3(val item: Int, val component: Int, val slot: Int) : ClientPacket
data class OpHeld4(val item: Int, val component: Int, val slot: Int) : ClientPacket
data class OpHeld5(val item: Int, val component: Int, val slot: Int) : ClientPacket
data class OpHeld6(val item: Int) : ClientPacket

class OpHeld1Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val types: ItemTypeList
) : ClientPacketHandler<OpHeld1> {

    override fun handle(client: Client, player: Player, packet: OpHeld1) {
        val type = types.getOrNull(packet.item) ?: return
        val option = ItemAction.Inventory1(player, type)
        actionBus.publish(option, type.id)
    }
}

class OpHeld2Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val types: ItemTypeList
) : ClientPacketHandler<OpHeld2> {

    override fun handle(client: Client, player: Player, packet: OpHeld2) {
        val type = types.getOrNull(packet.item) ?: return
        val option = ItemAction.Inventory2(player, type)
        actionBus.publish(option, type.id)
        player.equipItem(type, Equipment.WEAPON)
    }
}

class OpHeld3Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val types: ItemTypeList
) : ClientPacketHandler<OpHeld3> {

    override fun handle(client: Client, player: Player, packet: OpHeld3) {
        val type = types.getOrNull(packet.item) ?: return
        val option = ItemAction.Inventory3(player, type)
        actionBus.publish(option, type.id)
    }
}

class OpHeld4Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val types: ItemTypeList
) : ClientPacketHandler<OpHeld4> {

    override fun handle(client: Client, player: Player, packet: OpHeld4) {
        val type = types.getOrNull(packet.item) ?: return
        val option = ItemAction.Inventory4(player, type)
        actionBus.publish(option, type.id)
    }
}

class OpHeld5Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val types: ItemTypeList
) : ClientPacketHandler<OpHeld5> {

    override fun handle(client: Client, player: Player, packet: OpHeld5) {
        val type = types.getOrNull(packet.item) ?: return
        val option = ItemAction.Inventory5(player, type)
        actionBus.publish(option, type.id)
    }
}

class OpHeld6Handler @Inject constructor(
    private val actionBus: ActionBus,
    private val types: ItemTypeList
) : ClientPacketHandler<OpHeld6> {

    override fun handle(client: Client, player: Player, packet: OpHeld6) {
        val type = types.getOrNull(packet.item) ?: return
        val option = ItemAction.ExamineAction(player, type)
        actionBus.publish(option)
    }
}
