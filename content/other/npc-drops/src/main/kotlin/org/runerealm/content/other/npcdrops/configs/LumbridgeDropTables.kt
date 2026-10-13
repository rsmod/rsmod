package org.runerealm.content.other.npcdrops.configs

import org.runerealm.api.drop.table.NpcDropTable
import org.runerealm.api.drop.table.npcDropTable

/**
 * Drop tables for npcs around Lumbridge, generated from the OSRS Wiki (members' worlds).
 *
 * Drops that depend on systems that are not implemented yet (clue scrolls, quests, Wilderness-only
 * drops) are left out, as are energy potions, which were added after the revision this server
 * targets (Summer Sweep Up, 19 August 2026).
 */
object LumbridgeDropTables {
    /** [Goblin (drop table 1)](https://oldschool.runescape.wiki/w/Goblin) */
    // Not implemented: Energy potion(1) (energy potions added after rev 233 (19 Aug 2026)); Energy
    // potion(2) (energy potions added after rev 233 (19 Aug 2026)); Energy potion(3) (energy
    // potions added after rev 233 (19 Aug 2026)); Energy potion(4) (energy potions added after rev
    // 233 (19 Aug 2026)); Goblin skull (quest-only); beginner (clue scrolls not implemented); easy
    // (clue scrolls not implemented)
    val goblin_t1 = npcDropTable {
        always(drop_objs.bones)
        main(128) {
            obj(3, drop_objs.bronze_sq_shield)
            obj(4, drop_objs.bronze_spear)
            obj(5, drop_objs.body_rune, min = 7)
            obj(6, drop_objs.water_rune, min = 6)
            obj(3, drop_objs.earth_rune, min = 4)
            obj(3, drop_objs.bronze_bolts, min = 8)
            obj(28, drop_objs.coins, min = 5)
            obj(3, drop_objs.coins, min = 9)
            obj(3, drop_objs.coins, min = 15)
            obj(2, drop_objs.coins, min = 20)
            obj(1, drop_objs.coins)
            nothing(38)
            obj(15, drop_objs.hammer)
            obj(2, drop_objs.goblin_book)
            obj(5, drop_objs.goblin_mail)
            obj(3, drop_objs.chef_s_hat)
            obj(2, drop_objs.beer)
            obj(1, drop_objs.brass_necklace)
            obj(1, drop_objs.air_talisman)
        }
        tertiary(1, 35, drop_objs.ensouled_goblin_head)
        tertiary(1, 5000, drop_objs.goblin_champion_scroll)
    }

    /** [Goblin (drop table 2)](https://oldschool.runescape.wiki/w/Goblin) */
    // Not implemented: Coins (f2p only); Energy potion(1) (energy potions added after rev 233 (19
    // Aug 2026)); Energy potion(2) (energy potions added after rev 233 (19 Aug 2026)); Goblin skull
    // (quest-only); beginner (clue scrolls not implemented); easy (clue scrolls not implemented)
    val goblin_t2 = npcDropTable {
        always(drop_objs.bones)
        main(128) {
            obj(3, drop_objs.bronze_axe)
            obj(1, drop_objs.bronze_scimitar)
            obj(9, drop_objs.bronze_spear)
            obj(3, drop_objs.bronze_arrow, min = 7)
            obj(3, drop_objs.mind_rune, min = 2)
            obj(3, drop_objs.earth_rune, min = 4)
            obj(3, drop_objs.body_rune, min = 2)
            obj(2, drop_objs.bronze_javelin, min = 5)
            obj(1, drop_objs.chaos_rune)
            obj(1, drop_objs.nature_rune)
            table(2, SharedDropTables.herb)
            obj(34, drop_objs.coins)
            obj(13, drop_objs.coins, min = 3)
            obj(8, drop_objs.coins, min = 5)
            obj(7, drop_objs.coins, min = 16)
            obj(3, drop_objs.coins, min = 24)
            nothing(8)
            obj(9, drop_objs.hammer)
            obj(2, drop_objs.goblin_book)
            obj(10, drop_objs.goblin_mail)
            obj(1, drop_objs.grapes)
            obj(1, drop_objs.red_cape)
            obj(1, drop_objs.tin_ore)
        }
        tertiary(1, 30, drop_objs.ensouled_goblin_head)
        tertiary(1, 5000, drop_objs.goblin_champion_scroll)
    }

    /** [Chicken](https://oldschool.runescape.wiki/w/Chicken) */
    // Not implemented: Key (medium) (clue step only); beginner (clue scrolls not implemented)
    val chicken = npcDropTable {
        always(drop_objs.bones)
        always(drop_objs.raw_chicken)
        main(128) {
            obj(64, drop_objs.feather, min = 5)
            obj(32, drop_objs.feather, min = 15)
            nothing(32)
        }
    }

    /** [Cow](https://oldschool.runescape.wiki/w/Cow) */
    // Not implemented: beginner (clue scrolls not implemented)
    val cow = npcDropTable {
        always(drop_objs.cowhide)
        always(drop_objs.raw_beef)
        always(drop_objs.bones)
    }

