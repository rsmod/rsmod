package org.rsmod.game.type.seq

@DslMarker private annotation class SeqBuilderDsl

@SeqBuilderDsl
public class SeqTypeBuilder(public var internal: String? = null) {
    public var frameGroup: ShortArray? = null
    public var frameIndex: ShortArray? = null
    public var delay: ShortArray? = null
    public var replayOff: Int? = null
    // NOTE(optimization): can use a smaller data type array - either a short or byte array.
    public var walkMerge: IntArray? = null
    public var stretches: Boolean? = null
    public var priority: Int? = null
    public var mainhand: Int? = null
    public var offhand: Int? = null
    public var replayCount: Int? = null
    public var preanimMove: PreanimMove? = null
    public var postanimMove: PostanimMove? = null
    public var replaceMode: Int? = null
    public var iframeGroup: ShortArray? = null
    public var iframeIndex: ShortArray? = null
    public var sounds: Array<SeqFrameSound>? = null
    public var mayaAnimationSounds: Map<Int, SeqFrameSound>? = null
    public var keyframeSet: Int? = null
    public var keyframeRangeStart: Int? = null
    public var keyframeRangeEnd: Int? = null
    public var keyframeWalkMerge: BooleanArray? = null
    public var debugName: String? = null

    public fun build(id: Int): UnpackedSeqType {
        val internal = checkNotNull(internal) { "`internal` must be set. (id=$id)" }
        val frameGroup = checkNotNull(frameGroup) { "`frameGroup` must be set. (id=$id)" }
        val frameIndex = checkNotNull(frameIndex) { "`frameIndex` must be set. (id=$id)" }
        val delay = checkNotNull(delay) { "`delay` must be set. (id=$id)" }
        val replayOff = replayOff ?: DEFAULT_REPLAY_OFF
        val walkMerge = walkMerge ?: IntArray(0)
        val stretches = stretches == true
        val priority = priority ?: DEFAULT_PRIORITY
        val mainhand = mainhand ?: DEFAULT_MAINHAND
        val offhand = offhand ?: DEFAULT_OFFHAND
        val replayCount = replayCount ?: DEFAULT_REPLAY_COUNT
        val preanimMove = preanimMove
        val postanimMove = postanimMove
        val replaceMode = replaceMode ?: DEFAULT_REPLACE_MODE
        val iframeGroup = iframeGroup ?: ShortArray(0)
        val iframeIndex = iframeIndex ?: ShortArray(0)
        val sounds = sounds ?: emptyArray()
        val keyframeSet = keyframeSet ?: DEFAULT_KEYFRAME_SET
        val mayaAnimationSounds = mayaAnimationSounds
        val keyframeRangeStart = keyframeRangeStart ?: 0
        val keyframeRangeEnd = keyframeRangeEnd ?: 0
        val keyframeWalkMerge = keyframeWalkMerge ?: BooleanArray(0)
        return UnpackedSeqType(
            frameGroup = frameGroup,
            frameIndex = frameIndex,
            delay = delay,
            replayOff = replayOff,
            walkMerge = walkMerge,
            stretches = stretches,
            mainhand = mainhand,
            offhand = offhand,
            replayCount = replayCount,
            preanimMove = preanimMove,
            postanimMove = postanimMove,
            replaceMode = replaceMode,
            iframeGroup = iframeGroup,
            iframeIndex = iframeIndex,
            sounds = sounds,
            keyframeSet = keyframeSet,
            mayaAnimationSounds = mayaAnimationSounds,
            keyframeRangeStart = keyframeRangeStart,
            keyframeRangeEnd = keyframeRangeEnd,
            keyframeWalkMerge = keyframeWalkMerge,
            debugName = debugName,
            internalPriority = priority,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object {
        public const val DEFAULT_REPLAY_OFF: Int = -1
        public const val DEFAULT_PRIORITY: Int = 5
        public const val DEFAULT_MAINHAND: Int = -1
        public const val DEFAULT_OFFHAND: Int = -1
        public const val DEFAULT_REPLAY_COUNT: Int = 99
        public const val DEFAULT_REPLACE_MODE: Int = 2
        public const val DEFAULT_KEYFRAME_SET: Int = -1
    }
}
