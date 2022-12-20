package org.rsmod.game.pathfinder

public class Route(
    public val anchors: List<RouteCoordinates>,
    public val alternative: Boolean,
    public val success: Boolean
) : List<RouteCoordinates> by anchors {

    public val failed: Boolean
        get() = !success

    override fun toString(): String {
        return "Route(success=$success, alternative=$alternative, anchors=$anchors)"
    }
}
