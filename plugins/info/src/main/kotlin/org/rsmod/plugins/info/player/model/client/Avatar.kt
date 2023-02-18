package org.rsmod.plugins.info.player.model.client

import org.rsmod.plugins.info.player.model.coord.HighResCoord

public class Avatar(
    public var registered: Boolean = false,
    public var coords: HighResCoord = HighResCoord.ZERO,
    public var prevCoords: HighResCoord = HighResCoord.ZERO,
    public var extendedInfoLength: Int = 0,
    public var dynamicExtInfoUpdateClock: Int = 0
)

public inline val Avatar.isValid: Boolean get() = registered
public inline val Avatar.isInvalid: Boolean get() = !isValid

public fun Avatar.clean() {
    registered = false
    coords = HighResCoord.ZERO
    prevCoords = HighResCoord.ZERO
    extendedInfoLength = 0
    dynamicExtInfoUpdateClock = 0
}
