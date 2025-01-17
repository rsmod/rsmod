package org.rsmod.api.config.editors

import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor

internal object ObjEdits : ObjEditor() {
    init {
        edit("coins") { param[params.shop_sale_restricted] = true }
        edit("platinum_token") { param[params.shop_sale_restricted] = true }
        edit("chinchompa") {
            param[params.release_note_title] = "Drop all of your chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("red_chinchompa") {
            param[params.release_note_title] = "Drop all of your red chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("black_chinchompa") {
            param[params.release_note_title] = "Drop all of your black chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("snowball") { param[params.player_op5_text] = "Pelt" }
    }
}
