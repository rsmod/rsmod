package org.runerealm.content.other.npcdrops.configs

import org.runerealm.api.drop.table.weightedDropTable

/** Drop tables shared by many npcs, as documented on the OSRS Wiki. */
object SharedDropTables {
    /** [Herb drop table](https://oldschool.runescape.wiki/w/Drop_table#Herb_drop_table) */
    val herb =
        weightedDropTable(128) {
            obj(32, drop_objs.grimy_guam_leaf)
            obj(24, drop_objs.grimy_marrentill)
            obj(18, drop_objs.grimy_tarromin)
            obj(14, drop_objs.grimy_harralander)
            obj(11, drop_objs.grimy_ranarr_weed)
            obj(8, drop_objs.grimy_irit_leaf)
            obj(6, drop_objs.grimy_avantoe)
            obj(5, drop_objs.grimy_kwuarm)
            obj(4, drop_objs.grimy_cadantine)
            obj(3, drop_objs.grimy_lantadyme)
            obj(3, drop_objs.grimy_dwarf_weed)
        }
    /**
     * [Allotment seed drop
     * table](https://oldschool.runescape.wiki/w/Drop_table#Allotment_seed_drop_table)
     */
    val allotment_seed =
        weightedDropTable(128) {
            obj(64, drop_objs.potato_seed, min = 1, max = 4)
            obj(32, drop_objs.onion_seed, min = 1, max = 3)
            obj(16, drop_objs.cabbage_seed, min = 1, max = 3)
            obj(8, drop_objs.tomato_seed, min = 1, max = 2)
            obj(4, drop_objs.sweetcorn_seed, min = 1, max = 2)
            obj(2, drop_objs.strawberry_seed)
            obj(1, drop_objs.watermelon_seed)
            obj(1, drop_objs.snape_grass_seed)
        }
}
