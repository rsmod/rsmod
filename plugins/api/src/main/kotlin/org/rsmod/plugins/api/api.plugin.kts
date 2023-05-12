package org.rsmod.plugins.api

import org.rsmod.game.model.route.RouteRequestCoordinates
import org.rsmod.plugins.api.model.event.UpstreamEvent

onEvent<UpstreamEvent.MoveGameClick> { event ->
    // TODO: verify speed is valid for player (displace should be admins+, etc)
    routeRequest = RouteRequestCoordinates(event.coords, event.moveSpeed(), async = true)
}
