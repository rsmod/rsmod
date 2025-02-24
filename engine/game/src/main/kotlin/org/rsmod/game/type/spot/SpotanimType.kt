package org.rsmod.game.type.spot

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class SpotanimType : CacheType()

public data class HashedSpotanimType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, SpotanimType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "SpotanimType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedSpotanimType) return false
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

public data class UnpackedSpotanimType(
    public val model: Int,
    public val anim: Int,
    public val ambient: Int,
    public val contrast: Int,
    public val rotation: Int,
    public val resizeH: Int,
    public val resizeV: Int,
    public val recolS: ShortArray,
    public val recolD: ShortArray,
    public val retexS: ShortArray,
    public val retexD: ShortArray,
    override var internalId: Int?,
    override var internalName: String?,
) : SpotanimType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedSpotanimType =
        HashedSpotanimType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = model.toLong()
        result = 61 * result + anim
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedSpotanimType(" +
            "internalName=$internalName, " +
            "internalId=$internalId, " +
            "model=$model, " +
            "anim=$anim, " +
            "ambient=$ambient, " +
            "contrast=$contrast, " +
            "rotation=$rotation, " +
            "resizeH=$resizeH, " +
            "resizeV=$resizeV, " +
            "recolS=${recolS.contentToString()}, " +
            "recolD=${recolD.contentToString()}, " +
            "retexS=${recolS.contentToString()}, " +
            "retexD=${recolD.contentToString()}" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedSpotanimType) return false
        if (model != other.model) return false
        if (anim != other.anim) return false
        if (ambient != other.ambient) return false
        if (contrast != other.contrast) return false
        if (rotation != other.rotation) return false
        if (resizeH != other.resizeH) return false
        if (resizeV != other.resizeV) return false
        if (!recolS.contentEquals(other.recolS)) return false
        if (!recolD.contentEquals(other.recolD)) return false
        if (!retexS.contentEquals(other.retexS)) return false
        if (!retexD.contentEquals(other.retexD)) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
