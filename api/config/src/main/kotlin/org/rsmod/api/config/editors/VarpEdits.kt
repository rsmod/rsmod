package org.rsmod.api.config.editors

import org.rsmod.api.type.editors.varp.VarpEditor

object VarpEdits : VarpEditor() {
    init {
        edit("generic_varp_261") { temporary = true }
        edit("generic_varp_262") { temporary = true }
        edit("generic_varp_263") { temporary = true }
    }
}
