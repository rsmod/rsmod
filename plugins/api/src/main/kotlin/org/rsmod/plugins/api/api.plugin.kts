package org.rsmod.plugins.api

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.subscribe
import org.rsmod.game.model.route.RouteRequestCoordinates
import org.rsmod.plugins.api.model.event.UpstreamEvent

private val events: GameEventBus by inject()

events.subscribe<UpstreamEvent.MoveGameClick> {
    // TODO: verify speed is valid for player (displace should be admins+, etc)
    player.routeRequest = RouteRequestCoordinates(speed, coords)
}
