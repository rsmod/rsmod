package org.rsmod.api.config.editors

import org.rsmod.api.type.editors.varp.VarpEditor

object VarpEdits : VarpEditor() {
    init {
        edit("generic_temp_state_261") { temporary = true }
        edit("generic_temp_state_262") { temporary = true }
        edit("generic_temp_state_263") { temporary = true }
        edit("temp_state_675") { temporary = true }
    }
}
