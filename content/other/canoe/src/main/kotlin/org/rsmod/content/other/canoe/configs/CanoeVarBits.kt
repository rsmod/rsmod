package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias canoe_varbits = CanoeVarBits

object CanoeVarBits : VarBitReferences() {
    val current_station = find("canoe_current_station", 419071525)
    val lumbridge_state = find("canoe_state_lumbridge", 417418486)
    val champs_guild_state = find("canoe_state_champs_guild", 417675723)
    val barb_village_state = find("canoe_state_barb_village", 417932960)
    val edgeville_state = find("canoe_state_edgeville", 418190197)
    val ferox_enclave_state = find("canoe_state_ferox_enclave", 2389429414)

    val canoe_type = find("canoe_type", 418326410)
    val canoe_type_confirmed = find("canoe_type_confirmed", 418583220)

    val disable_wild_pond_warning = find("disable_canoe_wild_pond_warning", 3175066958)
}
