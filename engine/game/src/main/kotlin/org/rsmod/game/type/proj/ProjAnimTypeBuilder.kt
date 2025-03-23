package org.rsmod.game.type.proj

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

public class ProjAnimTypeBuilder(public var internal: String? = null) {
    public var startHeight: Int? = null
    public var endHeight: Int? = null
    public var delay: Int? = null
    public var angle: Int? = null
    public var lengthAdjustment: Int? = null
    public var progress: Int? = null
    public var stepMultiplier: Int? = null

    public fun build(id: Int): UnpackedProjAnimType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val startHeight = checkNotNull(startHeight) { "`startHeight` must be set." }
        val endHeight = checkNotNull(endHeight) { "`endHeight` must be set." }
        val delay = checkNotNull(delay) { "`delay` must be set." }
        val angle = checkNotNull(angle) { "`angle` must be set." }
        val lengthAdjustment = lengthAdjustment ?: 0
        val progress = progress ?: DEFAULT_PROGRESS
        val stepMultiplier = stepMultiplier ?: DEFAULT_STEP_MULTIPLIER
        return UnpackedProjAnimType(
            startHeight = startHeight,
            endHeight = endHeight,
            delay = delay,
            angle = angle,
            lengthAdjustment = lengthAdjustment,
            progress = progress,
            stepMultiplier = stepMultiplier,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object : MergeableCacheBuilder<UnpackedProjAnimType> {
        public const val DEFAULT_PROGRESS: Int = 11
        public const val DEFAULT_STEP_MULTIPLIER: Int = 5

        override fun merge(
            edit: UnpackedProjAnimType,
            base: UnpackedProjAnimType,
        ): UnpackedProjAnimType {
            val startHeight = select(edit, base, default = 0) { startHeight }
            val endHeight = select(edit, base, default = 0) { endHeight }
            val delay = select(edit, base, default = 0) { delay }
            val angle = select(edit, base, default = 0) { angle }
            val lengthAdjustment = select(edit, base, default = 0) { lengthAdjustment }
            val progress = select(edit, base, DEFAULT_PROGRESS) { progress }
            val stepMultiplier = select(edit, base, DEFAULT_STEP_MULTIPLIER) { stepMultiplier }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedProjAnimType(
                startHeight = startHeight,
                endHeight = endHeight,
                delay = delay,
                angle = angle,
                lengthAdjustment = lengthAdjustment,
                progress = progress,
                stepMultiplier = stepMultiplier,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
