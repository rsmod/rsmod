package org.rsmod.game.type.seq

import kotlin.math.ceil
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class SeqType : CacheType() {
    internal abstract var internalPriority: Int

    public val priority: Int
        get() = internalPriority
}

public data class HashedSeqType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
    override var internalPriority: Int = 0,
) : HashedCacheType, SeqType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "SeqType(internalId=$internalId, internalName=$internalName, supposedHash=$supposedHash)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedSeqType) return false
        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public data class UnpackedSeqType(
    public val frameGroup: ShortArray,
    public val frameIndex: ShortArray,
    public val delay: ShortArray,
    public val replayOff: Int,
    public val walkMerge: IntArray,
    public val stretches: Boolean,
    public val mainhand: Int,
    public val offhand: Int,
    public val replayCount: Int,
    public val preanimMove: PreanimMove?,
    public val postanimMove: PostanimMove?,
    public val replaceMode: Int,
    public val iframeGroup: ShortArray,
    public val iframeIndex: ShortArray,
    public val sounds: Array<SeqFrameSound>,
    public val keyframeSet: Int,
    public val mayaAnimationSounds: Map<Int, SeqFrameSound>?,
    public val keyframeRangeStart: Int,
    public val keyframeRangeEnd: Int,
    public val keyframeWalkMerge: BooleanArray,
    public val totalDelay: Int = delay.sum(),
    public val tickDuration: Int = delay.tickDuration(),
    override var internalPriority: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : SeqType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedSeqType =
        HashedSeqType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
            internalPriority = priority,
        )

    public fun computeIdentityHash(): Long {
        var result = frameGroup.contentHashCode().toLong()
        result = 61 * result + frameIndex.contentHashCode()
        result = 61 * result + delay.contentHashCode()
        result = 61 * result + replayOff
        result = 61 * result + walkMerge.contentHashCode()
        result = 61 * result + mainhand
        result = 61 * result + offhand
        result = 61 * result + replayCount
        result = 61 * result + (preanimMove?.id ?: -1)
        result = 61 * result + (postanimMove?.id ?: -1)
        result = 61 * result + replaceMode
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedSeqType(" +
            "internalName=$internalName, " +
            "internalId=$internalId, " +
            "frameGroup=${frameGroup.contentToString()}, " +
            "frameIndex=${frameIndex.contentToString()}, " +
            "delay=${delay.contentToString()}, " +
            "replayOff=$replayOff, " +
            "walkMerge=${walkMerge.contentToString()}, " +
            "stretches=$stretches, " +
            "priority=$priority, " +
            "mainhand=$mainhand, " +
            "offhand=$offhand, " +
            "replayCount=$replayCount, " +
            "preanimMove=$preanimMove, " +
            "postanimMove=$postanimMove, " +
            "replaceMode=$replaceMode, " +
            "iframeGroup=${iframeGroup.contentToString()}, " +
            "iframeIndex=${iframeIndex.contentToString()}, " +
            "sounds=${sounds.contentToString()}, " +
            "keyframeSet=$keyframeSet, " +
            "mayaAnimationSounds=$mayaAnimationSounds, " +
            "keyframeRangeStart=$keyframeRangeStart, " +
            "keyframeRangeEnd=$keyframeRangeEnd, " +
            "keyframeWalkMerge=${keyframeWalkMerge.contentToString()}" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedSeqType) return false

        if (!frameGroup.contentEquals(other.frameGroup)) return false
        if (!frameIndex.contentEquals(other.frameIndex)) return false
        if (!delay.contentEquals(other.delay)) return false
        if (replayOff != other.replayOff) return false
        if (!walkMerge.contentEquals(other.walkMerge)) return false
        if (stretches != other.stretches) return false
        if (priority != other.priority) return false
        if (mainhand != other.mainhand) return false
        if (offhand != other.offhand) return false
        if (replayCount != other.replayCount) return false
        if (preanimMove != other.preanimMove) return false
        if (postanimMove != other.postanimMove) return false
        if (replaceMode != other.replaceMode) return false
        if (!iframeGroup.contentEquals(other.iframeGroup)) return false
        if (!iframeIndex.contentEquals(other.iframeIndex)) return false
        if (!sounds.contentEquals(other.sounds)) return false
        if (keyframeSet != other.keyframeSet) return false
        if (mayaAnimationSounds != other.mayaAnimationSounds) return false
        if (keyframeRangeStart != other.keyframeRangeStart) return false
        if (keyframeRangeEnd != other.keyframeRangeEnd) return false
        if (!keyframeWalkMerge.contentEquals(other.keyframeWalkMerge)) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}

private fun ShortArray.tickDuration(): Int {
    val validDelays = dropLastWhile { it > 30 }
    val buffer = if (validDelays.size != size) 5 else 0
    val duration = (validDelays.sum() + buffer) * 20
    return ceil(duration / 600.0).toInt()
}
