package org.rsmod.game.model.route

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.move.MovementSpeed

public data class RouteRequestCoordinates(
    public val destination: Coordinates,
    public override val speed: MovementSpeed? = null,
    public override val async: Boolean = false
) : RouteRequest
