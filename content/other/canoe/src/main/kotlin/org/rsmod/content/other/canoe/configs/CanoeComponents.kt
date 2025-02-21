package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias canoe_components = CanoeComponents

object CanoeComponents : ComponentReferences() {
    val shape_log = find("canoe_shaping_com20", 5540955978932720971)
    val shape_dugout = find("canoe_shaping_com18", 574740175170890111)
    val shape_stable_dugout = find("canoe_shaping_com12", 5904149818566193745)
    val shape_waka = find("canoe_shaping_com11", 558687581070453046)
    val shape_com9 = find("canoe_shaping_com9", 1179288482004399632)

    val destination_edgeville = find("canoe_destination_com14", 7376914826480145382)
    val destination_lumbridge = find("canoe_destination_com15", 3916036672780179471)
    val destination_champs_guild = find("canoe_destination_com16", 1618082157654974091)
    val destination_barb_village = find("canoe_destination_com17", 1837217029778931043)
    val destination_wild_pond = find("canoe_destination_com18", 2365822516849885548)
    val destination_ferox_enclave = find("canoe_destination_com19", 2875271963303321718)
}
