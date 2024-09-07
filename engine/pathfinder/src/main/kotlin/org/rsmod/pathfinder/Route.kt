package org.rsmod.pathfinder

public data class Route(
    public val waypoints: List<RouteCoordinates>,
    public val alternative: Boolean,
    public val success: Boolean,
) : List<RouteCoordinates> by waypoints {
    public val failed: Boolean
        get() = !success

    public companion object {
        public val FAILED: Route =
            Route(waypoints = emptyList(), alternative = false, success = false)
    }
}
