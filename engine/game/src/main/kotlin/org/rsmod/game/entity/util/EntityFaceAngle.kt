package org.rsmod.game.entity.util

@JvmInline
public value class EntityFaceAngle(public val intValue: Int) {
    public companion object {
        public val ZERO: EntityFaceAngle = EntityFaceAngle(0)
        public val NULL: EntityFaceAngle = EntityFaceAngle(-1)

        public fun fromOrNull(nullableAngle: Int?): EntityFaceAngle =
            nullableAngle?.let(::EntityFaceAngle) ?: NULL
    }
}
