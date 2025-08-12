package org.rsmod.game.type.seq

@DslMarker private annotation class SeqBuilderDsl

@SeqBuilderDsl
public class SeqTypeBuilder(public var internal: String? = null) {
    public var frameGroup: ShortArray? = null
    public var frameIndex: ShortArray? = null
    public var delay: ShortArray? = null
    public var loops: Int? = null
    // NOTE(optimization): can use a smaller data type array - either a short or byte array.
    public var walkMerge: IntArray? = null
    public var stretches: Boolean? = null
    public var priority: Int? = null
    public var replaceHeldRight: Int? = null
    public var replaceHeldLeft: Int? = null
    public var maxLoops: Int? = null
    public var preanimMove: PreanimMove? = null
    public var postanimMove: PostanimMove? = null
    public var duplicateBehaviour: Int? = null
    public var iframeGroup: ShortArray? = null
    public var iframeIndex: ShortArray? = null
    public var sounds: Array<SeqFrameSound>? = null
    public var mayaAnimationSounds: Map<Int, SeqFrameSound>? = null
    public var keyframeSet: Int? = null
    public var keyframeRangeStart: Int? = null
    public var keyframeRangeEnd: Int? = null
    public var keyframeWalkMerge: BooleanArray? = null
    public var debugName: String? = null
    public var crossWorldSound: Boolean? = null

    public fun build(id: Int): UnpackedSeqType {
        val internal = checkNotNull(internal) { "`internal` must be set. (id=$id)" }
        val frameGroup = checkNotNull(frameGroup) { "`frameGroup` must be set. (id=$id)" }
        val frameIndex = checkNotNull(frameIndex) { "`frameIndex` must be set. (id=$id)" }
        val delay = checkNotNull(delay) { "`delay` must be set. (id=$id)" }
        val loops = loops ?: DEFAULT_LOOPS
        val walkMerge = walkMerge ?: IntArray(0)
        val stretches = stretches == true
        val priority = priority ?: DEFAULT_PRIORITY
        val replaceHeldRight = replaceHeldRight ?: DEFAULT_REPLACE_HELD_RIGHT
        val replaceHeldLeft = replaceHeldLeft ?: DEFAULT_REPLACE_HELD_LEFT
        val maxLoops = maxLoops ?: DEFAULT_MAX_LOOPS
        val preanimMove = preanimMove
        val postanimMove = postanimMove
        val duplicateBehaviour = duplicateBehaviour ?: DEFAULT_DUPLICATE_BEHAVIOUR
        val iframeGroup = iframeGroup ?: ShortArray(0)
        val iframeIndex = iframeIndex ?: ShortArray(0)
        val sounds = sounds ?: emptyArray()
        val keyframeSet = keyframeSet ?: DEFAULT_KEYFRAME_SET
        val mayaAnimationSounds = mayaAnimationSounds
        val keyframeRangeStart = keyframeRangeStart ?: 0
        val keyframeRangeEnd = keyframeRangeEnd ?: 0
        val keyframeWalkMerge = keyframeWalkMerge ?: BooleanArray(0)
        val crossWorldSound = crossWorldSound ?: false
        return UnpackedSeqType(
            frameGroup = frameGroup,
            frameIndex = frameIndex,
            delay = delay,
            loops = loops,
            walkMerge = walkMerge,
            stretches = stretches,
            replaceHeldRight = replaceHeldRight,
            replaceHeldLeft = replaceHeldLeft,
            maxLoops = maxLoops,
            preanimMove = preanimMove,
            postanimMove = postanimMove,
            duplicateBehaviour = duplicateBehaviour,
            iframeGroup = iframeGroup,
            iframeIndex = iframeIndex,
            sounds = sounds,
            keyframeSet = keyframeSet,
            mayaAnimationSounds = mayaAnimationSounds,
            keyframeRangeStart = keyframeRangeStart,
            keyframeRangeEnd = keyframeRangeEnd,
            keyframeWalkMerge = keyframeWalkMerge,
            debugName = debugName,
            crossWorldSound = crossWorldSound,
            internalPriority = priority,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object {
        public const val DEFAULT_LOOPS: Int = -1
        public const val DEFAULT_PRIORITY: Int = 5
        public const val DEFAULT_REPLACE_HELD_RIGHT: Int = -1
        public const val DEFAULT_REPLACE_HELD_LEFT: Int = -1
        public const val DEFAULT_MAX_LOOPS: Int = 99
        public const val DEFAULT_DUPLICATE_BEHAVIOUR: Int = 2
        public const val DEFAULT_KEYFRAME_SET: Int = -1
    }
}
