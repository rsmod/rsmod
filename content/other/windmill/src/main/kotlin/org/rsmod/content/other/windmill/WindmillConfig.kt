package org.rsmod.content.other.windmill

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias windmill_locs = WindmillLocs

internal object WindmillLocs : LocReferences() {
    val ladder_up = find("qip_cook_ladder", 1865137527552917217)
    val ladder_option = find("qip_cook_ladder_middle", 1367500736205422751)
    val ladder_down = find("qip_cook_ladder_top", 7410446485423123414)
}