    /** [Cow calf](https://oldschool.runescape.wiki/w/Cow_calf) */
    // Not implemented: beginner (clue scrolls not implemented)
    val cow_calf = npcDropTable {
        always(drop_objs.cowhide)
        always(drop_objs.raw_beef)
        always(drop_objs.bones)
    }

    /** [Rat](https://oldschool.runescape.wiki/w/Rat) */
    // Not implemented: Looting bag (wilderness only); Rat bone (quest-only); Rat's tail
    // (quest-only)
    val rat = npcDropTable {}

    /** [Giant rat (level 3 and 6)](https://oldschool.runescape.wiki/w/Giant_rat) */
    // Not implemented: Giant rat bone (quest-only); Looting bag (wilderness only); beginner (clue
    // scrolls not implemented)
    val giant_rat = npcDropTable {
        always(drop_objs.bones)
        always(drop_objs.raw_rat_meat)
    }

    /** [Giant spider](https://oldschool.runescape.wiki/w/Giant_spider) */
    // Not implemented: Looting bag (wilderness only); beginner (clue scrolls not implemented)
    val giant_spider = npcDropTable {}

    /** [Spider](https://oldschool.runescape.wiki/w/Spider) */
    // Not implemented: Looting bag (wilderness only); beginner (clue scrolls not implemented)
    val spider = npcDropTable {}

    /** [Man](https://oldschool.runescape.wiki/w/Man) */
    // Not implemented: Coins (f2p only); Energy potion(3) (energy potions added after rev 233 (19
    // Aug 2026)); Key (medium) (clue step only); Looting bag (wilderness only); beginner (clue
    // scrolls not implemented); easy (clue scrolls not implemented)
    val man = npcDropTable {
        always(drop_objs.bones)
        main(128) {
            obj(2, drop_objs.bronze_med_helm)
            obj(1, drop_objs.iron_dagger)
            obj(22, drop_objs.bronze_bolts, min = 2, max = 12)
            obj(3, drop_objs.bronze_arrow, min = 7)
            obj(2, drop_objs.earth_rune, min = 4)
            obj(2, drop_objs.fire_rune, min = 6)
            obj(2, drop_objs.mind_rune, min = 9)
            obj(1, drop_objs.chaos_rune, min = 2)
            table(23, SharedDropTables.herb)
            obj(38, drop_objs.coins, min = 3)
            obj(9, drop_objs.coins, min = 5)
            obj(4, drop_objs.coins, min = 15)
            obj(1, drop_objs.coins, min = 25)
            nothing(8)
            obj(5, drop_objs.fishing_bait)
            obj(2, drop_objs.copper_ore)
            obj(2, drop_objs.earth_talisman)
            obj(1, drop_objs.cabbage)
        }
    }

    /** [Woman](https://oldschool.runescape.wiki/w/Woman) */
    // Not implemented: Coins (f2p only); Energy potion(3) (energy potions added after rev 233 (19
    // Aug 2026)); beginner (clue scrolls not implemented); easy (clue scrolls not implemented)
    val woman = npcDropTable {
        always(drop_objs.bones)
        main(128) {
            obj(2, drop_objs.bronze_med_helm)
            obj(1, drop_objs.iron_dagger)
            obj(22, drop_objs.bronze_bolts, min = 2, max = 12)
            obj(3, drop_objs.bronze_arrow, min = 7)
            obj(2, drop_objs.earth_rune, min = 4)
            obj(2, drop_objs.fire_rune, min = 6)
            obj(2, drop_objs.mind_rune, min = 9)
            obj(1, drop_objs.chaos_rune, min = 2)
            table(23, SharedDropTables.herb)
            obj(38, drop_objs.coins, min = 3)
            obj(9, drop_objs.coins, min = 5)
            obj(4, drop_objs.coins, min = 15)
            obj(1, drop_objs.coins, min = 25)
            nothing(8)
            obj(5, drop_objs.fishing_bait)
            obj(2, drop_objs.copper_ore)
            obj(2, drop_objs.earth_talisman)
            obj(1, drop_objs.cabbage)
        }
    }

