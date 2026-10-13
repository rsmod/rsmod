@file:Suppress("SpellCheckingInspection")

package org.runerealm.api.death.configs

import org.runerealm.api.type.editors.obj.ObjEditor
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.game.type.obj.ObjType

public typealias death_objs = DeathObjs

/** Objs with special death behaviour, per the wiki's "Items Kept on Death" and "(broken)". */
public object DeathObjs : ObjReferences() {
    public val fire_cape: ObjType = find("tzhaar_cape_fire")
    public val fire_cape_broken: ObjType = find("tzhaar_cape_fire_broken")
    public val fire_cape_locked: ObjType = find("tzhaar_cape_fire_trouver")
    public val looting_bag: ObjType = find("looting_bag")
    public val skavid_map: ObjType = find("skavidmap")

    /** Untradeable items that are always kept, PvM and PvP ("Items that are always kept"). */
    internal val alwaysKept: List<ObjType> =
        listOf(
                "osrs_bond",
                "bought_osrs_bond",
                "osrs_bond_untradeable",
                "gotr_amulet_of_the_eye",
                "gotr_amulet_of_the_eye_red",
                "gotr_amulet_of_the_eye_green",
                "gotr_amulet_of_the_eye_blue",
                "doti_butleruniform",
                "doti_butleruniform_legs",
                "doti_butleruniform_villa",
                "doti_butleruniform_legs_villa",
                "crystal_chime",
                "wgs_dark_squall_hood",
                "wgs_dark_squall_robe_top",
                "wgs_dark_squall_robe_bottom",
                "witches_doorkey",
                "giantsoul_amulet_uncharged",
                "giantsoul_amulet_charged",
                "tog_sapphire_lantern_empty",
                "tog_sapphire_lantern_unlit",
                "tog_sapphire_lantern_lit",
                "strange_old_lockpick",
                "strange_old_lockpick_full",
            )
            .map { find(it) } + skavid_map

    /** Items that are never kept and vanish on an unsafe death ("Items that are never kept"). */
    internal val alwaysDeleted: List<ObjType> =
        listOf(
                "looting_bag_open",
                "bh_clue_box",
                "league_clue_box_beginner",
                "league_clue_box_easy",
                "league_clue_box_medium",
                "league_clue_box_hard",
                "league_clue_box_elite",
                "league_clue_box_master",
                "chronicle",
                "bh_emblem",
                "bh_emblem_2",
                "bh_emblem_3",
                "bh_emblem_4",
                "bh_emblem_5",
                "bh_emblem_6",
                "bh_emblem_7",
                "bh_emblem_8",
                "bh_emblem_9",
                "bh_emblem_10",
            )
            .map { find(it) } + looting_bag

    /**
     * PvP-breakable untradeables from the wiki's "(broken)" page: base name, repair cost, and
     * whether the unlocked item is lost completely above level 20 Wilderness. Each has a
     * `<name>_broken` form, and those with a `<name>_trouver` form can be locked.
     */
    internal val breakables: List<Breakable> =
        listOf(
            breakable("tzhaar_cape_fire", 150_000, false),
            breakable("infernal_cape", 225_000, true),
            breakable("avas_assembler", 240_000, false),
            breakable("ma2_saradomin_cape", 96_000, false),
            breakable("ma2_guthix_cape", 96_000, false),
            breakable("ma2_zamorak_cape", 96_000, false),
            breakable("skillcape_max_firecape", 150_000, false),
            breakable("skillcape_max_infernalcape", 225_000, true),
            breakable("skillcape_max_assembler", 240_000, false),
            breakable("skillcape_max_saradomin2", 99_000, false),
            breakable("skillcape_max_guthix2", 99_000, false),
            breakable("skillcape_max_zamorak2", 99_000, false),
            breakable("bronze_parryingdagger", 1_000, false),
            breakable("iron_parryingdagger", 2_000, false),
            breakable("steel_parryingdagger", 2_500, false),
            breakable("black_parryingdagger", 5_000, false),
            breakable("mithril_parryingdagger", 15_000, false),
            breakable("adamant_parryingdagger", 25_000, false),
            breakable("rune_parryingdagger", 35_000, false),
            breakable("dragon_parryingdagger", 240_000, false),
            breakable("infernal_defender", 600_000, false),
            breakable("game_pest_melee_helm", 160_000, true),
            breakable("game_pest_mage_helm", 160_000, true),
            breakable("game_pest_archer_helm", 160_000, true),
            breakable("pest_void_knight_top", 180_000, true),
            breakable("elite_void_knight_top", 250_000, true),
            breakable("pest_void_knight_robes", 180_000, true),
            breakable("elite_void_knight_robes", 250_000, true),
            breakable("pest_void_knight_gloves", 120_000, true),
            breakable("pest_void_knight_mace", 20_000, false),
            breakable("castlewars_sword_3", 5_000, false),
            breakable("castlewars_armour_body_3", 5_000, false),
            breakable("castlewars_armour_legs_3", 5_000, false),
            breakable("castlewars_med_helm_3", 5_000, false),
            breakable("castlewars_shield_3", 5_000, false),
            breakable("castlewars_armour_skirt_3", 5_000, false),
            breakable("castlewars_boots_3", 5_000, false),
            breakable("castlewars_full_helm_3", 5_000, false),
            breakable("castlewars_mage_top", 5_000, false),
            breakable("castlewars_mage_legs", 5_000, false),
            breakable("castlewars_mage_hat", 5_000, false),
            breakable("castlewars_range_top", 5_000, false),
            breakable("castlewars_range_legs", 5_000, false),
            breakable("castlewars_range_quiver", 5_000, false),
            breakable("castlewars_saradomin_halo", 25_000, false),
            breakable("castlewars_zamorak_halo", 25_000, false),
            breakable("castlewars_guthix_halo", 25_000, false),
            breakable("armadyl_halo", 25_000, false),
            breakable("bandos_halo", 25_000, false),
            breakable("seren_halo", 25_000, false),
            breakable("zaros_halo", 25_000, false),
            breakable("brassica_halo", 25_000, false),
            breakable("barbassault_penance_fighter_hat", 45_000, false),
            breakable("barbassault_penance_ranger_hat", 45_000, false),
            breakable("barbassault_penance_healer_hat", 45_000, false),
            breakable("barbassault_penance_runner_hat", 40_005, false),
            breakable("barbassault_penance_fighter_torso", 150_000, true),
            breakable("barbassault_penance_ranger_legs", 20_000, false),
            breakable("barronite_mace", 10_500, false),
        )

