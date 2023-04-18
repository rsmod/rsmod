package org.rsmod.game.pathfinder

public data class RayCast(
    public val coordinates: List<RouteCoordinates>,
    public val alternative: Boolean,
    public val success: Boolean
) : List<RouteCoordinates> by coordinates {

    public companion object {

        public val FAILED: RayCast = RayCast(
            coordinates = emptyList(),
            alternative = false,
            success = false
        )

        public val EMPTY_SUCCESS: RayCast = RayCast(
            coordinates = emptyList(),
            alternative = false,
            success = true
        )
    }
}
