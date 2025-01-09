package org.rsmod.game.loc

import org.rsmod.routefinder.loc.LocAngleConstants

public enum class LocAngle(public val id: Int) {
    West(LocAngleConstants.WEST),
    North(LocAngleConstants.NORTH),
    East(LocAngleConstants.EAST),
    South(LocAngleConstants.SOUTH);

    public fun turn(rotations: Int): LocAngle {
        val newAngle = (id + rotations) and LocEntity.ANGLE_BIT_MASK
        return LocAngle[newAngle]
    }

    public companion object {
        public operator fun get(id: Int): LocAngle =
            when (id) {
                West.id -> West
                North.id -> North
                East.id -> East
                South.id -> South
                else -> throw IllegalArgumentException("`id` not mapped to a LocAngle: $id")
            }
    }
}
