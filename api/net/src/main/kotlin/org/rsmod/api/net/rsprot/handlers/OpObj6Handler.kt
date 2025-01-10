package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.objs.OpObj6
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid

class OpObj6Handler
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val objRegistry: ObjRegistry,
    private val marketPrices: MarketPrices,
) : MessageHandler<OpObj6> {
    override fun handle(player: Player, message: OpObj6) {
        val coords = CoordGrid(message.x, message.z, player.level)
        val type = objTypes[message.id] ?: return
        val stack = objRegistry.findAll(coords)
        val obj = stack.firstOrNull { it.type == type.id } ?: return
        val marketPrice = marketPrices[type] ?: 0
        player.objExamine(type, obj.count, marketPrice)
    }
}
