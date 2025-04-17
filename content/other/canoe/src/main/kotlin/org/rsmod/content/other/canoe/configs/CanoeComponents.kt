package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias canoe_components = CanoeComponents

object CanoeComponents : ComponentReferences() {
    val shape_log = find("canoeing:log", 5540955978932720971)
    val shape_dugout = find("canoeing:dugout", 574740175170890111)
    val shape_stable_dugout = find("canoeing:stable_dugout", 5904149818566193745)
    val shape_waka = find("canoeing:waka", 558687581070453046)
    val shape_close = find("canoeing:close", 1179288482004399632)

    val destination_edgeville = find("canoe_map:edgeville", 7376914826480145382)
    val destination_lumbridge = find("canoe_map:lumbridge", 3916036672780179471)
    val destination_champs_guild = find("canoe_map:champions", 1618082157654974091)
    val destination_barb_village = find("canoe_map:barbarian", 1837217029778931043)
    val destination_wild_pond = find("canoe_map:wilderness", 2365822516849885548)
    val destination_ferox_enclave = find("canoe_map:sanctuary", 2875271963303321718)
}
