package org.runerealm.api.config.editors

import org.runerealm.api.config.refs.varps
import org.runerealm.api.type.editors.varp.VarpEditor

object VarpEdits : VarpEditor() {
    init {
        edit(varps.prayer0) { temporary = true }
        edit(varps.if1) { temporary = true }
        edit(varps.if2) { temporary = true }
        edit(varps.if3) { temporary = true }
        edit(varps.date_vars) { transmitOnDiff = true }
        edit(varps.canoeing_menu) { temporary = true }
    }
}
