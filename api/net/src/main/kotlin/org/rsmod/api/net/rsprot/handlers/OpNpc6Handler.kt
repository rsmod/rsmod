package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.npcs.OpNpc6
import org.rsmod.api.player.mes
import org.rsmod.api.player.util.ChatType
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.NpcTypeList

class OpNpc6Handler @Inject constructor(private val npcTypes: NpcTypeList) :
    MessageHandler<OpNpc6> {
    override fun handle(player: Player, message: OpNpc6) {
        val type = npcTypes[message.id] ?: return
        player.mes(type.desc, ChatType.NpcExamine)
    }
}
