package org.rsmod.api.npc.hit

import kotlin.math.max
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.hitmark_groups
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitBuilder
import org.rsmod.game.hit.HitType
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.hitmark.HitmarkTypeGroup
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.Wearpos

internal object InternalNpcHits {
    private val Player.righthand: InvObj?
        get() = worn[Wearpos.RightHand.slot]

    fun createBuilder(
        source: Npc,
        type: HitType,
        damage: Int,
        righthand: ObjType?,
        secondaryObj: ObjType?,
        hitmark: HitmarkTypeGroup,
        clientDelay: Int,
    ): HitBuilder {
        val righthand = righthand?.id
        val secondary = secondaryObj?.id
        return createBuilder(
            type = type,
            hitmark = hitmark,
            damage = damage,
            righthandObj = righthand,
            secondaryObj = secondary,
            sourceMaxDamageThreshold = null,
            sourceUid = source.uid.packed,
            sourceNpcSlot = source.slotId,
            sourcePlayerSlot = null,
            clientDelay = clientDelay,
            isPrivateHit = false,
        )
    }

    fun createBuilder(
        source: Player,
        type: HitType,
        damage: Int,
        secondaryObj: ObjType?,
        hitmark: HitmarkTypeGroup,
        clientDelay: Int,
        specific: Boolean,
    ): HitBuilder {
        val righthand = source.righthand?.id
        val secondary = secondaryObj?.id
        val sourceMaxHit = source.currentMaxHit()
        val sourceMaxThreshold = source.maxDamageLitThreshold(sourceMaxHit)
        return createBuilder(
            type = type,
            hitmark = hitmark,
            damage = damage,
            righthandObj = righthand,
            secondaryObj = secondary,
            sourceMaxDamageThreshold = sourceMaxThreshold,
            sourceUid = source.uid.packed,
            sourceNpcSlot = null,
            sourcePlayerSlot = source.slotId,
            clientDelay = clientDelay,
            isPrivateHit = specific,
        )
    }

    fun createBuilder(
        type: HitType,
        damage: Int,
        righthand: ObjType?,
        secondaryObj: ObjType?,
        hitmark: HitmarkTypeGroup,
        clientDelay: Int,
    ): HitBuilder {
        val righthand = righthand?.id
        val secondary = secondaryObj?.id
        return createBuilder(
            type = type,
            hitmark = hitmark,
            damage = damage,
            righthandObj = righthand,
            secondaryObj = secondary,
            sourceMaxDamageThreshold = null,
            sourceUid = null,
            sourceNpcSlot = null,
            sourcePlayerSlot = null,
            clientDelay = clientDelay,
            isPrivateHit = false,
        )
    }

    private fun createBuilder(
        type: HitType,
        hitmark: HitmarkTypeGroup,
        damage: Int,
        righthandObj: Int?,
        secondaryObj: Int?,
        sourceMaxDamageThreshold: Int?,
        sourceUid: Int?,
        sourceNpcSlot: Int?,
        sourcePlayerSlot: Int?,
        clientDelay: Int,
        isPrivateHit: Boolean,
    ): HitBuilder {
        val publicHitmark =
            when {
                isPrivateHit -> null
                hitmark.tint != null -> hitmark.tint?.id
                else -> hitmark.lit.id
            }
        val zeroDamageHitmarks = if (hitmark.isRegularDamage()) hitmark_groups.zero_damage else null
        return HitBuilder(
            type = type,
            damage = damage,
            sourceUid = sourceUid,
            sourceSlot = sourcePlayerSlot ?: sourceNpcSlot,
            isFromNpc = sourceNpcSlot != null,
            isFromPlayer = sourcePlayerSlot != null,
            clientDelay = clientDelay,
            righthandType = righthandObj,
            secondaryType = secondaryObj,
            targetHitmark = hitmark.lit.id,
            sourceHitmark = hitmark.lit.id,
            publicHitmark = publicHitmark,
            zeroDamageHitmarkLit = zeroDamageHitmarks?.lit?.id,
            zeroDamageHitmarkTint = zeroDamageHitmarks?.tint?.id,
            maxDamageHitmarkLit = hitmark.max?.id,
            targetMaxDamageThreshold = Int.MAX_VALUE,
            sourceMaxDamageThreshold = sourceMaxDamageThreshold ?: Int.MAX_VALUE,
        )
    }

    private fun HitmarkTypeGroup.isRegularDamage(): Boolean =
        isAssociatedWith(hitmark_groups.regular_damage)

    private fun Player.currentMaxHit(): Int = vars[varps.com_maxhit]

    private fun Player.maxDamageLitThreshold(sourceMaxHit: Int): Int? {
        val varThreshold = vars[varbits.settings_hitsplat_threshold]
        val minThreshold = max(constants.setting_lit_maxhit_min_threshold, varThreshold)
        val threshold = max(minThreshold, sourceMaxHit)
        return if (threshold == 0) {
            null
        } else {
            threshold
        }
    }
}
