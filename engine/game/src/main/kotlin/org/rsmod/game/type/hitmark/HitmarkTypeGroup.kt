package org.rsmod.game.type.hitmark

public data class HitmarkTypeGroup(
    val lit: HitmarkType,
    val tint: HitmarkType? = null,
    val max: HitmarkType? = null,
) {
    public fun isAssociatedWith(other: HitmarkTypeGroup): Boolean =
        lit.id == other.lit.id && tint?.id == other.tint?.id && max?.id == other.max?.id
}
