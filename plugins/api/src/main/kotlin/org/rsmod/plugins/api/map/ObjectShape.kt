package org.rsmod.plugins.api.map

public enum class ObjectShape(public val id: Int, public val slot: ObjectSlot) {

    WallStraight(id = 0, slot = ObjectSlot.Wall),
    WallDiagonalCorner(id = 1, slot = ObjectSlot.Wall),
    WallL(id = 2, slot = ObjectSlot.Wall),
    WallSquareCorner(id = 3, slot = ObjectSlot.Wall),
    WallDecorStraightNoOffset(id = 4, slot = ObjectSlot.Decor),
    WallDecorStraightOffset(id = 5, slot = ObjectSlot.Decor),
    WallDecorDiagonalOffset(id = 6, slot = ObjectSlot.Decor),
    WallDecorDiagonalNoOffset(id = 7, slot = ObjectSlot.Decor),
    WallDecorDiagonalBoth(id = 8, slot = ObjectSlot.Decor),
    WallDiagonal(id = 9, slot = ObjectSlot.Main),
    CenterpieceStraight(id = 10, slot = ObjectSlot.Main),
    CenterpieceDiagonal(id = 11, slot = ObjectSlot.Main),
    RoofStraight(id = 12, slot = ObjectSlot.Main),
    RoofDiagonalWithRoofEdge(id = 13, slot = ObjectSlot.Main),
    RoofDiagonal(id = 14, slot = ObjectSlot.Main),
    RoofLConcave(id = 15, slot = ObjectSlot.Main),
    RoofLConvex(id = 16, slot = ObjectSlot.Main),
    RoofFlat(id = 17, slot = ObjectSlot.Main),
    RoofEdgeStraight(id = 18, slot = ObjectSlot.Main),
    RoofEdgeDiagonalCorner(id = 19, slot = ObjectSlot.Main),
    RoodEdgeL(id = 20, slot = ObjectSlot.Main),
    RoofEdgeSquareCorner(id = 21, slot = ObjectSlot.Main),
    GroundDecor(id = 22, slot = ObjectSlot.GroundDetail);

    public companion object {

        public val values: Array<ObjectShape> = enumValues()

        public val mapped: Map<Int, ObjectShape> = values.associateBy { it.id }
    }
}
