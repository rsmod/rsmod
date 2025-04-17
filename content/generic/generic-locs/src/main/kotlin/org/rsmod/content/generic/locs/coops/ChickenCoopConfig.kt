package org.rsmod.content.generic.locs.coops

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor

object ChickenCoopLocEditor : LocEditor() {
    init {
        coop("chicken_coop_empty")
        coop("chicken_coop_bare")
        coop("chicken_coop_bare2")
    }

    private fun coop(internal: String) {
        edit(internal) { contentGroup = content.chicken_coop }
    }
}
