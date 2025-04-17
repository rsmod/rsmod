package org.rsmod.content.generic.locs.staircase

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias staircase_locs = StaircaseLocs

internal object StaircaseLocs : LocReferences() {
    val spiral_top = find("spiralstairstop")
    val spiral_bottom = find("spiralstairs")
    val spiral_middle = find("spiralstairsmiddle")
    val lumbridge_spiral_bottom = find("spiralstairsbottom_3")
    val lumbridge_spiral_top = find("spiralstairstop_3")
}

internal object StaircaseLocEdits : LocEditor() {
    init {
        edit(staircase_locs.spiral_top) { contentGroup = content.spiralstaircase_down }
        edit(staircase_locs.spiral_bottom) { contentGroup = content.spiralstaircase_up }
        edit(staircase_locs.spiral_middle) { contentGroup = content.spiralstaircase_option }

        edit(staircase_locs.lumbridge_spiral_bottom) { contentGroup = content.spiralstaircase_up }
        edit(staircase_locs.lumbridge_spiral_top) { contentGroup = content.spiralstaircase_down }
    }
}
