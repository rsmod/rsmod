package org.runerealm.content.generic.locs.staircase

import org.runerealm.api.config.refs.content
import org.runerealm.api.type.editors.loc.LocEditor
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.game.type.loc.LocType

typealias staircase_locs = StaircaseLocs

object StaircaseLocs : LocReferences() {
    val spiral_top = find("spiralstairstop")
    val spiral_bottom = find("spiralstairs")
    val spiral_middle = find("spiralstairsmiddle")
    val spiral_down = find("spiralstairsdown")
    val wooden_spiral_top = find("spiralstairstop_wooden")
    val wooden_spiral_bottom = find("spiralstairs_wooden")
    val wooden_spiral_middle = find("spiralstairsmiddle_wooden")
    val wooden_spiral_down = find("spiralstairsdown_wooden")
    val lumbridge_spiral_bottom = find("spiralstairsbottom_3")
    val lumbridge_spiral_top = find("spiralstairstop_3")
}

/**
 * Spiral staircases keep their cache ops, as in OSRS: "Top-floor"/"Bottom-floor" are not added by
 * the server but belong to dedicated loc types. Lumbridge Castle's three-floor staircases use
 * `spiralstairsbottom_3` on the ground floor ("Climb-up", "Top-floor") and `spiralstairstop_3` on
 * the top floor ("Climb-down", "Bottom-floor"), while its middle floor is a plain
 * `spiralstairsmiddle` ("Climb", "Climb-up", "Climb-down"). Every other staircase only has the
 * regular climb ops.
 */
// The ops below match the cache. They are restated because earlier builds added "Top-floor" and
// "Bottom-floor" to every staircase, and a packed game cache only repacks a loc when an edit for
// it differs: without these ops, servers would keep serving the old menu. An edit that sets any op
// replaces the loc's whole op list.
internal object StaircaseLocEdits : LocEditor() {
    init {
        up(staircase_locs.spiral_bottom)
        up(staircase_locs.wooden_spiral_bottom)
        up(staircase_locs.lumbridge_spiral_bottom, TOP_FLOOR)

        down(staircase_locs.spiral_top)
        down(staircase_locs.spiral_down)
        down(staircase_locs.wooden_spiral_top)
        down(staircase_locs.wooden_spiral_down)
        down(staircase_locs.lumbridge_spiral_top, BOTTOM_FLOOR)

        option(staircase_locs.spiral_middle)
        option(staircase_locs.wooden_spiral_middle)
    }

    private fun up(type: LocType, op2: String? = null) =
        edit(type) {
            contentGroup = content.spiralstaircase_up
            op1 = "Climb-up"
            this.op2 = op2
        }

    private fun down(type: LocType, op2: String? = null) =
        edit(type) {
            contentGroup = content.spiralstaircase_down
            op1 = "Climb-down"
            this.op2 = op2
        }

    private fun option(type: LocType) =
        edit(type) {
            contentGroup = content.spiralstaircase_option
            op1 = "Climb"
            op2 = "Climb-up"
            op3 = "Climb-down"
        }

    const val TOP_FLOOR = "Top-floor"
    const val BOTTOM_FLOOR = "Bottom-floor"
}
