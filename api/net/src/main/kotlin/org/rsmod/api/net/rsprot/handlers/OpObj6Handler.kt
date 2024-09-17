package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.objs.OpObj6
import org.rsmod.api.player.output.objExamine
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList

class OpObj6Handler @Inject constructor(private val objTypes: ObjTypeList) :
    MessageHandler<OpObj6> {
    override fun handle(player: Player, message: OpObj6) {
        val type = objTypes[message.id] ?: return
        player.objExamine(type, count = 1)
    }
}
