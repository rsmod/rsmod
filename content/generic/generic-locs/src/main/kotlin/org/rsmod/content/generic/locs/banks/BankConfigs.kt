package org.rsmod.content.generic.locs.banks

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor

internal object BankLocEditor : LocEditor() {
    init {
        booth("aide_bankbooth")
        booth("aide_bankbooth_multi")
    }

    private fun booth(internal: String) {
        edit(internal) { contentGroup = content.bank_booth }
    }
}
