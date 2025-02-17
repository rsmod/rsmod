package org.rsmod.content.generic.locs.ladders

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.editors.loc.LocEditor

internal object LadderLocsEdits : LocEditor() {
    init {
        edit("ladder_down") {
            param[params.climb_anim] = seqs.human_pickupfloor
            contentGroup = content.ladder_down
        }

        edit("ladder_up") { contentGroup = content.ladder_up }

        edit("ladder_option") { contentGroup = content.ladder_option }

        edit("lumbridge_kitchen_dungeonladder") {
            param[params.climb_anim] = seqs.human_pickupfloor
            contentGroup = content.dungeonladder_down
        }

        edit("lumbridge_cellar_dungeonladder") { contentGroup = content.dungeonladder_up }
    }
}
