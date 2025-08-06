package org.rsmod.content.skills.herblore.config

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.type.loc.LocType

typealias waterSources = WaterSourceReferences

internal object  WaterSourceLocs : LocEditor() {
    init {
        waterSources.locs.forEach { it ->
            configure(it)
        }
    }

    private fun configure(obj: LocType) {
        edit(obj) {
            contentGroup = content.fountains
        }
    }
}

object WaterSourceReferences : LocReferences() {
    val locs =
        listOf(
            find("fountain"),
            find("goldfountain"),
    )
}
