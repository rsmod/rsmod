package org.rsmod.content.other.generic.locs.banks

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor

internal object BankLocEditor : LocEditor() {
    init {
        booths("lumbridge_bank_booth_18491", "lumbridge_bank_booth_27291")
    }

    private fun booth(internal: String) {
        edit(internal) { contentGroup = content.bank_booth }
    }

    @Suppress("SameParameterValue")
    private fun booths(vararg internal: String) = internal.forEach(::booth)
}
