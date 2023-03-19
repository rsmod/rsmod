package org.rsmod.game.model.route

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.mob.move.MovementSpeed

public interface RouteRequest {

    public val async: Boolean
    public val speed: MovementSpeed
}

public data class RouteRequestCoordinates(
    public val destination: Coordinates,
    public override val speed: MovementSpeed,
    public override val async: Boolean = false
) : RouteRequest

public data class RouteRequestEntity(
    public val destination: Entity,
    public override val speed: MovementSpeed,
    public override val async: Boolean = false
) : RouteRequest
