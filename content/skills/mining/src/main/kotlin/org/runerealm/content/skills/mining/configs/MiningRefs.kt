package org.runerealm.content.skills.mining.configs

import org.runerealm.api.type.refs.category.CategoryReferences
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.seq.SeqReferences

typealias mining_objs = MiningObjs

typealias mining_locs = MiningLocs

typealias mining_seqs = MiningSeqs

typealias mining_categories = MiningCategories

object MiningObjs : ObjReferences() {
    val clay = find("clay")
    val soft_clay = find("softclay")
    val copper_ore = find("copper_ore")
    val tin_ore = find("tin_ore")
    val blurite_ore = find("blurite_ore")
    val iron_ore = find("iron_ore")
    val silver_ore = find("silver_ore")
    val coal = find("coal")
    val gold_ore = find("gold_ore")
    val mithril_ore = find("mithril_ore")
    val lovakite_ore = find("lovakite_ore")
    val adamantite_ore = find("adamantite_ore")
    val runite_ore = find("runite_ore")
    val amethyst = find("amethyst")
    val rune_essence = find("blankrune")
    val pure_essence = find("blankrune_high")

    val granite_500g = find("enakh_granite_tiny")
    val granite_2kg = find("enakh_granite_small")
    val granite_5kg = find("enakh_granite_medium")
    val sandstone_1kg = find("enakh_sandstone_tiny")
    val sandstone_2kg = find("enakh_sandstone_small")
    val sandstone_5kg = find("enakh_sandstone_medium")
    val sandstone_10kg = find("enakh_sandstone_large")

    val uncut_opal = find("uncut_opal")
    val uncut_jade = find("uncut_jade")
    val uncut_red_topaz = find("uncut_red_topaz")
    val uncut_sapphire = find("uncut_sapphire")
    val uncut_emerald = find("uncut_emerald")
    val uncut_ruby = find("uncut_ruby")
    val uncut_diamond = find("uncut_diamond")

    val bronze_pickaxe = find("bronze_pickaxe")
    val iron_pickaxe = find("iron_pickaxe")
    val steel_pickaxe = find("steel_pickaxe")
    val black_pickaxe = find("black_pickaxe")
    val mithril_pickaxe = find("mithril_pickaxe")
    val adamant_pickaxe = find("adamant_pickaxe")
    val rune_pickaxe = find("rune_pickaxe")
    val dragon_pickaxe = find("dragon_pickaxe")
    val dragon_pickaxe_upgraded = find("dragon_pickaxe_pretty")
    val dragon_pickaxe_or_zalcano = find("zalcano_pickaxe")
    val dragon_pickaxe_or_trailblazer = find("trailblazer_pickaxe_no_infernal")
    val infernal_pickaxe = find("infernal_pickaxe")
    val infernal_pickaxe_uncharged = find("infernal_pickaxe_empty")
    val infernal_pickaxe_or = find("trailblazer_pickaxe")
    val infernal_pickaxe_or_uncharged = find("trailblazer_pickaxe_empty")
    val third_age_pickaxe = find("3a_pickaxe")
    val crystal_pickaxe = find("crystal_pickaxe")
    val crystal_pickaxe_inactive = find("crystal_pickaxe_inactive")

    val amulet_of_glory_1 = find("amulet_of_glory_1")
    val amulet_of_glory_2 = find("amulet_of_glory_2")
    val amulet_of_glory_3 = find("amulet_of_glory_3")
    val amulet_of_glory_4 = find("amulet_of_glory_4")
    val amulet_of_glory_5 = find("amulet_of_glory_5")
    val amulet_of_glory_6 = find("amulet_of_glory_6")
    val amulet_of_glory_t1 = find("trail_amulet_of_glory_1")
    val amulet_of_glory_t2 = find("trail_amulet_of_glory_2")
    val amulet_of_glory_t3 = find("trail_amulet_of_glory_3")
    val amulet_of_glory_t4 = find("trail_amulet_of_glory_4")
    val amulet_of_glory_t5 = find("trail_amulet_of_glory_5")
    val amulet_of_glory_t6 = find("trail_amulet_of_glory_6")
    val amulet_of_eternal_glory = find("amulet_of_glory_inf")
}

object MiningLocs : LocReferences() {
    // Depleted ("empty") rocks, matched to their ore rock by model.
    val rocks1 = find("rocks1")
    val rocks2 = find("rocks2")
    val rocks3 = find("rocks3")
    val prif_rocks_empty = find("prif_mine_rocks1_empty")
    val vein_depleted_l = find("lotr_mine_wall_depleted_l")
    val vein_depleted_mid = find("lotr_mine_wall_depleted_mid")
    val vein_depleted_r = find("lotr_mine_wall_depleted_r")
    val vein_depleted_single = find("lotr_mine_wall_depleted_single")
    val amethyst_empty = find("amethystrock_empty")

    // Rocks without a cache category of their own.
    val granite_rocks = find("enakh_granite_rocks")
    val sandstone_rocks = find("enakh_sandstone_rocks")
    val rune_essence = find("blankrunestone")
    val lunar_rune_essence = find("lunar_runestone_top")
}

object MiningSeqs : SeqReferences() {
    val bronze_pickaxe = find("human_mining_bronze_pickaxe")
    val iron_pickaxe = find("human_mining_iron_pickaxe")
    val steel_pickaxe = find("human_mining_steel_pickaxe")
    val black_pickaxe = find("human_mining_black_pickaxe")
    val mithril_pickaxe = find("human_mining_mithril_pickaxe")
    val adamant_pickaxe = find("human_mining_adamant_pickaxe")
    val rune_pickaxe = find("human_mining_rune_pickaxe")
    val dragon_pickaxe = find("human_mining_dragon_pickaxe")
    val dragon_pickaxe_upgraded = find("human_mining_dragon_pickaxe_pretty")
    val dragon_pickaxe_or_trailblazer = find("human_mining_trailblazer_pickaxe_no_infernal")
    val infernal_pickaxe = find("human_mining_infernal_pickaxe")
    val infernal_pickaxe_or = find("human_mining_trailblazer_pickaxe")
    val third_age_pickaxe = find("human_mining_3a_pickaxe")
    val crystal_pickaxe = find("human_mining_crystal_pickaxe")
}

object MiningCategories : CategoryReferences() {
    val clay_rocks = find("clay_rocks")
    val copper_rocks = find("copper_rocks")
    val tin_rocks = find("tin_rocks")
    val blurite_rocks = find("blurite_rocks")
    val iron_rocks = find("iron_rocks")
    val silver_rocks = find("silver_rocks")
    val coal_rocks = find("coal_rocks")
    val gold_rocks = find("gold_rocks")
    val gem_rocks = find("gem_rocks")
    val mithril_rocks = find("mithril_rocks")
    val lovakite_rocks = find("lovakite_rocks")
    val adamantite_rocks = find("adamantite_rocks")
    val soft_clay_rocks = find("soft_clay_rocks")
    val runite_rocks = find("runite_rocks")
    val amethyst_rocks = find("amethyst_rocks")
    val depleted_rocks = find("depleted_rocks")
}
