package org.runerealm.api.config.editors

import org.runerealm.api.config.refs.invs
import org.runerealm.api.type.editors.inv.InvEditor
import org.runerealm.game.type.inv.InvScope
import org.runerealm.game.type.inv.InvStackType

internal object InvEdits : InvEditor() {
    init {
        edit(invs.inv) {
            scope = InvScope.Perm
            protect = false
            runWeight = true
        }

        edit(invs.worn) {
            scope = InvScope.Perm
            protect = false
            runWeight = true
        }

        edit(invs.bank) {
            scope = InvScope.Perm
            stack = InvStackType.Always
            protect = false
            placeholders = true
        }
    }
}
