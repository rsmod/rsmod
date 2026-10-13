package org.runerealm.content.generic.locs.coops

import org.runerealm.api.config.refs.content
import org.runerealm.api.type.editors.loc.LocEditor
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.game.type.loc.LocType

typealias coop_locs = ChickenCoopLocs

object ChickenCoopLocs : LocReferences() {
    val empty = find("chicken_coop_empty")
    val bare = find("chicken_coop_bare")
    val bare2 = find("chicken_coop_bare2")
}

object ChickenCoopLocEditor : LocEditor() {
    init {
        coop(coop_locs.empty)
        coop(coop_locs.bare)
        coop(coop_locs.bare2)
    }

    private fun coop(type: LocType) {
        edit(type) { contentGroup = content.chicken_coop }
    }
}
