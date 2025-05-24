package org.rsmod.game.type.hunt

import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder

@DslMarker private annotation class HuntModeBuilderDsl

@HuntModeBuilderDsl
public class HuntModeTypeBuilder(public var internal: String? = null) {
    public var type: HuntType? = null
    public var checkVis: HuntVis? = null
    public var checkNotTooStrong: HuntCheckNotTooStrong? = null
    public var checkNotCombat: Int? = null
    public var checkNotCombatSelf: Int? = null
    public var checkAfk: Boolean? = null
    public var checkNotBusy: Boolean? = null
    public var findKeepHunting: Boolean? = null
    public var findNewMode: NpcMode? = null
    public var nobodyNear: HuntNobodyNear? = null
    public var rate: Int? = null
    public var checkInvObj: HuntCondition.Inv? = null
    public var checkInvParam: HuntCondition.Inv? = null
    public var checkLoc: HuntCondition.Loc? = null
    public var checkNpc: HuntCondition.Npc? = null
    public var checkObj: HuntCondition.Obj? = null
    public var checkVar1: HuntCondition.Var? = null
    public var checkVar2: HuntCondition.Var? = null
    public var checkVar3: HuntCondition.Var? = null

    public fun build(id: Int): UnpackedHuntModeType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val type = type ?: DEFAULT_TYPE
        val checkVis = checkVis ?: DEFAULT_VIS
        val checkNotTooStrong = checkNotTooStrong ?: DEFAULT_NOTTOOSTRONG
        val checkNotCombat = checkNotCombat ?: DEFAULT_CHECKNOTCOMBAT
        val checkNotCombatSelf = checkNotCombatSelf ?: DEFAULT_CHECKNOTCOMBAT_SELF
        val checkAfk = checkAfk ?: DEFAULT_CHECK_AFK
        val checkNotBusy = checkNotBusy ?: false
        val findKeepHunting = findKeepHunting ?: false
        val findNewMode = findNewMode ?: DEFAULT_NEWMODE
        val nobodyNear = nobodyNear ?: DEFAULT_NOBODYNEAR
        val rate = rate ?: DEFAULT_RATE
        return UnpackedHuntModeType(
            type = type,
            checkVis = checkVis,
            checkNotTooStrong = checkNotTooStrong,
            checkNotCombat = checkNotCombat,
            checkNotCombatSelf = checkNotCombatSelf,
            checkAfk = checkAfk,
            checkNotBusy = checkNotBusy,
            findKeepHunting = findKeepHunting,
            findNewMode = findNewMode,
            nobodyNear = nobodyNear,
            rate = rate,
            checkInvObj = checkInvObj,
            checkInvParam = checkInvParam,
            checkLoc = checkLoc,
            checkNpc = checkNpc,
            checkObj = checkObj,
            checkVar1 = checkVar1,
            checkVar2 = checkVar2,
            checkVar3 = checkVar3,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object : MergeableCacheBuilder<UnpackedHuntModeType> {
        public const val DEFAULT_CHECK_AFK: Boolean = true
        public const val DEFAULT_RATE: Int = 1
        public const val DEFAULT_CHECKNOTCOMBAT: Int = -1
        public const val DEFAULT_CHECKNOTCOMBAT_SELF: Int = -1

        public val DEFAULT_TYPE: HuntType = HuntType.Off
        public val DEFAULT_VIS: HuntVis = HuntVis.Off
        public val DEFAULT_NOTTOOSTRONG: HuntCheckNotTooStrong = HuntCheckNotTooStrong.Off
        public val DEFAULT_NEWMODE: NpcMode = NpcMode.None
        public val DEFAULT_NOBODYNEAR: HuntNobodyNear = HuntNobodyNear.KeepHunting

        override fun merge(
            edit: UnpackedHuntModeType,
            base: UnpackedHuntModeType,
        ): UnpackedHuntModeType {
            val type = select(edit, base, DEFAULT_TYPE) { type }
            val checkVis = select(edit, base, DEFAULT_VIS) { checkVis }
            val checkNotTooStrong = select(edit, base, DEFAULT_NOTTOOSTRONG) { checkNotTooStrong }
            val checkNotCombat = select(edit, base, DEFAULT_CHECKNOTCOMBAT) { checkNotCombat }
            val checkNotCombatSelf =
                select(edit, base, DEFAULT_CHECKNOTCOMBAT_SELF) { checkNotCombatSelf }
            val checkAfk = select(edit, base, DEFAULT_CHECK_AFK) { checkAfk }
            val checkNotBusy = select(edit, base, default = false) { checkNotBusy }
            val findKeepHunting = select(edit, base, default = false) { findKeepHunting }
            val findNewMode = select(edit, base, DEFAULT_NEWMODE) { findNewMode }
            val nobodyNear = select(edit, base, DEFAULT_NOBODYNEAR) { nobodyNear }
            val rate = select(edit, base, DEFAULT_RATE) { rate }
            val checkInvObj = select(edit, base, default = null) { checkInvObj }
            val checkInvParam = select(edit, base, default = null) { checkInvParam }
            val checkLoc = select(edit, base, default = null) { checkLoc }
            val checkNpc = select(edit, base, default = null) { checkNpc }
            val checkObj = select(edit, base, default = null) { checkObj }
            val checkVar1 = select(edit, base, default = null) { checkVar1 }
            val checkVar2 = select(edit, base, default = null) { checkVar2 }
            val checkVar3 = select(edit, base, default = null) { checkVar3 }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedHuntModeType(
                type = type,
                checkVis = checkVis,
                checkNotTooStrong = checkNotTooStrong,
                checkNotCombat = checkNotCombat,
                checkNotCombatSelf = checkNotCombatSelf,
                checkAfk = checkAfk,
                checkNotBusy = checkNotBusy,
                findKeepHunting = findKeepHunting,
                findNewMode = findNewMode,
                nobodyNear = nobodyNear,
                rate = rate,
                checkInvObj = checkInvObj,
                checkInvParam = checkInvParam,
                checkLoc = checkLoc,
                checkNpc = checkNpc,
                checkObj = checkObj,
                checkVar1 = checkVar1,
                checkVar2 = checkVar2,
                checkVar3 = checkVar3,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
