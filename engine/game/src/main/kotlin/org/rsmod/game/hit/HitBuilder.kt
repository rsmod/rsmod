package org.rsmod.game.hit

import org.rsmod.game.type.obj.ObjType

public class HitBuilder(
    public val type: HitType,
    public var damage: Int,
    public val sourceUid: Int?,
    public val sourceSlot: Int?,
    public val isFromNpc: Boolean,
    public val isFromPlayer: Boolean,
    public var clientDelay: Int?,
    private var righthandType: Int?,
    private var secondaryType: Int?,
    private val targetHitmark: Int,
    private val sourceHitmark: Int,
    private val publicHitmark: Int?,
    private val zeroDamageHitmarkLit: Int?,
    private val zeroDamageHitmarkTint: Int?,
    private val maxDamageHitmarkLit: Int?,
    private val targetMaxDamageThreshold: Int,
    private val sourceMaxDamageThreshold: Int,
) {
    public fun isRighthandObj(type: ObjType): Boolean = type.id == righthandType

    public fun isSecondaryObj(type: ObjType): Boolean = type.id == secondaryType

    public fun build(): Hit {
        val hitmark = buildHitmark()
        return Hit(
            type = type,
            hitmark = hitmark,
            sourceUid = sourceUid,
            righthandObj = righthandType,
            secondaryObj = secondaryType,
        )
    }

    private fun buildHitmark(): Hitmark {
        val clientDelay = checkNotNull(clientDelay) { "`clientDelay` must be set." }

        var target = targetHitmark
        var source = sourceHitmark
        var public = publicHitmark

        if (damage == 0) {
            target = zeroDamageHitmarkLit ?: targetHitmark
            source = zeroDamageHitmarkLit ?: sourceHitmark
            if (publicHitmark != null) {
                public = zeroDamageHitmarkTint ?: publicHitmark
            }
        } else {
            if (damage >= targetMaxDamageThreshold) {
                target = maxDamageHitmarkLit ?: targetHitmark
            }
            if (damage >= sourceMaxDamageThreshold) {
                source = maxDamageHitmarkLit ?: sourceHitmark
            }
        }

        return when {
            sourceSlot == null ->
                Hitmark.fromNoSource(
                    self = target,
                    source = source,
                    public = public,
                    damage = damage,
                    delay = clientDelay,
                )

            isFromNpc ->
                Hitmark.fromNpcSource(
                    self = target,
                    source = source,
                    public = public,
                    damage = damage,
                    delay = clientDelay,
                    slotId = sourceSlot,
                )

            isFromPlayer ->
                Hitmark.fromPlayerSource(
                    self = target,
                    source = source,
                    public = public,
                    damage = damage,
                    delay = clientDelay,
                    slotId = sourceSlot,
                )

            else -> throw IllegalStateException() // Should be unreachable.
        }
    }
}
