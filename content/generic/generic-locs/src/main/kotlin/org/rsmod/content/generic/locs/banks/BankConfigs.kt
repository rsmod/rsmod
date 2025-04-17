package org.rsmod.content.generic.locs.banks

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.type.loc.LocType

internal typealias bank_locs = BankLocs

internal object BankLocs : LocReferences() {
    val bankbooth = find("aide_bankbooth")
    val bankbooth_multi = find("aide_bankbooth_multi")
}

internal object BankLocEditor : LocEditor() {
    init {
        booth(bank_locs.bankbooth)
        booth(bank_locs.bankbooth_multi)
    }

    private fun booth(type: LocType) {
        edit(type) { contentGroup = content.bank_booth }
    }
}
