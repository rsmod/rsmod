package org.rsmod.api.game.process.player

import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.script.onApLoc1
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.locTypeFactory
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class PlayerInteractionProcessorTest {
    @Test
    fun GameTestState.`cancel interaction due to verification condition`() = runGameTest {
        val loc = placeMapLoc(CoordGrid(0, 50, 50, 22, 43), locTypes.getValue(1276))

        val route = player.routeDestination
        player.moveSpeed = MoveSpeed.Walk
        player.teleport(loc.coords.translateX(-5))
        player.opLoc1(loc)

        advance(ticks = 1)
        val dest = route.lastOrNull()
        checkNotNull(dest)
        checkNotNull(player.interaction)

        // Replace original loc.
        placeMapLoc(CoordGrid(0, 50, 50, 22, 43), locTypes.getValue(1274))

        advance(ticks = 1)
        assertTrue(route.isEmpty())
        assertNull(player.interaction)
        assertNotEquals(player.coords, dest)
    }

    /**
     * This test ensures that interaction verification is skipped while the player is flagged as
     * busy.
     *
     * Though this test focuses on interactions with locs, the scenario applies to all interaction
     * types and their respective verification conditions. For example:
     * - When a target npc becomes delayed as a player is moving toward it for interaction.
     * - If the player is **not busy**, the interaction and movement will cancel.
     * - If the player is **busy**, they will continue moving toward the target. If the npc is no
     *   longer delayed by the time the player loses their delay, the interaction will proceed as
     *   normal.
     */
    @Test
    fun GameTestState.`skip interaction verification while player isBusy`() = runGameTest {
        val loc = placeMapLoc(CoordGrid(0, 50, 50, 22, 43), locTypes.getValue(1276))

        val route = player.routeDestination
        player.moveSpeed = MoveSpeed.Walk
        player.teleport(loc.coords.translateX(-5))
        player.opLoc1(loc)

        advance(ticks = 1)
        val dest = route.lastOrNull()
        checkNotNull(dest)
        checkNotNull(player.interaction)

        // Open a modal for `player.isBusy` condition to return true.
        player.ifOpenMain(interfaces.hp_hud)

        // Replace original loc. This would normally cancel the interaction and movement, however
        // this won't be the case while player is busy.
        placeMapLoc(CoordGrid(0, 50, 50, 22, 43), locTypes.getValue(1274))

        advance(ticks = 1)
        // Ensure route is still ongoing and interaction has not been cancelled.
        assertTrue(route.isNotEmpty())
        assertNotNull(player.interaction)

        // Wait until player's route has been fully consumed.
        advanceUntil(route::isEmpty) {
            "Could not reach destination: coords=${player.coords}, dest=$dest"
        }

        // Player should be still, however the interaction is not cleared until player is no longer
        // flagged as busy.
        assertTrue(route.isEmpty())
        assertNotNull(player.interaction)

        // Close the previously opened modal so the interaction can process.
        player.ifClose(eventBus)

        advance(ticks = 1)
        assertNull(player.interaction)
    }

    /**
     * When an ap interaction fails due to distance requirements, `apRangeCalled` should be set to
     * `true`. This signals to the engine that the player must continue movement along their initial
     * route to reach the required ap distance.
     */
    @Test
    fun GameTestState.`restore cleared waypoints on ap interaction when apRangeCalled is true`() =
        runGameTest(ApTestScript::class) {
            val loc = placeMapLoc(CoordGrid(0, 50, 50, 35, 28), signpost)
            val start = CoordGrid(0, 50, 50, 34, 20)
            player.enableWalk()
            player.teleport(start)
            player.opLoc1(loc)

            val expectedPath =
                listOf(
                    CoordGrid(0, 50, 50, 34, 21),
                    CoordGrid(0, 50, 50, 34, 22),
                    CoordGrid(0, 50, 50, 34, 23),
                )

            // The player should continue moving toward the destination each cycle until the ap
            // distance condition is met. Internally, the player's waypoints are cleared before
            // the ap script runs, but they are restored because `apRangeCalled` is set to `true`
            // during the script's distance verification.
            for (coord in expectedPath) {
                advance(ticks = 1)
                assertEquals(coord, player.coords)
            }

            // Once the player has traversed through the expected path, the respective script
            // should be invoked. In this test, it results in a game message being sent.
            assertMessageSent(ApTestScript.READ_MESSAGE)

            // After a successful ap interaction, the player's route waypoints should _not_ be
            // restored. This ensures they stop moving toward their previous destination.
            assertTrue(player.routeDestination.isEmpty())

            // Verify that waypoints remain cleared after one more cycle.
            advance(ticks = 1)
            assertTrue(player.routeDestination.isEmpty())
        }

    /**
     * When an ap interaction succeeds (the `isWithinDistance` condition passes), previously queued
     * waypoints should not be restored. This happens because `apRangeCalled` is **not** set to
     * `true`, as the ap distance condition was met without needing it.
     */
    @Test
    fun GameTestState.`clear waypoints on ap interaction when initial ap range condition passes`() =
        runGameTest(ApTestScript::class) {
            val loc = placeMapLoc(CoordGrid(0, 50, 50, 35, 28), signpost)
            val start = CoordGrid(0, 50, 50, 34, 25)
            player.enableWalk()
            player.teleport(start)
            player.opLoc1(loc)

            advance(ticks = 1)
            assertTrue(player.routeDestination.isEmpty())
            assertMessageSent(ApTestScript.READ_MESSAGE)
            assertEquals(start, player.coords)
        }

    private class ApTestScript : PluginScript() {
        override fun ScriptContext.startUp() {
            onApLoc1(signpost) { apReadSignpost(it.loc) }
            onOpLoc1(signpost) { readSignpost() }
        }

        private fun ProtectedAccess.apReadSignpost(loc: BoundLocInfo) {
            if (isWithinApRange(loc, distance = 5)) {
                readSignpost()
            }
        }

        private fun ProtectedAccess.readSignpost() {
            mes(READ_MESSAGE)
        }

        companion object {
            const val READ_MESSAGE = "You've read the signpost!"
        }
    }

    private companion object {
        private val signpost =
            locTypeFactory.create {
                name = "Signpost"
                op[0] = "Read"
                width = 2
                length = 2
            }
    }
}
