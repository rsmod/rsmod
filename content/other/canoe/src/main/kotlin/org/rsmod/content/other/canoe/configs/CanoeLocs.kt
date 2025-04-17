package org.rsmod.content.other.canoe.configs

import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

typealias canoe_locs = CanoeLocs

object CanoeLocs : LocReferences() {
    val station_lumbridge = find("canoeing_canoestation_lumbridge", 7518291836939615271)
    val station_champs_guild = find("canoeing_canoestation_championsguild", 7518291836953461113)
    val station_barb_village = find("canoeing_canoestation_barbarianvillage", 7518291836967306955)
    val station_edgeville = find("canoeing_canoestation_edgeville", 7518291836981152797)
    val station_ferox_enclave = find("canoeing_canoestation_sanctuary", 7518291957232309354)
    val ready_to_shape = find("canoestation_fallen_tree", 3482919538715667463)
    val ready_log = find("canoestation_log", 2191318268562489158)
    val ready_dugout = find("canoestation_dugout", 2921309672170420703)
    val ready_stable_dugout = find("canoestation_stabledugout", 2921309672170420704)
    val ready_waka = find("canoestation_waka", 2921309672170420705)
    val floating_log = find("canoeing_log_canoeing_station_in_water", 2907545460324258188)
    val floating_dugout = find("canoeing_dugout_canoeing_station_in_water", 2907545460324258189)
    val floating_stable_dugout =
        find("canoeing_catamaran_canoeing_station_in_water", 2907545460324258190)
    val floating_waka = find("canoeing_waka_canoeing_station_in_water", 2907545460324258191)
    val sinking_log = find("canoeing_log_sinking", 6110366560596651993)
    val sinking_dugout = find("canoeing_dugout_sinking", 6110366560596651994)
    val sinking_stable_dugout = find("canoeing_catamaran_sinking", 6110366560596651995)
    val sinking_waka = find("canoeing_waka_sinking", 6110366560596651996)
}

object CanoeLocEditor : LocEditor() {
    init {
        edit("canoestation_log") {
            param[params.skill_xp] = 30
            param[params.levelrequire] = 12
            param[params.next_loc_stage] = canoe_locs.sinking_log
        }

        edit("canoestation_dugout") {
            param[params.skill_xp] = 60
            param[params.levelrequire] = 27
            param[params.next_loc_stage] = canoe_locs.sinking_dugout
        }

        edit("canoestation_stabledugout") {
            param[params.skill_xp] = 90
            param[params.levelrequire] = 42
            param[params.next_loc_stage] = canoe_locs.sinking_stable_dugout
        }

        edit("canoestation_waka") {
            param[params.skill_xp] = 150
            param[params.levelrequire] = 57
            param[params.next_loc_stage] = canoe_locs.sinking_waka
        }
    }
}
