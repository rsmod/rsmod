package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.npcs.OpNpc6
import org.runerealm.api.player.output.ChatType
import org.runerealm.api.player.output.mes
import org.runerealm.game.entity.Player
import org.runerealm.game.type.npc.NpcTypeList

class OpNpc6Handler @Inject constructor(private val npcTypes: NpcTypeList) :
    MessageHandler<OpNpc6> {
    override fun handle(player: Player, message: OpNpc6) {
        val type = npcTypes[message.id] ?: return
        player.mes(type.desc, ChatType.NpcExamine)
    }
}
