package org.rsmod.game.pathfinder

public class Route(
    public val waypoints: List<RouteCoordinates>,
    public val alternative: Boolean,
    public val success: Boolean
) : List<RouteCoordinates> by waypoints {

    public val failed: Boolean
        get() = !success

    override fun toString(): String {
        return "Route(success=$success, alternative=$alternative, waypoints=$waypoints)"
    }
}
