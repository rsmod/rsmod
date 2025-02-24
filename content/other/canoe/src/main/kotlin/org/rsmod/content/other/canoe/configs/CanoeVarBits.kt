package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias canoe_varbits = CanoeVarBits

object CanoeVarBits : VarBitReferences() {
    val current_station = find("canoe_current_station", 34776280224821)
    val lumbridge_state = find("canoe_state_lumbridge", 34725604382582)
    val champs_guild_state = find("canoe_state_champs_guild", 34725604412839)
    val barb_village_state = find("canoe_state_barb_village", 34725604443096)
    val edgeville_state = find("canoe_state_edgeville", 34725604473353)
    val ferox_enclave_state = find("canoe_state_ferox_enclave", 141269738569818)

    val canoe_type = find("canoe_type", 34776280160646)
    val canoe_type_confirmed = find("canoe_type_confirmed", 34776280190476)

    val disable_wild_pond_warning = find("disable_canoe_wild_pond_warning", 86297499186395)
}
