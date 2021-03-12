package org.rsmod.plugins.api.model.obj

/* credits: Joshua F */
@Suppress("UNUSED")
object ObjectShape {

    const val WALL = 0
    const val WALL_CORNER_DIAG = 1
    const val UNFINISHED_WALL = 2
    const val WALL_CORNER = 3
    const val WALL_DECOR_STRAIGHT_XOFFSET = 4
    const val WALL_DECOR_STRAIGHT_ZOFFSET = 5
    const val WALL_DECOR_DIAGONAL_XOFFSET = 6
    const val WALL_DECOR_DIAGONAL_ZOFFSET = 7
    const val INTERIOR_WALL_DECOR_DIAG = 8
    const val WALL_OPEN = 9
    const val COMPLEX_GROUND_DECOR = 10
    const val GROUND_DEFAULT = 11
    const val ROOF_TOP_SIDE = 12
    const val ROOF_TOP_CORNER_FLAT = 13
    const val ROOF_TOP_FLAT_DOWNWARD_CREASE = 14
    const val ROOF_TOP_SLANTED_UPWARD_CREASE = 15
    const val ROOF_TOP_SLANTED_DOWNWARD_CREASE = 16
    const val ROOF_TOP_FLAT = 17
    const val ROOF_EDGE = 18
    const val ROOF_EDGE_CORNER_FLAT = 19
    const val ROOF_CONNECTING_EDGE = 20
    const val ROOF_EDGE_CORNER_POINTED = 21
    const val GROUND_DECOR = 22

    val WALL_SHAPES = intArrayOf(
        WALL,
        WALL_CORNER_DIAG,
        UNFINISHED_WALL,
        WALL_CORNER
    )

    val WALL_DECOR_SHAPES = intArrayOf(
        WALL_DECOR_STRAIGHT_XOFFSET,
        WALL_DECOR_STRAIGHT_ZOFFSET,
        WALL_DECOR_DIAGONAL_XOFFSET,
        WALL_DECOR_DIAGONAL_ZOFFSET,
        INTERIOR_WALL_DECOR_DIAG
    )

    val NORMAL_SHAPES = intArrayOf(
        WALL_OPEN,
        ROOF_TOP_SIDE,
        ROOF_TOP_CORNER_FLAT,
        ROOF_TOP_FLAT_DOWNWARD_CREASE,
        ROOF_TOP_SLANTED_UPWARD_CREASE,
        ROOF_TOP_SLANTED_DOWNWARD_CREASE,
        ROOF_TOP_FLAT,
        ROOF_EDGE,
        ROOF_EDGE_CORNER_FLAT,
        ROOF_CONNECTING_EDGE,
        ROOF_EDGE_CORNER_POINTED,
        COMPLEX_GROUND_DECOR,
        GROUND_DEFAULT
    )

    val GROUND_DECOR_SHAPES = intArrayOf(
        GROUND_DECOR
    )
}
