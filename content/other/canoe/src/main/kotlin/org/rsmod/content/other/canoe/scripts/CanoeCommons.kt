package org.rsmod.content.other.canoe.scripts

import org.rsmod.api.config.objParam
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.player.stat.woodcuttingLvl
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.enumVarBitOrNull
import org.rsmod.api.player.vars.typeCoordVarp
import org.rsmod.api.utils.vars.VarEnumDelegate
import org.rsmod.content.other.canoe.configs.canoe_locs
import org.rsmod.content.other.canoe.configs.canoe_varbits
import org.rsmod.content.other.canoe.configs.canoe_varps
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.enums.find
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.varbit.VarBitType

/* Canoe station helpers */
var ProtectedAccess.stationCoords by typeCoordVarp(canoe_varps.station_coords)
var ProtectedAccess.canoeStation by enumVarBitOrNull<Station>(canoe_varbits.current_station)

internal operator fun ProtectedAccess.set(station: Station, state: StationState) {
    vars[station.stateVarBit] = state.varValue
}

internal fun ProtectedAccess.clearStation(station: Station) {
    vars[station.stateVarBit] = 0
}

internal fun ProtectedAccess.clearCanoeVars() {
    canoeType = null

    val station = canoeStation
    if (station != null) {
        clearStation(station)
    }
}

internal fun ProtectedAccess.resolveStation(): Station {
    return checkNotNull(canoeStation) {
        "Expected valid `canoeStation` var: varValue=${vars[canoe_varbits.current_station]}"
    }
}

enum class Station(override val varValue: Int, val stateVarBit: VarBitType) : VarEnumDelegate {
    Lumbridge(1, canoe_varbits.lumbridge_state),
    ChampionsGuild(2, canoe_varbits.champs_guild_state),
    BarbarianVillage(3, canoe_varbits.barb_village_state),
    Edgeville(4, canoe_varbits.edgeville_state),
    FeroxEnclave(5, canoe_varbits.ferox_enclave_state),
}

enum class StationState(val varValue: Int) {
    StationFullyGrown(0),
    Log(1),
    Dugout(2),
    StableDugout(3),
    Waka(4),
    PushingLog(5),
    PushingDugout(6),
    PushingStableDugout(7),
    PushingWaka(8),
    StationFalling(9),
    StationReadyToShape(10),
    FloatingLog(11),
    FloatingDugout(12),
    FloatingStableDugout(13),
    FloatingWaka(14),
}

/* Canoe helpers */
var ProtectedAccess.confirmedCanoeType by boolVarBit(canoe_varbits.canoe_avoid_if)
var ProtectedAccess.canoeType by enumVarBitOrNull<Canoe>(canoe_varbits.canoe_type)

internal operator fun ProtectedAccess.set(station: Station, canoe: Canoe, state: CanoeState) {
    val stationState = canoe.toStationState(state)
    vars[station.stateVarBit] = stationState.varValue
}

private fun Canoe.toStationState(state: CanoeState): StationState =
    when (state) {
        CanoeState.Ready -> readyState
        CanoeState.Pushing -> pushingState
        CanoeState.Floating -> floatingState
    }

enum class Canoe(
    override val varValue: Int,
    val loc: LocType,
    val readyState: StationState,
    val pushingState: StationState,
    val floatingState: StationState,
) : VarEnumDelegate {
    Log(
        varValue = 1,
        loc = canoe_locs.ready_log,
        readyState = StationState.Log,
        pushingState = StationState.PushingLog,
        floatingState = StationState.FloatingLog,
    ),
    Dugout(
        varValue = 2,
        loc = canoe_locs.ready_dugout,
        readyState = StationState.Dugout,
        pushingState = StationState.PushingDugout,
        floatingState = StationState.FloatingDugout,
    ),
    StableDugout(
        varValue = 3,
        loc = canoe_locs.ready_stable_dugout,
        readyState = StationState.StableDugout,
        pushingState = StationState.PushingStableDugout,
        floatingState = StationState.FloatingStableDugout,
    ),
    Waka(
        varValue = 4,
        loc = canoe_locs.ready_waka,
        readyState = StationState.Waka,
        pushingState = StationState.PushingWaka,
        floatingState = StationState.FloatingWaka,
    ),
}

enum class CanoeState {
    Ready,
    Pushing,
    Floating,
}

/* General-purpose helpers */
internal data class AxeSuccessRate(val low: Int, val high: Int)

internal fun axeSuccessRates(
    axe: InvObj,
    ratesEnum: EnumType<ObjType, Int>,
    enumTypes: EnumTypeList,
): AxeSuccessRate {
    val axes = enumTypes[ratesEnum]
    val rates = enumTypes[axes].find(axe)
    val low = rates shr 16
    val high = rates and 0xFFFF
    return AxeSuccessRate(low, high)
}

internal val UnpackedObjType.axeWoodcuttingReq: Int by objParam(params.levelrequire)

internal fun findAxe(player: Player, objTypes: ObjTypeList): InvObj? {
    val worn = player.wornAxe(objTypes)
    val carried = player.carriedAxe(objTypes)
    if (worn != null && carried != null) {
        if (objTypes[worn].axeWoodcuttingReq >= objTypes[carried].axeWoodcuttingReq) {
            return worn
        }
        return carried
    }
    return worn ?: carried
}

private fun Player.wornAxe(objTypes: ObjTypeList): InvObj? {
    val righthand = righthand ?: return null
    return righthand.takeIf { objTypes[it].isUsableAxe(woodcuttingLvl) }
}

private fun Player.carriedAxe(objTypes: ObjTypeList): InvObj? {
    return inv.filterNotNull { objTypes[it].isUsableAxe(woodcuttingLvl) }
        .maxByOrNull { objTypes[it].axeWoodcuttingReq }
}

private fun UnpackedObjType.isUsableAxe(woodcuttingLevel: Int): Boolean =
    isContentType(content.woodcutting_axe) && woodcuttingLevel >= axeWoodcuttingReq

internal val UnpackedObjType.axeWoodcuttingAnim: SeqType by objParam(params.skill_anim)
