package org.rsmod.game.model.route

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.mob.move.MovementSpeed

public interface RouteRequest {

    public val speed: MovementSpeed
}

public data class RouteRequestCoordinates(
    public override val speed: MovementSpeed,
    public val destination: Coordinates
) : RouteRequest

public data class RouteRequestEntity(
    public override val speed: MovementSpeed,
    public val destination: Entity
) : RouteRequest
