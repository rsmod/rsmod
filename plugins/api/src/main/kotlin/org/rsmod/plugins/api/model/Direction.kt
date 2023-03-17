package org.rsmod.plugins.api.model

public enum class Direction(public val offX: Int, public val offZ: Int) {
    South(0, -1),
    North(0, 1),
    West(-1, 0),
    East(1, 0),
    SouthWest(-1, -1),
    NorthWest(-1, 1),
    SouthEast(1, -1),
    NorthEast(1, 1);

    public companion object {

        public val values: Array<Direction> = enumValues()
    }
}
