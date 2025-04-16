package org.rsmod.api.config.editors

import org.rsmod.api.type.editors.varp.VarpEditor

object VarpEdits : VarpEditor() {
    init {
        edit("if1") { temporary = true }
        edit("if2") { temporary = true }
        edit("if3") { temporary = true }
        edit("canoeing_menu") { temporary = true }
    }
}