    /** [Imp](https://oldschool.runescape.wiki/w/Imp) */
    // Not implemented: Looting bag (wilderness only)
    val imp = npcDropTable {
        always(drop_objs.fiendish_ashes)
        main(128) {
            obj(5, drop_objs.black_bead)
            obj(5, drop_objs.red_bead)
            obj(5, drop_objs.white_bead)
            obj(5, drop_objs.yellow_bead)
            obj(8, drop_objs.bronze_bolts)
            obj(8, drop_objs.blue_wizard_hat)
            obj(5, drop_objs.egg)
            obj(5, drop_objs.raw_chicken)
            obj(4, drop_objs.burnt_bread)
            obj(4, drop_objs.burnt_meat)
            obj(2, drop_objs.cabbage)
            obj(2, drop_objs.bread_dough)
            obj(1, drop_objs.bread)
            obj(1, drop_objs.cooked_meat)
            obj(8, drop_objs.hammer)
            obj(5, drop_objs.tinderbox)
            obj(4, drop_objs.shears)
            obj(4, drop_objs.bucket)
            obj(2, drop_objs.bucket_of_water)
            obj(2, drop_objs.jug)
            obj(2, drop_objs.jug_of_water)
            obj(2, drop_objs.pot)
            obj(2, drop_objs.pot_of_flour)
            obj(8, drop_objs.ball_of_wool)
            obj(7, drop_objs.mind_talisman)
            obj(6, drop_objs.ashes)
            obj(4, drop_objs.clay)
            obj(4, drop_objs.cadava_berries)
            obj(3, drop_objs.grain)
            obj(2, drop_objs.chef_s_hat)
            obj(2, drop_objs.flyer)
            obj(1, drop_objs.potion_apothecary)
        }
        tertiary(1, 25, drop_objs.ensouled_imp_head)
        tertiary(1, 5000, drop_objs.imp_champion_scroll)
    }

    /** [Mugger](https://oldschool.runescape.wiki/w/Mugger) */
    // Not implemented: Coins (f2p only); beginner (clue scrolls not implemented); easy (clue
    // scrolls not implemented)
    val mugger = npcDropTable {
        always(drop_objs.bones)
        main(128) {
            obj(27, drop_objs.bronze_bolts, min = 2, max = 12)
            obj(3, drop_objs.mind_rune, min = 9)
            obj(2, drop_objs.water_rune, min = 6)
            obj(2, drop_objs.earth_rune, min = 5)
            table(13, SharedDropTables.herb)
            obj(12, drop_objs.coins, min = 5)
            obj(3, drop_objs.coins, min = 15)
            obj(1, drop_objs.coins, min = 25)
            obj(40, drop_objs.rope)
            nothing(13)
            obj(1, drop_objs.knife)
            obj(6, drop_objs.fishing_bait)
            obj(2, drop_objs.copper_ore)
            obj(2, drop_objs.bronze_med_helm)
            obj(1, drop_objs.cabbage)
        }
    }

    /** [Unicorn](https://oldschool.runescape.wiki/w/Unicorn) */
    // Not implemented: Unicorn bone (quest-only)
    val unicorn = npcDropTable {
        always(drop_objs.bones)
        always(drop_objs.unicorn_horn)
        tertiary(1, 35, drop_objs.ensouled_unicorn_head)
    }

    /** [Ram](https://oldschool.runescape.wiki/w/Ram) */
    // Not implemented: Ram skull (quest-only)
    val ram = npcDropTable { always(drop_objs.bones) }

    /** [Farmer (members' worlds)](https://oldschool.runescape.wiki/w/Farmer) */
    // Not implemented: Energy potion(3) (energy potions added after rev 233 (19 Aug 2026));
    // beginner (clue scrolls not implemented); easy (clue scrolls not implemented)
    val farmer = npcDropTable {
        always(drop_objs.bones)
        main(128) {
            obj(2, drop_objs.earth_rune, min = 4)
            obj(2, drop_objs.fire_rune, min = 6)
            obj(2, drop_objs.mind_rune, min = 9)
            obj(1, drop_objs.chaos_rune, min = 2)
            table(11, SharedDropTables.herb)
            table(27, SharedDropTables.allotment_seed)
            obj(38, drop_objs.coins, min = 3)
            obj(1, drop_objs.coins, min = 25)
            nothing(28)
            obj(5, drop_objs.fishing_bait)
            obj(3, drop_objs.rake)
            obj(2, drop_objs.earth_talisman)
            obj(2, drop_objs.gardening_boots)
            obj(2, drop_objs.seed_dibber)
            obj(1, drop_objs.secateurs)
            obj(1, drop_objs.watering_can_8)
        }
    }

    /** [Frog](https://oldschool.runescape.wiki/w/Frog) */
    // Not implemented: beginner (clue scrolls not implemented)
    val frog = npcDropTable { always(drop_objs.bones) }

    /** [Big frog (level 10)](https://oldschool.runescape.wiki/w/Big_frog) */
    // Not implemented: Big frog leg (quest-only); beginner (clue scrolls not implemented)
    val big_frog = npcDropTable { always(drop_objs.bones) }

    /** [Giant frog (level 13)](https://oldschool.runescape.wiki/w/Giant_frog) */
    // Not implemented: beginner (clue scrolls not implemented)
    val giant_frog = npcDropTable {
        always(drop_objs.big_bones)
        tertiary(1, 400, drop_objs.long_bone)
        tertiary(2, 10025, drop_objs.curved_bone)
    }

    /** [Duck](https://oldschool.runescape.wiki/w/Duck): ducks do not drop anything. */
    val duck = NpcDropTable.Empty

    /** [Duckling](https://oldschool.runescape.wiki/w/Duckling): ducklings do not drop anything. */
    val duckling = NpcDropTable.Empty
}
