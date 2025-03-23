package org.rsmod.game.type.proj

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class ProjAnimType : CacheType()

public data class HashedProjAnimType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, ProjAnimType() {
    override fun toString(): String =
        "ProjAnimType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedProjAnimType) return false
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

public data class UnpackedProjAnimType(
    public val startHeight: Int,
    public val endHeight: Int,
    public val delay: Int,
    public val angle: Int,
    public val lengthAdjustment: Int,
    public val progress: Int,
    public val stepMultiplier: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : ProjAnimType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedProjAnimType =
        HashedProjAnimType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = startHeight.toLong()
        result = 61 * result + endHeight
        result = 61 * result + delay
        result = 61 * result + angle
        result = 61 * result + lengthAdjustment
        result = 61 * result + progress
        result = 61 * result + stepMultiplier
        result = 61 * result + (internalId ?: 0)
        return result
    }

    override fun toString(): String =
        "UnpackedProjAnimType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "startHeight=$startHeight, " +
            "endHeight=$endHeight, " +
            "delay=$delay, " +
            "angle=$angle, " +
            "lengthAdjustment=$lengthAdjustment, " +
            "progress=$progress, " +
            "stepMultiplier=$stepMultiplier" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedProjAnimType) return false
        if (internalId != other.internalId) return false
        if (startHeight != other.startHeight) return false
        if (endHeight != other.endHeight) return false
        if (delay != other.delay) return false
        if (angle != other.angle) return false
        if (lengthAdjustment != other.lengthAdjustment) return false
        if (progress != other.progress) return false
        if (stepMultiplier != other.stepMultiplier) return false
        return true
    }

    override fun hashCode(): Int {
        var result = startHeight
        result = 31 * result + endHeight
        result = 31 * result + delay
        result = 31 * result + angle
        result = 31 * result + lengthAdjustment
        result = 31 * result + progress
        result = 31 * result + stepMultiplier
        return result
    }
}
