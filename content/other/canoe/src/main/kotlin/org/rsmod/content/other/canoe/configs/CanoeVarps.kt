package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.builders.varp.VarpBuilder
import org.rsmod.api.type.refs.varp.VarpReferences

typealias canoe_varps = CanoeVarps

object CanoeVarps : VarpReferences() {
    val station_coords = find("canoe_station_coords")
}

internal object CanoeVarpBuilder : VarpBuilder() {
    init {
        build("canoe_station_coords") { temporary = true }
    }
}
