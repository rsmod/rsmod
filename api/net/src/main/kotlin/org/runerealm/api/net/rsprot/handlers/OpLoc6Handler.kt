package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.locs.OpLoc6
import org.runerealm.api.player.output.ChatType
import org.runerealm.api.player.output.mes
import org.runerealm.game.entity.Player
import org.runerealm.game.type.loc.LocTypeList

class OpLoc6Handler @Inject constructor(private val locTypes: LocTypeList) :
    MessageHandler<OpLoc6> {
    override fun handle(player: Player, message: OpLoc6) {
        val type = locTypes[message.id] ?: return
        player.mes(type.desc, ChatType.LocExamine)
    }
}
