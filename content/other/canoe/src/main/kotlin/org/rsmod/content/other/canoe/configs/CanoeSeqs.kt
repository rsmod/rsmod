package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.refs.seq.SeqReferences

typealias canoe_seqs = CanoeSeqs

object CanoeSeqs : SeqReferences() {
    val canoeing_station_animations = find("canoeing_station_animations", 8572148383224722846)
    val canoeing_pushing_into_water = find("canoeing_pushing_into_water", 2997966032775519330)
    val canoeing_rowing = find("canoeing_rowing", 1556344674969094092)

    val shape_dragon_axe_or =
        find("human_canoeing_carve_trailblazer_axe_no_infernal", 6519373399330397515)
    val shape_infernal_axe = find("human_canoeing_carve_infernal_axe", 6519363148465094371)
    val shape_rune_axe = find("human_canoeing_carve_rune_axe", 6519353112971847058)
    val shape_adamant_axe = find("human_canoeing_carve_adamant_axe", 6519353111282654457)
    val shape_mithril_axe = find("human_canoeing_carve_mithril_axe", 6519353109593461856)
    val shape_black_axe = find("human_canoeing_carve_black_axe", 6519353114661039663)
    val shape_steel_axe = find("human_canoeing_carve_steel_axe", 6519353107904269256)
    val shape_iron_axe = find("human_canoeing_carve_iron_axe", 6519353104525884053)
    val shape_bronze_axe = find("human_canoeing_carve_bronze_axe", 6519353106215076656)
    val shape_dragon_axe = find("human_canoeing_carve_dragon_axe", 6519357656899946445)
    val shape_3a_axe = find("human_canoeing_carve_3a_axe", 6519368866382057288)
    val shape_gilded_axe = find("human_canoeing_carve_gilded_axe", 6519371626522769995)
    val shape_crystal_axe = find("human_canoeing_carve_crystal_axe", 6519371959293712610)
    val shape_infernal_axe_or =
        find("human_canoeing_carve_league_trailblazer_axe", 6519373172978597601)
}
