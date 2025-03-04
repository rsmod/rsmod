package org.rsmod.game.type.headbar

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

@DslMarker private annotation class HeadbarBuilderDsl

@HeadbarBuilderDsl
public class HeadbarTypeBuilder(public var internal: String? = null) {
    public var unknown1: Int? = null
    public var showPriority: Int? = null
    public var hidePriority: Int? = null
    public var fadeout: Int? = null
    public var stickTime: Int? = null
    public var unknown6: Int? = null
    public var full: Int? = null
    public var empty: Int? = null
    public var segments: Int? = null
    public var padding: Int? = null

    public fun build(id: Int): UnpackedHeadbarType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val unknown1 = unknown1 ?: DEFAULT_UNKNOWN1
        val showPriority = showPriority ?: DEFAULT_SHOW_PRIORITY
        val hidePriority = hidePriority ?: DEFAULT_HIDE_PRIORITY
        val fadeout = fadeout ?: DEFAULT_FADEOUT
        val stickTime = stickTime ?: DEFAULT_STICK_TIME
        val unknown6 = unknown6 ?: DEFAULT_UNKNOWN6
        val segments = segments ?: DEFAULT_SEGMENTS
        val padding = padding ?: 0
        return UnpackedHeadbarType(
            unknown1 = unknown1,
            showPriority = showPriority,
            hidePriority = hidePriority,
            fadeout = fadeout,
            stickTime = stickTime,
            unknown6 = unknown6,
            full = full,
            empty = empty,
            padding = padding,
            internalSegments = segments,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object : MergeableCacheBuilder<UnpackedHeadbarType> {
        public const val DEFAULT_UNKNOWN1: Int = 0
        public const val DEFAULT_SHOW_PRIORITY: Int = 255
        public const val DEFAULT_HIDE_PRIORITY: Int = 255
        public const val DEFAULT_FADEOUT: Int = -1
        public const val DEFAULT_STICK_TIME: Int = 70
        public const val DEFAULT_UNKNOWN6: Int = 0
        public const val DEFAULT_SEGMENTS: Int = 30

        override fun merge(
            edit: UnpackedHeadbarType,
            base: UnpackedHeadbarType,
        ): UnpackedHeadbarType {
            val unknown1 = select(edit, base, DEFAULT_UNKNOWN1) { unknown1 }
            val showPriority = select(edit, base, DEFAULT_SHOW_PRIORITY) { showPriority }
            val hidePriority = select(edit, base, DEFAULT_HIDE_PRIORITY) { hidePriority }
            val fadeout = select(edit, base, DEFAULT_FADEOUT) { fadeout }
            val stickTime = select(edit, base, DEFAULT_STICK_TIME) { stickTime }
            val unknown6 = select(edit, base, DEFAULT_UNKNOWN6) { unknown6 }
            val full = select(edit, base, default = null) { full }
            val empty = select(edit, base, default = null) { empty }
            val segments = select(edit, base, DEFAULT_SEGMENTS) { segments }
            val padding = select(edit, base, default = 0) { padding }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedHeadbarType(
                unknown1 = unknown1,
                showPriority = showPriority,
                hidePriority = hidePriority,
                fadeout = fadeout,
                stickTime = stickTime,
                unknown6 = unknown6,
                full = full,
                empty = empty,
                padding = padding,
                internalSegments = segments,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
