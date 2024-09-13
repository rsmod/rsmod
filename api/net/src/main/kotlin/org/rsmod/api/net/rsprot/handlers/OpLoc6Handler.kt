package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.locs.OpLoc6
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.mes
import org.rsmod.game.entity.Player
import org.rsmod.game.type.loc.LocTypeList

class OpLoc6Handler @Inject constructor(private val locTypes: LocTypeList) :
    MessageHandler<OpLoc6> {
    override fun handle(player: Player, message: OpLoc6) {
        val type = locTypes[message.id] ?: return
        player.mes(type.desc, ChatType.LocExamine)
    }
}
