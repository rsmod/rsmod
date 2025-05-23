package org.rsmod.game.type.obj

import kotlin.contracts.contract
import org.rsmod.game.interact.HeldOp
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType
import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.util.ParamMap
import org.rsmod.game.type.util.resolve

public sealed class ObjType : CacheType()

public data class HashedObjType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, ObjType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "ObjType(internalName='$internalName', internalId=$internalId, supposedHash=$supposedHash)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedObjType) return false
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

public data class UnpackedObjType(
    public val name: String,
    public val desc: String,
    public val model: Int,
    public val zoom2d: Int,
    public val xan2d: Int,
    public val yan2d: Int,
    public val xof2d: Int,
    public val yof2d: Int,
    public val stackable: Boolean,
    public val cost: Int,
    public val wearpos1: Int,
    public val wearpos2: Int,
    public val members: Boolean,
    public val manwear: Int,
    public val manwearOff: Int,
    public val manwear2: Int,
    public val womanwear: Int,
    public val womanwearOff: Int,
    public val womanwear2: Int,
    public val wearpos3: Int,
    public val op: Array<String?>,
    public val iop: Array<String?>,
    public val recolS: ShortArray,
    public val recolD: ShortArray,
    public val retexS: ShortArray,
    public val retexD: ShortArray,
    public val shiftclickiop: Int,
    public val isubop1: Array<String>?,
    public val isubop2: Array<String>?,
    public val isubop3: Array<String>?,
    public val isubop4: Array<String>?,
    public val isubop5: Array<String>?,
    public val stockmarket: Boolean,
    public val weight: Int,
    public val manwear3: Int,
    public val womanwear3: Int,
    public val manhead: Int,
    public val womanhead: Int,
    public val manhead2: Int,
    public val womanhead2: Int,
    public val category: Int,
    public val zan2d: Int,
    public val certlink: Int,
    public val certtemplate: Int,
    public val countObj: IntArray,
    public val countCount: IntArray,
    public val resizeX: Int,
    public val resizeY: Int,
    public val resizeZ: Int,
    public val ambient: Int,
    public val contrast: Int,
    public val team: Int,
    public val boughtlink: Int,
    public val boughttemplate: Int,
    public val placeholderlink: Int,
    public val placeholdertemplate: Int,
    public val paramMap: ParamMap?,
    public val generateCertificate: Boolean,
    public val generatePlaceholder: Boolean,
    public val objvar: IntArray,
    public val playerCost: Int,
    public val playerCostDerived: Int,
    public val playerCostDerivedConst: Int,
    public val stockMarketBuyLimit: Int,
    public val stockMarketRecalcUsers: Int,
    public val tradeable: Boolean,
    public val respawnRate: Int,
    public val dummyitem: Int,
    public val contentGroup: Int,
    public val weaponCategory: Int,
    public val transformlink: Int,
    public val transformtemplate: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : ObjType() {
    private val identityHash by lazy { computeIdentityHash() }

    public val lowercaseName: String
        get() = name.lowercase()

    public val isStackable: Boolean
        get() = (stackable || certtemplate > 0) && objvar.isEmpty()

    public val hasPlaceholder: Boolean
        get() = placeholderlink > 0 && placeholdertemplate == 0

    public val isPlaceholder: Boolean
        get() = placeholdertemplate != 0

    public val canCert: Boolean
        get() = !stackable && certlink > 0 && certtemplate == 0 && objvar.isEmpty()

    public val isCert: Boolean
        get() = certtemplate != 0

    public val hasTransformation: Boolean
        get() = transformlink > 0 && transformtemplate == 0

    public val isTransformation: Boolean
        get() = transformtemplate != 0

    public val isDummyItem: Boolean
        get() = dummyitem != ObjTypeBuilder.DEFAULT_DUMMYITEM

    public val resolvedDummyitem: Dummyitem?
        get() = Dummyitem[dummyitem]

    public val highAlch: Int
        get() = cost * 60 / 100

    public val lowAlch: Int
        get() = cost * 40 / 100

    public val isEquipable: Boolean
        get() = wearpos1 != -1 && (iop[1] == "Wield" || iop[1] == "Wear")

    public fun <T : Any> param(type: ParamType<T>): T = paramMap.resolve(type)

    public fun <T : Any> paramOrNull(type: ParamType<T>): T? = paramMap?.get(type)

    public fun <T : Any> hasParam(type: ParamType<T>): Boolean = paramOrNull(type) != null

    public fun hasOp(interactionOp: InteractionOp): Boolean {
        val text = op.getOrNull(interactionOp.slot - 1) ?: return false
        val invalid = text.isBlank() || text.equals("hidden", ignoreCase = true)
        return !invalid
    }

    public fun hasInvOp(slot: Int): Boolean {
        val text = iop.getOrNull(slot - 1) ?: return false
        return text.isNotBlank()
    }

    public fun hasInvOp(invOp: HeldOp): Boolean {
        return hasInvOp(invOp.slot)
    }

    public fun isContentType(content: ContentGroupType): Boolean {
        return contentGroup == content.id
    }

    public fun isCategoryType(cat: CategoryType): Boolean {
        return category == cat.internalId
    }

    public fun toHashedType(): HashedObjType =
        HashedObjType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = (internalId?.hashCode()?.toLong() ?: 0)
        result = 61 * result + name.hashCode().toLong()
        result = 61 * result + stackable.hashCode()
        result = 61 * result + cost
        result = 61 * result + wearpos1
        result = 61 * result + wearpos2
        result = 61 * result + members.hashCode()
        result = 61 * result + wearpos3
        result = 61 * result + op.contentHashCode()
        result = 61 * result + iop.contentHashCode()
        result = 61 * result + shiftclickiop
        result = 61 * result + (isubop1?.contentHashCode() ?: 0)
        result = 61 * result + (isubop2?.contentHashCode() ?: 0)
        result = 61 * result + (isubop3?.contentHashCode() ?: 0)
        result = 61 * result + (isubop4?.contentHashCode() ?: 0)
        result = 61 * result + (isubop5?.contentHashCode() ?: 0)
        result = 61 * result + boughtlink
        result = 61 * result + boughttemplate
        result = 61 * result + placeholderlink
        result = 61 * result + placeholdertemplate
        result = 61 * result + (paramMap?.hashCode() ?: 0)
        result = 61 * result + objvar.contentHashCode()
        result = 61 * result + tradeable.hashCode()
        result = 61 * result + respawnRate
        result = 61 * result + dummyitem
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedObjType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "internalHash=${computeIdentityHash()}, " +
            "contentGroup=$contentGroup, " +
            "name='$name', " +
            "desc='$desc', " +
            "weaponCategory=$weaponCategory, " +
            "model=$model, " +
            "zoom2d=$zoom2d, " +
            "xan2d=$xan2d, " +
            "yan2d=$yan2d, " +
            "xof2d=$xof2d, " +
            "yof2d=$yof2d, " +
            "stackable=$stackable, " +
            "cost=$cost, " +
            "wearpos1=$wearpos1, " +
            "wearpos2=$wearpos2, " +
            "members=$members, " +
            "manwear=$manwear, " +
            "manwearOff=$manwearOff, " +
            "manwear2=$manwear2, " +
            "womanwear=$womanwear, " +
            "womanwearOff=$womanwearOff, " +
            "womanwear2=$womanwear2, " +
            "wearpos3=$wearpos3, " +
            "op=${op.contentToString()}, " +
            "iop=${iop.contentToString()}, " +
            "recolS=${recolS.contentToString()}, " +
            "recolD=${recolD.contentToString()}, " +
            "retexS=${retexS.contentToString()}, " +
            "retexD=${retexD.contentToString()}, " +
            "shiftclickiop=$shiftclickiop, " +
            "isubop1=${isubop1?.contentToString()}, " +
            "isubop2=${isubop2?.contentToString()}, " +
            "isubop3=${isubop3?.contentToString()}, " +
            "isubop4=${isubop4?.contentToString()}, " +
            "isubop5=${isubop5?.contentToString()}, " +
            "stockmarket=$stockmarket, " +
            "weight=$weight, " +
            "manwear3=$manwear3, " +
            "womanwear3=$womanwear3, " +
            "manhead=$manhead, " +
            "womanhead=$womanhead, " +
            "manhead2=$manhead2, " +
            "womanhead2=$womanhead2, " +
            "category=$category, " +
            "zan2d=$zan2d, " +
            "certlink=$certlink, " +
            "certtemplate=$certtemplate, " +
            "countObj=${countObj.contentToString()}, " +
            "countCount=${countCount.contentToString()}, " +
            "resizeX=$resizeX, " +
            "resizeY=$resizeY, " +
            "resizeZ=$resizeZ, " +
            "ambient=$ambient, " +
            "contrast=$contrast, " +
            "team=$team, " +
            "boughtlink=$boughtlink, " +
            "boughttemplate=$boughttemplate, " +
            "placeholderlink=$placeholderlink, " +
            "placeholdertemplate=$placeholdertemplate, " +
            "transformlink=$transformlink, " +
            "transformtemplate=$transformtemplate, " +
            "params=$paramMap, " +
            "generateCertificate=$generateCertificate, " +
            "generatePlaceholder=$generatePlaceholder, " +
            "varobjs=${objvar.contentToString()}, " +
            "playerCost=$playerCost, " +
            "playerCostDerived=$playerCostDerived, " +
            "playerCostDerivedConst=$playerCostDerivedConst, " +
            "stockMarketBuyLimit=$stockMarketBuyLimit, " +
            "stockMarketRecalcUsers=$stockMarketRecalcUsers, " +
            "tradeable=$tradeable, " +
            "respawnRate=$respawnRate, " +
            "dummyitem=$dummyitem" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedObjType) return false

        if (name != other.name) return false
        if (desc != other.desc) return false
        if (weaponCategory != other.weaponCategory) return false
        if (model != other.model) return false
        if (zoom2d != other.zoom2d) return false
        if (xan2d != other.xan2d) return false
        if (yan2d != other.yan2d) return false
        if (xof2d != other.xof2d) return false
        if (yof2d != other.yof2d) return false
        if (stackable != other.stackable) return false
        if (cost != other.cost) return false
        if (wearpos1 != other.wearpos1) return false
        if (wearpos2 != other.wearpos2) return false
        if (members != other.members) return false
        if (manwear != other.manwear) return false
        if (manwearOff != other.manwearOff) return false
        if (manwear2 != other.manwear2) return false
        if (womanwear != other.womanwear) return false
        if (womanwearOff != other.womanwearOff) return false
        if (womanwear2 != other.womanwear2) return false
        if (wearpos3 != other.wearpos3) return false
        if (!op.contentEquals(other.op)) return false
        if (!iop.contentEquals(other.iop)) return false
        if (!recolS.contentEquals(other.recolS)) return false
        if (!recolD.contentEquals(other.recolD)) return false
        if (!retexS.contentEquals(other.retexS)) return false
        if (!retexD.contentEquals(other.retexD)) return false
        if (shiftclickiop != other.shiftclickiop) return false
        if (!isubop1.contentEquals(other.isubop1)) return false
        if (!isubop2.contentEquals(other.isubop2)) return false
        if (!isubop3.contentEquals(other.isubop3)) return false
        if (!isubop4.contentEquals(other.isubop4)) return false
        if (!isubop5.contentEquals(other.isubop5)) return false
        if (stockmarket != other.stockmarket) return false
        if (weight != other.weight) return false
        if (manwear3 != other.manwear3) return false
        if (womanwear3 != other.womanwear3) return false
        if (manhead != other.manhead) return false
        if (womanhead != other.womanhead) return false
        if (manhead2 != other.manhead2) return false
        if (womanhead2 != other.womanhead2) return false
        if (category != other.category) return false
        if (zan2d != other.zan2d) return false
        if (certlink != other.certlink) return false
        if (certtemplate != other.certtemplate) return false
        if (!countObj.contentEquals(other.countObj)) return false
        if (!countCount.contentEquals(other.countCount)) return false
        if (resizeX != other.resizeX) return false
        if (resizeY != other.resizeY) return false
        if (resizeZ != other.resizeZ) return false
        if (ambient != other.ambient) return false
        if (contrast != other.contrast) return false
        if (team != other.team) return false
        if (boughtlink != other.boughtlink) return false
        if (boughttemplate != other.boughttemplate) return false
        if (placeholderlink != other.placeholderlink) return false
        if (placeholdertemplate != other.placeholdertemplate) return false
        if (paramMap != other.paramMap) return false
        if (generateCertificate != other.generateCertificate) return false
        if (generatePlaceholder != other.generatePlaceholder) return false
        if (!objvar.contentEquals(other.objvar)) return false
        if (playerCost != other.playerCost) return false
        if (playerCostDerived != other.playerCostDerived) return false
        if (playerCostDerivedConst != other.playerCostDerivedConst) return false
        if (stockMarketBuyLimit != other.stockMarketBuyLimit) return false
        if (stockMarketRecalcUsers != other.stockMarketRecalcUsers) return false
        if (tradeable != other.tradeable) return false
        if (respawnRate != other.respawnRate) return false
        if (dummyitem != other.dummyitem) return false
        if (contentGroup != other.contentGroup) return false
        if (transformlink != other.transformlink) return false
        if (transformtemplate != other.transformtemplate) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + desc.hashCode()
        result = 31 * result + weaponCategory
        result = 31 * result + model
        result = 31 * result + zoom2d
        result = 31 * result + xan2d
        result = 31 * result + yan2d
        result = 31 * result + xof2d
        result = 31 * result + yof2d
        result = 31 * result + stackable.hashCode()
        result = 31 * result + cost
        result = 31 * result + wearpos1
        result = 31 * result + wearpos2
        result = 31 * result + members.hashCode()
        result = 31 * result + manwear
        result = 31 * result + manwearOff
        result = 31 * result + manwear2
        result = 31 * result + womanwear
        result = 31 * result + womanwearOff
        result = 31 * result + womanwear2
        result = 31 * result + wearpos3
        result = 31 * result + op.contentHashCode()
        result = 31 * result + iop.contentHashCode()
        result = 31 * result + recolS.contentHashCode()
        result = 31 * result + recolD.contentHashCode()
        result = 31 * result + retexS.contentHashCode()
        result = 31 * result + retexD.contentHashCode()
        result = 31 * result + shiftclickiop
        result = 31 * result + (isubop1?.contentHashCode() ?: 0)
        result = 31 * result + (isubop2?.contentHashCode() ?: 0)
        result = 31 * result + (isubop3?.contentHashCode() ?: 0)
        result = 31 * result + (isubop4?.contentHashCode() ?: 0)
        result = 31 * result + (isubop5?.contentHashCode() ?: 0)
        result = 31 * result + stockmarket.hashCode()
        result = 31 * result + weight
        result = 31 * result + manwear3
        result = 31 * result + womanwear3
        result = 31 * result + manhead
        result = 31 * result + womanhead
        result = 31 * result + manhead2
        result = 31 * result + womanhead2
        result = 31 * result + category
        result = 31 * result + zan2d
        result = 31 * result + certlink
        result = 31 * result + certtemplate
        result = 31 * result + countObj.contentHashCode()
        result = 31 * result + countCount.contentHashCode()
        result = 31 * result + resizeX
        result = 31 * result + resizeY
        result = 31 * result + resizeZ
        result = 31 * result + ambient
        result = 31 * result + contrast
        result = 31 * result + team
        result = 31 * result + boughtlink
        result = 31 * result + boughttemplate
        result = 31 * result + placeholderlink
        result = 31 * result + placeholdertemplate
        result = 31 * result + (paramMap?.hashCode() ?: 0)
        result = 31 * result + generateCertificate.hashCode()
        result = 31 * result + generatePlaceholder.hashCode()
        result = 31 * result + objvar.contentHashCode()
        result = 31 * result + playerCost
        result = 31 * result + playerCostDerived
        result = 31 * result + playerCostDerivedConst
        result = 31 * result + stockMarketBuyLimit
        result = 31 * result + stockMarketRecalcUsers
        result = 31 * result + tradeable.hashCode()
        result = 31 * result + respawnRate
        result = 31 * result + dummyitem
        result = 31 * result + contentGroup
        result = 31 * result + transformlink
        result = 31 * result + transformtemplate
        return result
    }
}

public fun ObjType.isAssociatedWith(obj: InvObj?): Boolean {
    contract { returns(true) implies (obj != null) }
    return obj != null && obj.id == id
}

public fun ObjType?.isType(other: ObjType): Boolean {
    contract { returns(true) implies (this@isType != null) }
    return this != null && this.id == other.id
}

public fun ObjType?.isAnyType(type1: ObjType, type2: ObjType): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null && (type1.id == id || type2.id == id)
}

public fun ObjType?.isAnyType(type1: ObjType, type2: ObjType, type3: ObjType): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null && (type1.id == id || type2.id == id || type3.id == id)
}

public fun ObjType?.isAnyType(
    type1: ObjType,
    type2: ObjType,
    type3: ObjType,
    type4: ObjType,
): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null && (type1.id == id || type2.id == id || type3.id == id || type4.id == id)
}

public fun ObjType?.isAnyType(type1: ObjType, type2: ObjType, vararg types: ObjType): Boolean {
    contract { returns(true) implies (this@isAnyType != null) }
    return this != null && (type1.id == id || type2.id == id || types.any { it.id == id })
}
