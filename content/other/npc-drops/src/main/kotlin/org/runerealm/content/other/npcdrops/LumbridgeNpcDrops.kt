package org.runerealm.content.other.npcdrops

import org.runerealm.api.drop.table.NpcDropTableProvider
import org.runerealm.content.other.npcdrops.configs.LumbridgeDropTables
import org.runerealm.content.other.npcdrops.configs.drop_npcs

class LumbridgeNpcDrops : NpcDropTableProvider() {
    init {
        register(
            LumbridgeDropTables.goblin_t1,
            drop_npcs.goblin_unarmed_melee_1,
            drop_npcs.goblin_unarmed_melee_2,
            drop_npcs.goblin_unarmed_melee_3,
            drop_npcs.goblin_unarmed_melee_4,
            drop_npcs.goblin_unarmed_melee_5,
            drop_npcs.goblin_unarmed_melee_6,
            drop_npcs.goblin_unarmed_melee_7,
            drop_npcs.goblin_unarmed_melee_8,
        )
        register(LumbridgeDropTables.goblin_t2, drop_npcs.goblin_armed)
        register(LumbridgeDropTables.chicken, drop_npcs.chicken, drop_npcs.chicken_brown)
        register(LumbridgeDropTables.cow, drop_npcs.cow, drop_npcs.cow2, drop_npcs.cow3)
        register(LumbridgeDropTables.cow_calf, drop_npcs.cow2_calf, drop_npcs.cow3_calf)
        register(LumbridgeDropTables.rat, drop_npcs.rat)
        register(
            LumbridgeDropTables.giant_rat,
            drop_npcs.giantrat,
            drop_npcs.giantrat1_2,
            drop_npcs.giantrat1_3,
            drop_npcs.giantrat_grey,
            drop_npcs.giantrat_grey2,
            drop_npcs.giantrat_grey3,
        )
        register(LumbridgeDropTables.giant_spider, drop_npcs.giantspider1)
        register(LumbridgeDropTables.spider, drop_npcs.spider)
        register(
            LumbridgeDropTables.man,
            drop_npcs.man,
            drop_npcs.man2,
            drop_npcs.man3,
            drop_npcs.man_indoor,
        )
        register(LumbridgeDropTables.woman, drop_npcs.woman, drop_npcs.woman2, drop_npcs.woman3)
        register(LumbridgeDropTables.imp, drop_npcs.imp)
        register(LumbridgeDropTables.mugger, drop_npcs.mugger)
        register(LumbridgeDropTables.unicorn, drop_npcs.unicorn)
        register(
            LumbridgeDropTables.ram,
            drop_npcs.ramunsheered,
            drop_npcs.ramunsheered2,
            drop_npcs.ramunsheered3,
        )
        register(
            LumbridgeDropTables.farmer,
            drop_npcs.farmer1,
            drop_npcs.farmer2_f,
            drop_npcs.farmer3,
            drop_npcs.farmer3_f,
            drop_npcs.farmer4,
        )
        register(LumbridgeDropTables.frog, drop_npcs.little_frog_nodrops)
        register(LumbridgeDropTables.big_frog, drop_npcs.medium_frog_nodrops)
        register(LumbridgeDropTables.giant_frog, drop_npcs.giant_frog_nodrops)
        register(LumbridgeDropTables.duck, drop_npcs.duck, drop_npcs.duck_female)
        register(LumbridgeDropTables.duckling, drop_npcs.duckling)
    }
}
