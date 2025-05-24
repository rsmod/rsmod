package org.rsmod.game.type.hunt

import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class HuntModeType : CacheType()

public data class HashedHuntModeType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, HuntModeType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "HuntType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedHuntModeType) return false
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

public data class UnpackedHuntModeType(
    public val type: HuntType,
    public val checkVis: HuntVis,
    public val checkNotTooStrong: HuntCheckNotTooStrong,
    public val checkNotCombat: Int,
    public val checkNotCombatSelf: Int,
    public val checkAfk: Boolean,
    public val checkNotBusy: Boolean,
    public val findKeepHunting: Boolean,
    public val findNewMode: NpcMode,
    public val nobodyNear: HuntNobodyNear,
    public val rate: Int,
    public val checkInvObj: HuntCondition.Inv?,
    public val checkInvParam: HuntCondition.Inv?,
    public val checkLoc: HuntCondition.Loc?,
    public val checkNpc: HuntCondition.Npc?,
    public val checkObj: HuntCondition.Obj?,
    public val checkVar1: HuntCondition.Var?,
    public val checkVar2: HuntCondition.Var?,
    public val checkVar3: HuntCondition.Var?,
    override var internalId: Int?,
    override var internalName: String?,
) : HuntModeType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedHuntModeType =
        HashedHuntModeType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = type.hashCode().toLong()
        result = 61 * result + checkNotCombat
        result = 61 * result + checkNotCombatSelf
        result = 61 * result + checkAfk.hashCode()
        result = 61 * result + checkNotBusy.hashCode()
        result = 61 * result + findKeepHunting.hashCode()
        result = 61 * result + type.hashCode()
        result = 61 * result + checkVis.hashCode()
        result = 61 * result + checkNotTooStrong.hashCode()
        result = 61 * result + findNewMode.hashCode()
        result = 61 * result + nobodyNear.hashCode()
        result = 61 * result + id
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedHuntModeType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "type=$type, " +
            "checkVis=$checkVis, " +
            "checkNotTooStrong=$checkNotTooStrong, " +
            "checkNotCombat=$checkNotCombat, " +
            "checkNotCombatSelf=$checkNotCombatSelf, " +
            "checkAfk=$checkAfk, " +
            "checkNotBusy=$checkNotBusy, " +
            "findKeepHunting=$findKeepHunting, " +
            "findNewMode=$findNewMode, " +
            "nobodyNear=$nobodyNear, " +
            "rate=$rate, " +
            "checkInvObj=$checkInvObj, " +
            "checkInvParam=$checkInvParam, " +
            "checkLoc=$checkLoc, " +
            "checkNpc=$checkNpc, " +
            "checkObj=$checkObj, " +
            "checkVar1=$checkVar1, " +
            "checkVar2=$checkVar2, " +
            "checkVar3=$checkVar3" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UnpackedHuntModeType
        if (internalId != other.internalId) return false
        if (checkNotCombat != other.checkNotCombat) return false
        if (checkNotCombatSelf != other.checkNotCombatSelf) return false
        if (checkAfk != other.checkAfk) return false
        if (checkNotBusy != other.checkNotBusy) return false
        if (findKeepHunting != other.findKeepHunting) return false
        if (rate != other.rate) return false
        if (checkInvObj != other.checkInvObj) return false
        if (checkInvParam != other.checkInvParam) return false
        if (checkLoc != other.checkLoc) return false
        if (checkNpc != other.checkNpc) return false
        if (checkObj != other.checkObj) return false
        if (checkVar1 != other.checkVar1) return false
        if (checkVar2 != other.checkVar2) return false
        if (checkVar3 != other.checkVar3) return false
        if (type != other.type) return false
        if (checkVis != other.checkVis) return false
        if (checkNotTooStrong != other.checkNotTooStrong) return false
        if (findNewMode != other.findNewMode) return false
        if (nobodyNear != other.nobodyNear) return false
        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + checkNotCombat
        result = 31 * result + checkNotCombatSelf
        result = 31 * result + checkAfk.hashCode()
        result = 31 * result + checkNotBusy.hashCode()
        result = 31 * result + findKeepHunting.hashCode()
        result = 31 * result + rate
        result = 31 * result + checkInvObj.hashCode()
        result = 31 * result + checkInvParam.hashCode()
        result = 31 * result + checkLoc.hashCode()
        result = 31 * result + checkNpc.hashCode()
        result = 31 * result + checkObj.hashCode()
        result = 31 * result + checkVar1.hashCode()
        result = 31 * result + checkVar2.hashCode()
        result = 31 * result + checkVar3.hashCode()
        result = 31 * result + checkVis.hashCode()
        result = 31 * result + checkNotTooStrong.hashCode()
        result = 31 * result + findNewMode.hashCode()
        result = 31 * result + nobodyNear.hashCode()
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
