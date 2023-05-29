package org.rsmod.game.model.route

import org.rsmod.game.model.mob.move.MovementSpeed

public interface RouteRequest {

    public val async: Boolean
    public val speed: MovementSpeed?
}
