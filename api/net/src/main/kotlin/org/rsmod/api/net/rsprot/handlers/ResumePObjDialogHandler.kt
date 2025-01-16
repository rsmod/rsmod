package org.rsmod.api.net.rsprot.handlers

import com.google.inject.Inject
import net.rsprot.protocol.game.incoming.resumed.ResumePObjDialog
import org.rsmod.api.player.input.ResumePObjDialogInput
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList

class ResumePObjDialogHandler @Inject constructor(private val objTypes: ObjTypeList) :
    MessageHandler<ResumePObjDialog> {
    override fun handle(player: Player, message: ResumePObjDialog) {
        val objType = objTypes.getOrDefault(message.obj, null) ?: return
        val input = ResumePObjDialogInput(objType)
        player.resumeActiveCoroutine(input)
    }
}
