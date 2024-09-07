package org.rsmod.content.other.generic.ladders

import org.rsmod.api.config.refs.BaseParams
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.game.type.util.ParamMapBuilder

internal object LadderLocsEdits : LocEditor() {
    init {
        edit("ladder_down") {
            val param = ParamMapBuilder()
            param[BaseParams.climb_anim] = seqs.human_pickupfloor
            this.paramMap = param.toParamMap()
            contentType = content.ladder_down.id
        }

        edit("ladder_up") { contentType = content.ladder_up.id }

        edit("ladder_option") { contentType = content.ladder_option.id }
    }
}
