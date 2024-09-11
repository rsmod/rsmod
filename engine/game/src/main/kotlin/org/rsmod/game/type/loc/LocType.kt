package org.rsmod.game.type.loc

import kotlin.contracts.contract
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.util.ParamMap

public infix fun LocType.isAssociatedWith(loc: LocInfo?): Boolean {
    contract { returns(true) implies (loc != null) }
    return loc != null && loc.isAssociatedWith(this)
}

public sealed class LocType(internal var internalId: Int?, internal var internalName: String?) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String?
        get() = internalName
}

public class HashedLocType(
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
) : LocType(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

    override fun toString(): String =
        "LocType(internalName='$internalName', internalId=$internalId, supposedHash=$supposedHash)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedLocType) return false

        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false
        if (internalName != other.internalName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public class UnpackedLocType(
    public val models: IntArray,
    public val shapes: ByteArray,
    public val name: String,
    public val desc: String,
    public val width: Int,
    public val length: Int,
    public val blockWalk: Int,
    public val blockRange: Boolean,
    public val active: Int,
    public val hillSkew: Int,
    public val shareLight: Boolean,
    public val occlude: Boolean,
    public val anim: Int,
    public val wallWidth: Int,
    public val ambient: Int,
    public val contrast: Int,
    public val op: Array<String?>,
    public val recolS: ShortArray,
    public val recolD: ShortArray,
    public val retexS: ShortArray,
    public val retexD: ShortArray,
    public val category: Int,
    public val mirror: Boolean,
    public val shadow: Boolean,
    public val resizeX: Int,
    public val resizeY: Int,
    public val resizeZ: Int,
    public val mapscene: Int,
    public val forceApproachFlags: Int,
    public val offsetX: Int,
    public val offsetY: Int,
    public val offsetZ: Int,
    public val forceDecor: Boolean,
    public val breakRouteFinding: Boolean,
    public val raiseObject: Int,
    public val multiVarBit: Int,
    public val multiVarp: Int,
    public val multiLocDefault: Int,
    public val multiLoc: ShortArray,
    public val bgsoundSound: Int,
    public val bgsoundRange: Int,
    public val bgsoundSize: Int,
    public val bgsoundMinDelay: Int,
    public val bgsoundMaxDelay: Int,
    public val bgsoundRandomSounds: ShortArray,
    public val treeSkew: Int,
    public val mapIcon: Int,
    public val randomAnimFrame: Boolean,
    public val fixLocAnimAfterLocChange: Boolean,
    public val paramMap: ParamMap?,
    public val contentType: Int,
    internalId: Int,
    internalName: String,
) : LocType(internalId, internalName) {
    public fun <T : Any> param(type: ParamType<T>): T {
        val params = paramMap
        if (params == null) {
            return type.typedDefault
                ?: error("Param `$type` does not have a default value. Use `paramOrNull` instead.")
        }
        val value = params[type]
        if (value != null) {
            return value
        }
        return type.typedDefault ?: error("LocType does not have no-default param `$type` defined.")
    }

    public fun <T : Any> paramOrNull(type: ParamType<T>): T? = paramMap?.get(type)

    public fun toHashedType(): HashedLocType =
        HashedLocType(
            internalId = internalId,
            internalName = internalName,
            startHash = computeIdentityHash(),
        )

    public fun computeIdentityHash(): Long {
        var result = name.hashCode().toLong()
        result = 61 * result + shapes.contentHashCode()
        result = 61 * result + width
        result = 61 * result + length
        result = 61 * result + blockWalk
        result = 61 * result + active
        result = 61 * result + op.contentHashCode()
        result = 61 * result + forceApproachFlags
        result = 61 * result + breakRouteFinding.hashCode()
        result = 61 * result + multiVarBit
        result = 61 * result + multiVarp
        result = 61 * result + multiLocDefault
        result = 61 * result + multiLoc.contentHashCode()
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedLocType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "internalHash=${computeIdentityHash()}, " +
            "contentType=$contentType, " +
            "models=${models.contentToString()}, " +
            "shapes=${shapes.contentToString()}, " +
            "name='$name', " +
            "desc='$desc', " +
            "width=$width, " +
            "length=$length, " +
            "blockWalk=$blockWalk, " +
            "blockRange=$blockRange, " +
            "active=$active, " +
            "hillSkew=$hillSkew, " +
            "shareLight=$shareLight, " +
            "occlude=$occlude, " +
            "anim=$anim, " +
            "wallWidth=$wallWidth, " +
            "ambient=$ambient, " +
            "contrast=$contrast, " +
            "ops=${op.contentToString()}, " +
            "recolS=${recolS.contentToString()}, " +
            "recolD=${recolD.contentToString()}, " +
            "retexS=${retexS.contentToString()}, " +
            "retexD=${retexD.contentToString()}, " +
            "category=$category, " +
            "mirror=$mirror, " +
            "shadow=$shadow, " +
            "resizeX=$resizeX, " +
            "resizeY=$resizeY, " +
            "resizeZ=$resizeZ, " +
            "mapscene=$mapscene, " +
            "forceApproachFlags=$forceApproachFlags, " +
            "offsetX=$offsetX, " +
            "offsetY=$offsetY, " +
            "offsetZ=$offsetZ, " +
            "forceDecor=$forceDecor, " +
            "breakRouteFinding=$breakRouteFinding, " +
            "raiseObject=$raiseObject, " +
            "multiVarBit=$multiVarBit, " +
            "multiVarp=$multiVarp, " +
            "multiLocDefault=$multiLocDefault, " +
            "multiLoc=${multiLoc.contentToString()}, " +
            "bgsoundSound=$bgsoundSound, " +
            "bgsoundRange=$bgsoundRange, " +
            "bgsoundSize=$bgsoundSize, " +
            "bgsoundMinDelay=$bgsoundMinDelay, " +
            "bgsoundMaxDelay=$bgsoundMaxDelay, " +
            "bgsoundRandomSounds=${bgsoundRandomSounds.contentToString()}, " +
            "treeSkew=$treeSkew, " +
            "mapIcon=$mapIcon, " +
            "randomAnimFrame=$randomAnimFrame, " +
            "fixLocAnimAfterLocChange=$fixLocAnimAfterLocChange, " +
            "params=$paramMap" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedLocType) return false

        if (!models.contentEquals(other.models)) return false
        if (!shapes.contentEquals(other.shapes)) return false
        if (name != other.name) return false
        if (desc != other.desc) return false
        if (width != other.width) return false
        if (length != other.length) return false
        if (blockWalk != other.blockWalk) return false
        if (blockRange != other.blockRange) return false
        if (active != other.active) return false
        if (hillSkew != other.hillSkew) return false
        if (shareLight != other.shareLight) return false
        if (occlude != other.occlude) return false
        if (anim != other.anim) return false
        if (wallWidth != other.wallWidth) return false
        if (ambient != other.ambient) return false
        if (contrast != other.contrast) return false
        if (!op.contentEquals(other.op)) return false
        if (!recolS.contentEquals(other.recolS)) return false
        if (!recolD.contentEquals(other.recolD)) return false
        if (!retexS.contentEquals(other.retexS)) return false
        if (!retexD.contentEquals(other.retexD)) return false
        if (category != other.category) return false
        if (mirror != other.mirror) return false
        if (shadow != other.shadow) return false
        if (resizeX != other.resizeX) return false
        if (resizeY != other.resizeY) return false
        if (resizeZ != other.resizeZ) return false
        if (mapscene != other.mapscene) return false
        if (forceApproachFlags != other.forceApproachFlags) return false
        if (offsetX != other.offsetX) return false
        if (offsetY != other.offsetY) return false
        if (offsetZ != other.offsetZ) return false
        if (forceDecor != other.forceDecor) return false
        if (breakRouteFinding != other.breakRouteFinding) return false
        if (raiseObject != other.raiseObject) return false
        if (multiVarBit != other.multiVarBit) return false
        if (multiVarp != other.multiVarp) return false
        if (multiLocDefault != other.multiLocDefault) return false
        if (!multiLoc.contentEquals(other.multiLoc)) return false
        if (bgsoundSound != other.bgsoundSound) return false
        if (bgsoundRange != other.bgsoundRange) return false
        if (bgsoundSize != other.bgsoundSize) return false
        if (bgsoundMinDelay != other.bgsoundMinDelay) return false
        if (bgsoundMaxDelay != other.bgsoundMaxDelay) return false
        if (!bgsoundRandomSounds.contentEquals(other.bgsoundRandomSounds)) return false
        if (treeSkew != other.treeSkew) return false
        if (mapIcon != other.mapIcon) return false
        if (randomAnimFrame != other.randomAnimFrame) return false
        if (fixLocAnimAfterLocChange != other.fixLocAnimAfterLocChange) return false
        if (paramMap != other.paramMap) return false
        if (contentType != other.contentType) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = models.contentHashCode()
        result = 31 * result + shapes.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + desc.hashCode()
        result = 31 * result + width
        result = 31 * result + length
        result = 31 * result + blockWalk
        result = 31 * result + blockRange.hashCode()
        result = 31 * result + active
        result = 31 * result + hillSkew
        result = 31 * result + shareLight.hashCode()
        result = 31 * result + occlude.hashCode()
        result = 31 * result + anim
        result = 31 * result + wallWidth
        result = 31 * result + ambient
        result = 31 * result + contrast
        result = 31 * result + op.contentHashCode()
        result = 31 * result + recolS.contentHashCode()
        result = 31 * result + recolD.contentHashCode()
        result = 31 * result + retexS.contentHashCode()
        result = 31 * result + retexD.contentHashCode()
        result = 31 * result + category
        result = 31 * result + mirror.hashCode()
        result = 31 * result + shadow.hashCode()
        result = 31 * result + resizeX
        result = 31 * result + resizeY
        result = 31 * result + resizeZ
        result = 31 * result + mapscene
        result = 31 * result + forceApproachFlags
        result = 31 * result + offsetX
        result = 31 * result + offsetY
        result = 31 * result + offsetZ
        result = 31 * result + forceDecor.hashCode()
        result = 31 * result + breakRouteFinding.hashCode()
        result = 31 * result + raiseObject
        result = 31 * result + multiVarBit
        result = 31 * result + multiVarp
        result = 31 * result + multiLocDefault
        result = 31 * result + multiLoc.contentHashCode()
        result = 31 * result + bgsoundSound
        result = 31 * result + bgsoundRange
        result = 31 * result + bgsoundSize
        result = 31 * result + bgsoundMinDelay
        result = 31 * result + bgsoundMaxDelay
        result = 31 * result + bgsoundRandomSounds.contentHashCode()
        result = 31 * result + treeSkew
        result = 31 * result + mapIcon
        result = 31 * result + randomAnimFrame.hashCode()
        result = 31 * result + fixLocAnimAfterLocChange.hashCode()
        result = 31 * result + (paramMap?.hashCode() ?: 0)
        result = 31 * result + contentType
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
