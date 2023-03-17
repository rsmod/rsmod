package org.rsmod.plugins.api.cache.map.loc

public data class MapLoc(
    public val id: Int,
    public val localCoords: Int,
    public val attributes: Int
) {

    public val shape: Int get() = attributes shr 2

    public val rot: Int get() = attributes and 0x3

    public val localX: Int get() = (localCoords shr 6) and 0x3F

    public val localZ: Int get() = localCoords and 0x3F

    public val level: Int get() = (localCoords shr 12) and 0x3
}
