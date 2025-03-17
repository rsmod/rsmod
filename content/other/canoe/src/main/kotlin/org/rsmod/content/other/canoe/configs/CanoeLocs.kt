package org.rsmod.content.other.canoe.configs

import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

typealias canoe_locs = CanoeLocs

object CanoeLocs : LocReferences() {
    val station_lumbridge = find("canoe_station_multibase_lumbridge", 7518291836939615271)
    val station_champs_guild = find("canoe_station_multibase_champs_guild", 7518291836953461113)
    val station_barb_village = find("canoe_station_multibase_barb_village", 7518291836967306955)
    val station_edgeville = find("canoe_station_multibase_edgeville", 7518291836981152797)
    val station_ferox_enclave = find("canoe_station_multibase_ferox_enclave", 7518291957232309354)
    val ready_to_shape = find("canoe_station_ready_to_shape", 3482919538715667463)
    val ready_log = find("canoe_ready_log", 2191318268562489158)
    val ready_dugout = find("canoe_ready_dugout", 2921309672170420703)
    val ready_stable_dugout = find("canoe_ready_stable_dugout", 2921309672170420704)
    val ready_waka = find("canoe_ready_waka", 2921309672170420705)
    val floating_log = find("canoe_floating_log", 2907545460324258188)
    val floating_dugout = find("canoe_floating_dugout", 2907545460324258189)
    val floating_stable_dugout = find("canoe_floating_stable_dugout", 2907545460324258190)
    val floating_waka = find("canoe_floating_waka", 2907545460324258191)
    val sinking_log = find("canoe_sinking_log", 6110366560596651993)
    val sinking_dugout = find("canoe_sinking_dugout", 6110366560596651994)
    val sinking_stable_dugout = find("canoe_sinking_stable_dugout", 6110366560596651995)
    val sinking_waka = find("canoe_sinking_waka", 6110366560596651996)
}

object CanoeLocEditor : LocEditor() {
    init {
        edit("canoe_ready_log") {
            param[params.skill_xp] = 30
            param[params.levelrequire] = 12
            param[params.next_loc_stage] = canoe_locs.sinking_log
        }

        edit("canoe_ready_dugout") {
            param[params.skill_xp] = 60
            param[params.levelrequire] = 27
            param[params.next_loc_stage] = canoe_locs.sinking_dugout
        }

        edit("canoe_ready_stable_dugout") {
            param[params.skill_xp] = 90
            param[params.levelrequire] = 42
            param[params.next_loc_stage] = canoe_locs.sinking_stable_dugout
        }

        edit("canoe_ready_waka") {
            param[params.skill_xp] = 150
            param[params.levelrequire] = 57
            param[params.next_loc_stage] = canoe_locs.sinking_waka
        }
    }
}
