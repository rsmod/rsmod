package org.rsmod.content.generic.locs.ladders

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.editors.loc.LocEditor

internal object LadderLocsEdits : LocEditor() {
    init {
        edit("laddertop") {
            param[params.climb_anim] = seqs.human_pickupfloor
            contentGroup = content.ladder_down
        }

        edit("ladder") { contentGroup = content.ladder_up }

        edit("laddermiddle") { contentGroup = content.ladder_option }

        edit("qip_cook_trapdoor_open") {
            param[params.climb_anim] = seqs.human_pickupfloor
            contentGroup = content.dungeonladder_down
        }

        edit("ladder_from_cellar") { contentGroup = content.dungeonladder_up }
    }
}
