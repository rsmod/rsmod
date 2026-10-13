package org.runerealm.content.areas.city.lumbridge

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.type.npc.NpcType
import org.runerealm.map.CoordGrid

/** Talks to every scripted Lumbridge NPC and checks that a dialogue starts without errors. */
class LumbridgeNpcTalkTest {
    @Test
    fun GameTestState.`every lumbridge npc starts a dialogue`() {
        val failures = mutableListOf<String>()
        // A fresh scope per batch keeps each scope's entity slots well below its limits.
        for (batch in talkNpcs().chunked(BATCH_SIZE)) {
            runGameTest(*SCRIPTS) { failures += talkToAll(batch) }
        }
        assertTrue(failures.isEmpty()) { "No dialogue started for: $failures" }
    }

    private fun GameTestScope.talkToAll(types: List<NpcType>): List<String> {
        val start = CoordGrid(0, 50, 50, 20, 20)
        val failures = mutableListOf<String>()
        for ((index, type) in types.withIndex()) {
            val coords = start.translate(xOffset = (index % 8) * 4, zOffset = (index / 8) * 4)
            allocZoneCollision(coords)
            val talker = registerPlayer(coords)
            val npc = spawnNpc(coords.translateX(1), npcTypes[type])
            talker.opNpc1(npc)
            var started = false
            for (tick in 0 until 5) {
                advance()
                if (talker.isModalButtonProtected) {
                    started = true
                    break
                }
            }
            if (!started) {
                failures += npcTypes[type].internalName ?: type.toString()
            }
            unregisterPlayer(talker)
            npcRepo.del(npc, Int.MAX_VALUE)
        }
        return failures
    }

    private fun talkNpcs(): List<NpcType> =
        listOf(
            lumbridge_npcs.hans,
            lumbridge_npcs.bob,
            lumbridge_npcs.cook,
            lumbridge_npcs.duke_horacio,
            lumbridge_npcs.father_aereck,
            lumbridge_npcs.guide,
            lumbridge_npcs.count_check,
            lumbridge_npcs.doomsayer,
            lumbridge_npcs.woodsman_tutor,
            lumbridge_npcs.fishing_tutor,
            lumbridge_npcs.prayer_tutor,
            lumbridge_npcs.smithing_apprentice,
            lumbridge_npcs.arthur_the_clue_hunter,
            lumbridge_npcs.hatius_lumbridge_diary,
            lumbridge_npcs.bartender,
            lumbridge_npcs.barfy_bill,
            lumbridge_npcs.shop_keeper,
            lumbridge_npcs.shop_assistant,
            lumbridge_npcs.gee,
            lumbridge_npcs.donie,
            lumbridge_npcs.millie,
            lumbridge_npcs.perdu,
            lumbridge_npcs.veos,
            lumbridge_npcs.father_urhney,
            lumbridge_npcs.candle_seller,
            lumbridge_npcs.sigmund,
            lumbridge_npcs.abigale,
            lumbridge_npcs.hewey,
            lumbridge_npcs.abigaila,
            lumbridge_npcs.nigel,
            lumbridge_npcs.warrior,
            lumbridge_npcs.monk,
            lumbridge_npcs.wizard,
            lumbridge_npcs.archer,
            lumbridge_npcs.melee_tutor,
            lumbridge_npcs.ranged_tutor,
            lumbridge_npcs.magic_tutor,
            lumbridge_npcs.cooking_tutor,
            lumbridge_npcs.crafting_tutor,
            lumbridge_npcs.mining_tutor,
            lumbridge_npcs.fred_the_farmer,
            lumbridge_npcs.seth_groats,
            lumbridge_npcs.gillie,
            lumbridge_npcs.fayeth,
            lumbridge_npcs.vasquen,
            lumbridge_npcs.tool_leprechaun,
        )

    private companion object {
        const val BATCH_SIZE = 15

        val SCRIPTS = LUMBRIDGE_NPC_SCRIPTS
    }
}