    /** Barrows equipment: every charge state drops to the killer as its broken (`0`) form. */
    internal val barrows: List<Pair<List<ObjType>, ObjType>> =
        listOf("ahrim", "dharok", "guthan", "karil", "torag", "verac").flatMap { brother ->
            listOf("head", "weapon", "body", "legs").map { piece ->
                val base = "barrows_${brother}_$piece"
                val states = listOf(base) + listOf(100, 75, 50, 25).map { "${base}_$it" }
                states.map { find(it) } to find("${base}_broken")
            }
        }

    /** Slayer helmets drop to the killer as a black mask. */
    internal val slayerHelms: List<ObjType> =
        listOf(
                "slayer_helm",
                "slayer_helm_i",
                "slayer_helm_black",
                "slayer_helm_i_black",
                "slayer_helm_green",
                "slayer_helm_i_green",
                "slayer_helm_red",
                "slayer_helm_i_red",
                "slayer_helm_purple",
                "slayer_helm_i_purple",
                "slayer_helm_turquoise",
                "slayer_helm_i_turquoise",
                "slayer_helm_hydra",
                "slayer_helm_i_hydra",
                "slayer_helm_twisted",
                "slayer_helm_i_twisted",
                "slayer_helm_jad",
                "slayer_helm_i_jad",
                "slayer_helm_verzik",
                "slayer_helm_i_verzik",
                "slayer_helm_zuk",
                "slayer_helm_i_zuk",
            )
            .map { find(it) }

    internal val black_mask: ObjType = find("harmless_black_mask")
    internal val magic_shortbow_i: ObjType = find("magic_shortbow_i")
    internal val magic_shortbow: ObjType = find("magic_shortbow")

    /**
     * Untradeable, purchasable quest rewards with notable combat stats: turned into coins for the
     * killer (e.g. Culinaromancer's gloves, Barrelchest anchor).
     */
    internal val coinsForKiller: List<ObjType> =
        (1..10).map { find("hundred_gauntlets_level_$it") } + find("brain_anchor")

    private fun breakable(name: String, repairCost: Int, lostAbove20: Boolean): Breakable =
        Breakable(
            obj = find(name),
            broken = find("${name}_broken"),
            locked = find("${name}_trouver"),
            repairCost = repairCost,
            lostAbove20 = lostAbove20,
        )

    internal data class Breakable(
        val obj: ObjType,
        val broken: ObjType,
        val locked: ObjType,
        val repairCost: Int,
        val lostAbove20: Boolean,
    )
}

internal object DeathObjEditor : ObjEditor() {
    init {
        for (obj in death_objs.alwaysKept) {
            edit(obj) { param[death_params.keep] = true }
        }
        for (obj in death_objs.alwaysDeleted) {
            edit(obj) { param[death_params.delete] = true }
        }
        for (breakable in death_objs.breakables) {
            edit(breakable.obj) {
                tradeable = false
                param[death_params.pvp_broken] = breakable.broken
                param[death_params.pvp_repair_cost] = breakable.repairCost
                param[death_params.lost_above_20] = breakable.lostAbove20
            }
            edit(breakable.locked) {
                tradeable = false
                param[death_params.pvm_keep] = true
                param[death_params.pvp_broken] = breakable.broken
                param[death_params.pvp_repair_cost] = breakable.repairCost
                param[death_params.lost_above_20] = false
            }
        }
        for ((states, broken) in death_objs.barrows) {
            for (state in states) {
                edit(state) { param[death_params.pvp_drop_as] = broken }
            }
        }
        for (helm in death_objs.slayerHelms) {
            edit(helm) {
                tradeable = false
                param[death_params.pvp_drop_as] = death_objs.black_mask
            }
        }
        edit(death_objs.magic_shortbow_i) {
            param[death_params.pvp_drop_as] = death_objs.magic_shortbow
        }
        for (obj in death_objs.coinsForKiller) {
            edit(obj) {
                tradeable = false
                param[death_params.pvp_coins] = true
            }
        }
    }
}
