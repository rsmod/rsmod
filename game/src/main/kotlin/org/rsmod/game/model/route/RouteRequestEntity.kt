package org.rsmod.game.model.route

import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.mob.move.MovementSpeed

public data class RouteRequestEntity(
    public val destination: Entity,
    public override val speed: MovementSpeed? = null,
    public override val async: Boolean = false
) : RouteRequest
