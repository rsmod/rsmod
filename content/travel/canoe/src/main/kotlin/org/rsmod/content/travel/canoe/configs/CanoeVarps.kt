package org.rsmod.content.travel.canoe.configs

import org.rsmod.api.type.builders.varp.VarpBuilder
import org.rsmod.api.type.editors.varp.VarpEditor
import org.rsmod.api.type.refs.varp.VarpReferences

typealias canoe_varps = CanoeVarps

object CanoeVarps : VarpReferences() {
    val station_coords = find("canoe_station_coords")
    val river_lum = find("canoeing_river_lum")
    val river_lum_2 = find("canoeing_river_lum_2")
}

internal object CanoeVarpBuilder : VarpBuilder() {
    init {
        build("canoe_station_coords") { temporary = true }
    }
}

internal object CanoeVarpEditor : VarpEditor() {
    init {
        edit(canoe_varps.river_lum) { temporary = true }
        edit(canoe_varps.river_lum_2) { temporary = true }
    }
}
