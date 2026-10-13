package org.runerealm.content.other.npcdrops

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.drop.table.DropResult
import org.runerealm.api.drop.table.NpcDropTable
import org.runerealm.api.drop.table.NpcDropTables
import org.runerealm.api.drop.table.RolledDrop
import org.runerealm.api.drop.table.WeightedDropTable
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.random.SequenceRandom
import org.runerealm.content.other.npcdrops.configs.LumbridgeDropTables
import org.runerealm.content.other.npcdrops.configs.drop_npcs
import org.runerealm.content.other.npcdrops.configs.drop_objs
import org.runerealm.game.type.npc.NpcType

class LumbridgeNpcDropsTest {
    private val tables = NpcDropTables(setOf(LumbridgeNpcDrops()))

    /** Each npc is registered with the table generated from the wiki page of the same name. */
    private val expectedNames =
        mapOf(
            LumbridgeDropTables.goblin_t1 to "Goblin",
            LumbridgeDropTables.goblin_t2 to "Goblin",
            LumbridgeDropTables.chicken to "Chicken",
            LumbridgeDropTables.cow to "Cow",
            LumbridgeDropTables.cow_calf to "Cow calf",
            LumbridgeDropTables.rat to "Rat",
            LumbridgeDropTables.giant_rat to "Giant rat",
            LumbridgeDropTables.giant_spider to "Giant spider",
            LumbridgeDropTables.spider to "Spider",
            LumbridgeDropTables.man to "Man",
            LumbridgeDropTables.woman to "Woman",
            LumbridgeDropTables.imp to "Imp",
            LumbridgeDropTables.mugger to "Mugger",
            LumbridgeDropTables.unicorn to "Unicorn",
            LumbridgeDropTables.ram to "Ram",
            LumbridgeDropTables.farmer to "Farmer",
            LumbridgeDropTables.frog to "Frog",
            LumbridgeDropTables.big_frog to "Big frog",
            LumbridgeDropTables.giant_frog to "Giant frog",
        )

    /** Npcs that share the empty drop table. */
    private val emptyTableNames = setOf("Duck", "Duckling")

    @Test
    fun GameTestState.`npcs match their wiki page names`() = runGameTest {
        for (npc in allNpcs()) {
            val table = tables[npc]
            assertNotNull(table) { "No table for $npc" }
            val name = npcTypes[npc].name
            if (table === NpcDropTable.Empty) {
                assertTrue(name in emptyTableNames) { "Unexpected empty table for $npc" }
                continue
            }
            assertEquals(expectedNames.getValue(table!!), name) { "Unexpected table for $npc" }
        }
    }

    @Test
    fun GameTestState.`all drop objs resolve to cache types`() = runGameTest {
        for (npc in allNpcs()) {
            for (obj in tables[npc]!!.objs()) {
                assertTrue(objTypes.containsKey(obj.id)) { "Unknown obj $obj for $npc" }
            }
        }
    }

    @Test
    fun `cow always drops hide, beef and bones`() {
        val drops = tables.roll(tables[drop_npcs.cow]!!, SequenceRandom())
        assertEquals(
            listOf(
                RolledDrop(drop_objs.cowhide, 1),
                RolledDrop(drop_objs.raw_beef, 1),
                RolledDrop(drop_objs.bones, 1),
            ),
            drops,
        )
    }

    @Test
    fun `rat drops nothing`() {
        assertEquals(
            emptyList<RolledDrop>(),
            tables.roll(tables[drop_npcs.rat]!!, SequenceRandom()),
        )
    }

    @Test
    fun `man main table rolls coins and herb sub-table`() {
        val man = tables[drop_npcs.man]!!
        val random = SequenceRandom()
        // Bronze med helm (2) + iron dagger (1) + bronze bolts (22) + runes (3+2+2+2+1) = 35.
        random.next = 35 // First herb drop table slot.
        random.then = 0 // Grimy guam leaf (32/128).
        random.then = 99 // Tertiary rolls fail.
        assertEquals(
            listOf(RolledDrop(drop_objs.bones, 1), RolledDrop(drop_objs.grimy_guam_leaf, 1)),
            tables.roll(man, random).take(2),
        )
    }

    private fun allNpcs(): List<NpcType> =
        listOf(
            drop_npcs.goblin_unarmed_melee_1,
            drop_npcs.goblin_unarmed_melee_8,
            drop_npcs.goblin_armed,
            drop_npcs.chicken,
            drop_npcs.chicken_brown,
            drop_npcs.cow,
            drop_npcs.cow3,
            drop_npcs.cow2_calf,
            drop_npcs.rat,
            drop_npcs.giantrat,
            drop_npcs.giantrat_grey3,
            drop_npcs.giantspider1,
            drop_npcs.spider,
            drop_npcs.man,
            drop_npcs.man_indoor,
            drop_npcs.woman3,
            drop_npcs.imp,
            drop_npcs.mugger,
            drop_npcs.unicorn,
            drop_npcs.ramunsheered,
            drop_npcs.farmer4,
            drop_npcs.farmer2_f,
            drop_npcs.farmer3_f,
            drop_npcs.duckling,
            drop_npcs.little_frog_nodrops,
            drop_npcs.medium_frog_nodrops,
            drop_npcs.giant_frog_nodrops,
            drop_npcs.duck,
            drop_npcs.duck_female,
        )

    private fun NpcDropTable.objs(): List<org.runerealm.game.type.obj.ObjType> =
        always.map { it.obj } + tertiary.map { it.drop.obj } + (main?.objs() ?: emptyList())

    private fun WeightedDropTable.objs(): List<org.runerealm.game.type.obj.ObjType> =
        entries.flatMap {
            when (val result = it.result) {
                DropResult.Nothing -> emptyList()
                is DropResult.Obj -> listOf(result.drop.obj)
                is DropResult.SubTable -> result.table.objs()
            }
        }
}
