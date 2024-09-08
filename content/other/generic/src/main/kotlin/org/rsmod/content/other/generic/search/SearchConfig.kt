package org.rsmod.content.other.generic.search

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.loc.LocEditor

internal object SearchLocEdits : LocEditor() {
    init {
        crates().forEach(::crate)
    }

    private fun crate(internal: String) {
        edit(internal) {
            param[params.game_message] = SearchConstants.EMPTY_CRATE
            contentType = content.empty_crate
        }
    }

    private fun crates(): Set<String> =
        setOf("stack_crate_2", "big_crate_2", "stack_crate", "big_crate")
}

internal object SearchConstants {
    // You search the crates but find nothing of value. <- one type of box in varrock castle
    const val EMPTY_CRATE = "You search the crate but find nothing."
    const val EMPTY_CHEST = "You search the chest but find nothing."
    const val EMPTY_CRATE2 = "The crate is empty."
    const val EMPTY_BOXES = "There is nothing interesting in these boxes."
    const val EMPTY_SACKS = "There is nothing interesting in these sacks."

    const val DEFAULT = "You find nothing of interest."
}
