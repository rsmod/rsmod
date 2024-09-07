package org.rsmod.api.obj.spawns

import org.rsmod.map.CoordGrid

public data class ParsedObjSpawn(
    public val obj: String,
    public val count: Int = 1,
    public val coords: CoordGrid,
)
