package org.rsmod.routefinder.loc

public object LocLayerConstants {
    public const val WALL: Int = 0
    public const val WALL_DECOR: Int = 1
    public const val GROUND: Int = 2
    public const val GROUND_DECOR: Int = 3

    public fun of(shape: Int): Int =
        when (shape) {
            LocShapeConstants.WALL_STRAIGHT,
            LocShapeConstants.WALL_DIAGONAL_CORNER,
            LocShapeConstants.WALL_L,
            LocShapeConstants.WALL_SQUARE_CORNER -> WALL
            LocShapeConstants.WALLDECOR_STRAIGHT_NOOFFSET,
            LocShapeConstants.WALLDECOR_STRAIGHT_OFFSET,
            LocShapeConstants.WALLDECOR_DIAGONAL_OFFSET,
            LocShapeConstants.WALLDECOR_DIAGONAL_NOOFFSET,
            LocShapeConstants.WALLDECOR_DIAGONAL_BOTH -> WALL_DECOR
            LocShapeConstants.WALL_DIAGONAL,
            LocShapeConstants.CENTREPIECE_STRAIGHT,
            LocShapeConstants.CENTREPIECE_DIAGONAL,
            LocShapeConstants.ROOF_STRAIGHT,
            LocShapeConstants.ROOF_DIAGONAL_WITH_ROOFEDGE,
            LocShapeConstants.ROOF_DIAGONAL,
            LocShapeConstants.ROOF_L_CONCAVE,
            LocShapeConstants.ROOF_L_CONVEX,
            LocShapeConstants.ROOF_FLAT,
            LocShapeConstants.ROOFEDGE_STRAIGHT,
            LocShapeConstants.ROOFEDGE_DIAGONAL_CORNER,
            LocShapeConstants.ROOFEDGE_L,
            LocShapeConstants.ROOFEDGE_SQUARE_CORNER -> GROUND
            LocShapeConstants.GROUND_DECOR -> GROUND_DECOR
            else -> throw NotImplementedError("Conversion for loc shape not implemented: $shape")
        }
}
