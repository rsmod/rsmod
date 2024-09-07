package org.rsmod.game.loc

import org.rsmod.pathfinder.loc.LocShapeConstants

public enum class LocShape(public val id: Int) {
    WallStraight(LocShapeConstants.WALL_STRAIGHT),
    WallDiagonalCorner(LocShapeConstants.WALL_DIAGONAL_CORNER),
    WallL(LocShapeConstants.WALL_L),
    WallSquareCorner(LocShapeConstants.WALL_SQUARE_CORNER),
    WallDecorStraightNoOffset(LocShapeConstants.WALLDECOR_STRAIGHT_NOOFFSET),
    WallDecorStraightOffset(LocShapeConstants.WALLDECOR_STRAIGHT_OFFSET),
    WallDecorDiagonalOffset(LocShapeConstants.WALLDECOR_DIAGONAL_OFFSET),
    WallDecorDiagonalNoOffset(LocShapeConstants.WALLDECOR_DIAGONAL_NOOFFSET),
    WallDecorDiagonalBoth(LocShapeConstants.WALLDECOR_DIAGONAL_BOTH),
    WallDiagonal(LocShapeConstants.WALL_DIAGONAL),
    CentrepieceStraight(LocShapeConstants.CENTREPIECE_STRAIGHT),
    CentrepieceDiagonal(LocShapeConstants.CENTREPIECE_DIAGONAL),
    RoofStraight(LocShapeConstants.ROOF_STRAIGHT),
    RoofDiagonalWithRoofEdge(LocShapeConstants.ROOF_DIAGONAL_WITH_ROOFEDGE),
    RoofDiagonal(LocShapeConstants.ROOF_DIAGONAL),
    RoofLConcave(LocShapeConstants.ROOF_L_CONCAVE),
    RoofLConvex(LocShapeConstants.ROOF_L_CONVEX),
    RoofFlat(LocShapeConstants.ROOF_FLAT),
    RoofEdgeStraight(LocShapeConstants.ROOFEDGE_STRAIGHT),
    RoofEdgeDiagonalCorner(LocShapeConstants.ROOFEDGE_DIAGONAL_CORNER),
    RoofEdgeL(LocShapeConstants.ROOFEDGE_L),
    RoofEdgeSquareCorner(LocShapeConstants.ROOFEDGE_SQUARE_CORNER),
    GroundDecor(LocShapeConstants.GROUND_DECOR);

    public companion object {
        public operator fun get(id: Int): LocShape =
            when (id) {
                WallStraight.id -> WallStraight
                WallDiagonalCorner.id -> WallDiagonalCorner
                WallL.id -> WallL
                WallSquareCorner.id -> WallSquareCorner
                WallDecorStraightNoOffset.id -> WallDecorStraightNoOffset
                WallDecorStraightOffset.id -> WallDecorStraightOffset
                WallDecorDiagonalOffset.id -> WallDecorDiagonalOffset
                WallDecorDiagonalNoOffset.id -> WallDecorDiagonalNoOffset
                WallDecorDiagonalBoth.id -> WallDecorDiagonalBoth
                WallDiagonal.id -> WallDiagonal
                CentrepieceStraight.id -> CentrepieceStraight
                CentrepieceDiagonal.id -> CentrepieceDiagonal
                RoofStraight.id -> RoofStraight
                RoofDiagonalWithRoofEdge.id -> RoofDiagonalWithRoofEdge
                RoofDiagonal.id -> RoofDiagonal
                RoofLConcave.id -> RoofLConcave
                RoofLConvex.id -> RoofLConvex
                RoofFlat.id -> RoofFlat
                RoofEdgeStraight.id -> RoofEdgeStraight
                RoofEdgeDiagonalCorner.id -> RoofEdgeDiagonalCorner
                RoofEdgeL.id -> RoofEdgeL
                RoofEdgeSquareCorner.id -> RoofEdgeSquareCorner
                GroundDecor.id -> GroundDecor
                else -> throw IllegalArgumentException("`id` not mapped to a LocShape: $id")
            }
    }
}
