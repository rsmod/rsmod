package org.rsmod.content.other.generic.ladders

import org.rsmod.api.config.refs.BaseParams
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.editors.loc.LocEditor

internal object LadderLocsEdits : LocEditor() {
    init {
        edit("ladder_down") {
            param[BaseParams.climb_anim] = seqs.human_pickupfloor
            contentType = content.ladder_down
        }

        edit("ladder_up") { contentType = content.ladder_up }

        edit("ladder_option") { contentType = content.ladder_option }
    }
}
