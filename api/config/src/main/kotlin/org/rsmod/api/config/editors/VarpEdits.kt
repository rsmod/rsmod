package org.rsmod.api.config.editors

import org.rsmod.api.config.refs.varps
import org.rsmod.api.type.editors.varp.VarpEditor

object VarpEdits : VarpEditor() {
    init {
        edit(varps.if1) { temporary = true }
        edit(varps.if2) { temporary = true }
        edit(varps.if3) { temporary = true }
        edit(varps.canoeing_menu) { temporary = true }
    }
}
