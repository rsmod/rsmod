package org.runerealm.content.areas.city.lumbridge

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.stats
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.content.areas.city.lumbridge.npcs.Bob
import org.runerealm.content.areas.city.lumbridge.npcs.Doomsayer
import org.runerealm.content.areas.city.lumbridge.npcs.Fayeth
import org.runerealm.content.areas.city.lumbridge.npcs.GeneralStore
import org.runerealm.content.areas.city.lumbridge.npcs.MagicCombatTutor
import org.runerealm.content.areas.city.lumbridge.npcs.Perdu
import org.runerealm.content.areas.city.lumbridge.npcs.RangedCombatTutor
import org.runerealm.content.areas.city.lumbridge.npcs.Vasquen
import org.runerealm.content.areas.city.lumbridge.npcs.WoodsmanTutor
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.npc.NpcType
import org.runerealm.map.CoordGrid

/** Right-click ops of Lumbridge npcs that are not "Talk-to". */
class LumbridgeNpcRightClickTest {
    @Test
    fun GameTestState.`magic tutor claim gives runes`() =
        runGameTest(MagicCombatTutor::class) {
            val npc = spawnNextToPlayer(lumbridge_npcs.magic_tutor)
            player.opNpc3(npc)
            advance(ticks = 3)
            assertEquals(30, player.inv.count(objs.mind_rune))
            assertEquals(30, player.inv.count(objs.air_rune))
        }

    @Test
    fun GameTestState.`ranged tutor claim gives training bow and arrows`() =
        runGameTest(RangedCombatTutor::class) {
            val npc = spawnNextToPlayer(lumbridge_npcs.ranged_tutor)
            player.opNpc3(npc)
            advance(ticks = 3)
            assertEquals(1, player.inv.count(test_objs.training_bow))
            assertTrue(player.inv.count(test_objs.training_arrows) > 0)
        }

    @Test
    fun GameTestState.`doomsayer toggle warnings opens the warnings interface`() =
        runGameTest(Doomsayer::class) {
            val npc = spawnNextToPlayer(lumbridge_npcs.doomsayer)
            player.opNpc3(npc)
            advance(ticks = 3)
            assertTrue(player.ui.containsModal(test_interfaces.cws_doomsayer))
        }

    @Test
    fun GameTestState.`perdu trade opens the lost property shop`() =
        runGameTest(Perdu::class) {
            val npc = spawnNextToPlayer(lumbridge_npcs.perdu)
            player.opNpc3(npc)
            advance(ticks = 3)
            assertTrue(player.ui.containsModal(test_interfaces.lost_property))
        }

    @Test
    fun GameTestState.`woodsman sells the woodcutting cape with no members check`() =
        runGameTest(WoodsmanTutor::class) {
            player.setBaseLevel(stats.woodcutting, 99)
            player.statMap.setXP(stats.woodcutting, 13_034_431)
            player.inv[0] = org.runerealm.game.inv.InvObj(objs.coins, 100_000)
            val npc = spawnNextToPlayer(lumbridge_npcs.woodsman_tutor)
            player.opNpc1(npc)
            advance(ticks = 2)
            // Mastery greeting -> "Yes, please" -> "No problem."
            continueDialogue(choices = listOf(2, 2)) {
                player.inv.count(test_objs.woodcutting_cape) > 0
            }
            assertEquals(1, player.inv.count(test_objs.woodcutting_cape))
            assertEquals(1, player.inv.count(test_objs.woodcutting_hood))
            assertEquals(1_000, player.inv.count(objs.coins))
            assertFalse(player.inv.count(test_objs.woodcutting_cape_trimmed) > 0)
        }

    @Test
    fun GameTestState.`trade ops open the shop`() {
        val traders =
            listOf(
                Triple(lumbridge_npcs.shop_keeper, 3, GeneralStore::class),
                Triple(lumbridge_npcs.shop_assistant, 3, GeneralStore::class),
                Triple(lumbridge_npcs.bob, 3, Bob::class),
                Triple(lumbridge_npcs.fayeth, 5, Fayeth::class),
                Triple(lumbridge_npcs.vasquen, 5, Vasquen::class),
            )
        for ((type, op, script) in traders) {
            runGameTest(script) {
                val npc = spawnNextToPlayer(type)
                when (op) {
                    3 -> player.opNpc3(npc)
                    5 -> player.opNpc5(npc)
                }
                advance(ticks = 3)
                assertTrue(player.ui.containsModal(test_interfaces.shopmain)) {
                    "Shop did not open for ${npcTypes[type].internalName} op$op"
                }
                npcRepo.del(npc, Int.MAX_VALUE)
            }
        }
    }

    private fun GameTestScope.spawnNextToPlayer(type: NpcType): Npc {
        val coords = CoordGrid(0, 50, 50, 20, 20)
        allocZoneCollision(coords)
        player.teleport(coords)
        return spawnNpc(coords.translateX(1), npcTypes[type])
    }

    /**
     * Clicks through chat boxes, answering each menu with the next of [choices], until [done] or
     * the dialogue ends.
     */
    private fun GameTestScope.continueDialogue(choices: List<Int>, done: () -> Boolean) {
        val pending = ArrayDeque(choices)
        repeat(40) {
            if (done()) {
                return
            }
            val ui = player.ui
            when {
                ui.containsModal(interfaces.chatmenu) -> {
                    val choice = pending.removeFirstOrNull() ?: return
                    player.resumePauseButton(components.chatmenu_pbutton, choice)
                }
                ui.containsModal(interfaces.chat_right) ->
                    player.resumePauseButton(components.chat_right_pbutton)
                ui.containsModal(interfaces.chat_left) ->
                    player.resumePauseButton(components.chat_left_pbutton)
                ui.containsModal(interfaces.messagebox) ->
                    player.resumePauseButton(components.messagebox_pbutton)
                ui.containsModal(interfaces.obj_dialogue) ->
                    player.resumePauseButton(components.objectbox_pbutton)
                else -> Unit
            }
            advance()
        }
    }
}

internal object test_objs : ObjReferences() {
    val training_bow = find("aide_shortbow")
    val training_arrows = find("aide_arrow")
    val woodcutting_cape = find("skillcape_woodcutting")
    val woodcutting_cape_trimmed = find("skillcape_woodcutting_trimmed")
    val woodcutting_hood = find("skillcape_woodcutting_hood")
}

internal object test_interfaces : InterfaceReferences() {
    val cws_doomsayer = find("cws_doomsayer")
    val lost_property = find("lost_property")
    val shopmain = find("shopmain")
}
