package org.rsmod.content.generic.locs.staircase

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias staircase_locs = StaircaseLocs

internal object StaircaseLocs : LocReferences() {
    val lumbridge_spiral_bottom = find("spiralstairsbottom_3")
    val lumbridge_spiral_top = find("spiralstairstop_3")
}

internal object StaircaseLocEdits : LocEditor() {
    init {
        edit("spiralstaircase_down") { contentGroup = content.spiralstaircase_down }
        edit("spiralstaircase_up") { contentGroup = content.spiralstaircase_up }
        edit("spiralstaircase_option") { contentGroup = content.spiralstaircase_option }

        edit("spiralstairsbottom_3") { contentGroup = content.spiralstaircase_up }
        edit("spiralstairstop_3") { contentGroup = content.spiralstaircase_down }
    }
}
