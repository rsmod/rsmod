package org.rsmod.game.movement

import org.rsmod.game.entity.PathingEntityAvatar
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.map.CoordGrid

public sealed class RouteRequest(public val clientRequest: Boolean)

public class RouteRequestPathingEntity(
    public val destination: PathingEntityAvatar,
    clientRequest: Boolean = false,
) : RouteRequest(clientRequest)

public class RouteRequestCoord(public val destination: CoordGrid, clientRequest: Boolean = false) :
    RouteRequest(clientRequest)

public class RouteRequestLoc(
    public val destination: CoordGrid,
    public val width: Int,
    public val length: Int,
    public val shape: Int,
    public val angle: Int,
    public val forceApproachFlags: Int,
    clientRequest: Boolean = false,
) : RouteRequest(clientRequest) {
    public constructor(
        loc: LocInfo,
        width: Int,
        length: Int,
        forceApproachFlags: Int,
    ) : this(
        destination = loc.coords,
        width = width,
        length = length,
        shape = loc.shapeId,
        angle = loc.angleId,
        forceApproachFlags = forceApproachFlags,
    )

    public constructor(
        loc: LocInfo,
        type: UnpackedLocType,
    ) : this(
        loc = loc,
        width = type.width,
        length = type.length,
        forceApproachFlags = type.forceApproachFlags,
    )
}
