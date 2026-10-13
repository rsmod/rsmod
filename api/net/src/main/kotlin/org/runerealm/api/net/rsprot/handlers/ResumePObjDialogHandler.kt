package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.resumed.ResumePObjDialog
import org.runerealm.api.player.input.ResumePObjDialogInput
import org.runerealm.game.entity.Player
import org.runerealm.game.type.obj.ObjTypeList

class ResumePObjDialogHandler @Inject constructor(private val objTypes: ObjTypeList) :
    MessageHandler<ResumePObjDialog> {
    override fun handle(player: Player, message: ResumePObjDialog) {
        val objType = objTypes.getOrDefault(message.obj, null) ?: return
        val input = ResumePObjDialogInput(objType)
        player.resumeActiveCoroutine(input)
    }
}
