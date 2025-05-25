package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias canoe_varbits = CanoeVarBits

object CanoeVarBits : VarBitReferences() {
    val current_station = find("canoe_startfrom", 34776280224821)
    val lumbridge_state = find("canoestation_state_lumbridge", 34724759786281)
    val champs_guild_state = find("canoestation_state_championsguild", 34724759816538)
    val barb_village_state = find("canoestation_state_barbarianvillage", 34724759846795)
    val edgeville_state = find("canoestation_state_edgeville", 34724759877052)
    val ferox_enclave_state = find("canoestation_state_sanctuary", 141268893973517)

    val canoe_type = find("canoe_type", 34776280160646)
    val canoe_avoid_if = find("canoe_avoid_if", 34776280190476)

    val disable_wild_pond_warning = find("wildy_canoe_warning", 86297499186395)
}
