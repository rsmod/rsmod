package org.rsmod.api.player.protect

import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.script.onApNpc1
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.game.entity.Npc
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class ProtectedAccessPlayerMoveTest {
    @Test
    fun GameTestState.`playerRun towards bartender from west entrance door`() =
        runGameTest(BartenderTestScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 32, 41), bartender)
            val start = CoordGrid(0, 50, 50, 26, 40)
            player.teleport(start)
            player.enableRun()
            player.opNpc1(npc)

            advance(ticks = 1)
            assertModalNotOpen(interfaces.npc_dialogue)

            advance(ticks = 1)
            val expectedPath = start.translate(3, -1)
            check(player.coords == expectedPath) {
                "Unexpected player coords after test script 1-cycle delay: " +
                    "expected=$expectedPath, actual=${player.coords}"
            }
            assertModalOpen(interfaces.npc_dialogue)

            advance(ticks = 1)
            val expectedDest = start.translate(4, -1)
            assertEquals(expectedDest, player.coords)
        }

    @Test
    fun GameTestState.`playerWalk towards bartender from west entrance door`() =
        runGameTest(BartenderTestScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 32, 41), bartender)
            val start = CoordGrid(0, 50, 50, 26, 40)
            player.teleport(start)
            player.enableWalk()
            player.opNpc1(npc)

            fun assertExpectedPath(expected: CoordGrid) {
                check(player.coords == expected) {
                    "Unexpected player coords: expected=$expected, actual=${player.coords}"
                }
                assertModalNotOpen(interfaces.npc_dialogue)
            }

            // This portion happens while player does not have LOS to npc, which means the ap
            // script has not yet been invoked.
            advance(ticks = 1)
            assertExpectedPath(start.translate(0, -1))

            advance(ticks = 1)
            assertExpectedPath(start.translate(1, -1))

            advance(ticks = 1)
            assertExpectedPath(start.translate(2, -1))

            advance(ticks = 1)
            assertExpectedPath(start.translate(3, -1))

            advance(ticks = 1)
            assertEquals(start.translate(4, -1), player.coords)
            assertModalOpen(interfaces.npc_dialogue)
        }

    @Test
    fun GameTestState.`playerWalk towards bartender with distance of 3`() =
        runGameTest(BartenderTestScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 32, 41), bartender)
            val start = CoordGrid(0, 50, 50, 29, 39)
            player.teleport(start)
            player.enableWalk()
            player.opNpc1(npc)

            advance(ticks = 1)
            assertModalNotOpen(interfaces.npc_dialogue)

            advance(ticks = 1)
            val expectedPath = start.translate(1, 0)
            check(player.coords == expectedPath) {
                "Unexpected player coords after test script 1-cycle delay: " +
                    "expected=$expectedPath, actual=${player.coords}"
            }
            assertModalNotOpen(interfaces.npc_dialogue)

            advance(ticks = 1)
            assertEquals(expectedPath, player.coords)
            assertModalOpen(interfaces.npc_dialogue)
        }

    @Test
    fun GameTestState.`playerRun towards bartender while stuck behind stool diagonal from bar`() =
        runGameTest(BartenderTestScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 32, 41), bartender)
            val start = CoordGrid(0, 50, 50, 30, 39)
            player.teleport(start)
            player.enableRun()
            player.opNpc1(npc)

            advance(ticks = 1)
            assertModalNotOpen(interfaces.npc_dialogue)

            advance(ticks = 1)
            assertEquals(start, player.coords)
            assertModalOpen(interfaces.npc_dialogue)
        }

    @Test
    fun GameTestState.`playerWalk towards bartender while stuck behind stool diagonal from bar`() =
        runGameTest(BartenderTestScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 32, 41), bartender)
            val start = CoordGrid(0, 50, 50, 30, 39)
            player.teleport(start)
            player.enableWalk()
            player.opNpc1(npc)

            advance(ticks = 1)
            assertModalNotOpen(interfaces.npc_dialogue)

            advance(ticks = 1)
            check(player.coords == start) {
                "Unexpected player coords: expected=$start, actual=${player.coords}"
            }
            assertModalNotOpen(interfaces.npc_dialogue)

            advance(ticks = 1)
            assertEquals(start, player.coords)
            assertModalOpen(interfaces.npc_dialogue)
        }

    private class BartenderTestScript : PluginScript() {
        override fun ScriptContext.startUp() {
            onApNpc1(bartender) { apDialogue(it.npc) }
        }

        private suspend fun ProtectedAccess.apDialogue(npc: Npc) {
            val dest = npc.coords.translate(-2, 0)
            if (coords == dest) {
                startDialogue(npc)
                return
            }
            walk(coords)
            delay(1)
            playerMove(dest)
            startDialogue(npc)
        }

        private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
            startDialogue(npc) {
                chatNpcNoTurn(happy, "Welcome to the Sheared Ram. What can I do for you?")
            }
        }
    }

    private companion object {
        private val bartender =
            npcTypeFactory.create {
                name = "Bartender"
                size = 1
            }
    }
}
