package org.rsmod.api.game.process.player

import org.junit.jupiter.api.Test
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.script.onApLoc1
import org.rsmod.api.script.onApNpc2
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.locTypeFactory
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.game.entity.Npc
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
     * This test scenario occurs when a player clicks a npc that is actively moving, but the route
     * waypoints were processed while the npc was within operable distance to the player.
     *
     * In this case, an empty route is detected during early [PlayerRouteRequestProcess] processing.
     * If the recalc request is cleared and the npc moves later in the same game cycle (which it
     * will, since it's actively moving), then the player will _not_ move, even though the npc is no
     * longer within reach.
     *
     * This unintended behavior causes the interaction model to send the `I can't reach that!`
     * message.
     */
    @Test
    fun GameTestState.`recalc request should not be cleared on empty route`() = runGameTest {
        val npc = spawnNpc(CoordGrid(0, 49, 50, 57, 62), sheep)
        player.teleport(CoordGrid(0, 49, 50, 58, 62))
        player.enableRun()
        player.opNpc1(npc)

        npc.walk(npc.coords.translateZ(-1))

        advance(ticks = 1)
        assertMessageNotSent(constants.dm_reach)
    }

    /**
     * Test that pathing-entity-target interactions recalculate their route based on the target's
     * latest position, but only during the last waypoint before reaching the initial destination.
     *
     * Scenario in this test where blue tiles are blocked and red tiles are the source and target:
     *
     * @see <img width=240 height=239 src="https://i.imgur.com/iniC8VQ.png">
     */
    @Test
    fun GameTestState.`recalculate route during last waypoint stretch`() = runGameTest {
        val base = CoordGrid(0, 1, 1, 0, 0)
        val sourceStart = base.translate(7, 6)
        val targetStart = base.translate(0, 7)
        val targetEnd = base.translate(7, 7)
        val target = spawnNpc(targetStart, dummy)
        for (dx in 1..6) {
            placeMapLoc(base.translate(dx, 1), crate)
            placeMapLoc(base.translate(dx, 1), crate)
            placeMapLoc(base.translate(dx, 7), crate)
        }
        for (dz in 1..7) {
            placeMapLoc(base.translate(1, dz), crate)
            placeMapLoc(base.translate(6, dz), crate)
        }

        player.coords = sourceStart
        player.varMoveSpeed = MoveSpeed.Walk
        check(player.routeDestination.isEmpty())
        check(target.coords == targetStart)
        player.opNpc1(target)

        // On the first process, our route request should have filled the player's route
        // destination, and should contain a total of 3 waypoints to reach the target.
        advance(ticks = 1)
        checkNotNull(player.interaction)
        assertEquals(3, player.routeDestination.size)

        // As the waypoints have been calculated, we can now move the target.
        target.coords = targetEnd

        // Iterate over the next 5 steps, asserting the player's coords and current
        // route destination waypoint count.
        repeat(4) { i ->
            // We take into account the first step taken, and then the steps that will be
            // taken after these process calls.
            val travelled = 1 + (i + 1)
            advance(ticks = 1)
            assertEquals(3, player.routeDestination.size)
            assertEquals(sourceStart.translateZ(-travelled), player.coords)
        }

        // On the 7th step, the player will have reached the first waypoint.
        advance(ticks = 1)
        assertEquals(2, player.routeDestination.size)
        assertEquals(base.translate(0, 0), player.routeDestination.peekFirst())
        assertEquals(base.translate(7, 0), player.coords)

        // Iterate over the next 6 steps, asserting the player's coords and current
        // route destination waypoint count.
        repeat(6) { i ->
            val travelled = i + 1
            advance(ticks = 1)
            assertEquals(2, player.routeDestination.size)
            assertEquals(sourceStart.translate(-travelled, -6), player.coords)
        }

        // After 6 steps, the destination from the initial route should still be valid.
        assertEquals(base.translate(0, 0), player.routeDestination.peekFirst())

        // Take the 7th step now, which should lead to the current waypoint being removed.
        advance(ticks = 1)
        assertEquals(1, player.routeDestination.size)
        assertEquals(base.translate(0, 0), player.coords)

        // Process the next step, which should now insert all new waypoints to reach the
        // updated target position.
        advance(ticks = 1)
        assertEquals(base.translate(7, 0), player.routeDestination.peekFirst())
        assertEquals(base.translate(7, 6), player.routeDestination.peekLast())
        assertEquals(2, player.routeDestination.size)

        // At this point, we _could_ assert that the rest of steps are completed... but that
        // is out of scope of this test. By now, we have made sure the path was recalculated
        // and only done so during the final waypoint "line."
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
        player.ifOpenMain(interfaces.hpbar_hud)

        // Replace original loc. This would normally cancel the interaction and movement, however,
        // this won't be the case while the player is busy.
        placeMapLoc(CoordGrid(0, 50, 50, 22, 43), locTypes.getValue(1274))

        advance(ticks = 1)
        // Ensure route is still ongoing and interaction has not been cancelled.
        assertTrue(route.isNotEmpty())
        assertNotNull(player.interaction)

        // Wait until the player's route has been fully consumed.
        advanceUntil(route::isEmpty) {
            "Could not reach destination: coords=${player.coords}, dest=$dest"
        }

        // Player should be still; however, the interaction is not cleared until the player is no
        // longer flagged as busy.
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

    /**
     * For interactions targeting pathing entities (`Player`s and `Npc`s), the interaction model is
     * responsible for recalculating the route after the initial interaction step and before
     * movement processing occurs each tick.
     *
     * This behavior is crucial for "persistent" ap interactions, such as ranged combat, where the
     * player needs to continuously adjust their position to remain within valid ap range. Without
     * this rerouting, if the target moves away, the player would not follow and would remain out of
     * range, causing the interaction to fail.
     */
    @Test
    fun GameTestState.`continuously re-route towards pathing entity that leaves valid ap range`() =
        runGameTest(ApRerouteTestScript::class) {
            val playerCoord = CoordGrid(0, 1, 1, 0, 0)
            allocZoneCollision(playerCoord)
            allocZoneCollision(playerCoord.translateX(8))
            allocZoneCollision(playerCoord.translateX(16))

            val npcCoord = playerCoord.translateX(5)
            val npc = spawnNpc(npcCoord, man)
            player.varMoveSpeed = MoveSpeed.Walk
            player.teleport(playerCoord)
            player.opNpc2(npc)

            advance(ticks = 1)
            assertMessageSent("Attack target.")

            // Move npc out of the default max ap range from player.
            npc.walk(npc.coords.translateX(3))

            // Player still within valid reroute script ap range.
            advance(ticks = 1)
            assertEquals(npcCoord.translateX(1), npc.coords)
            assertEquals(playerCoord, player.coords)
            assertMessageSent("Attack target.")

            // Player still within valid reroute script ap range.
            advance(ticks = 1)
            assertEquals(npcCoord.translateX(2), npc.coords)
            assertEquals(playerCoord, player.coords)
            assertMessageSent("Attack target.")

            // Player no longer within valid ap range according to reroute script and should begin
            // moving towards the new npc coords.
            advance(ticks = 1)
            assertEquals(npcCoord.translateX(3), npc.coords)
            assertEquals(playerCoord.translateX(1), player.coords)
            assertMessageSent("Attack target.")

            // Move the npc further away than max ap range.
            npc.coords = npc.coords.translateX(5)

            // Expecting npc to be 12 tiles away from player.
            check(npc.coords.chebyshevDistance(player.coords) == 12)

            advance(ticks = 1)
            assertEquals(playerCoord.translateX(2), player.coords)
            assertMessageNotSent("Attack target.")

            advance(ticks = 1)
            assertEquals(playerCoord.translateX(3), player.coords)
            assertMessageNotSent("Attack target.")

            advance(ticks = 1)
            assertEquals(playerCoord.translateX(4), player.coords)
            assertMessageNotSent("Attack target.")

            advance(ticks = 1)
            assertEquals(playerCoord.translateX(5), player.coords)
            assertMessageNotSent("Attack target.")

            // After walking for 5 cycles, the player should be within 7 tiles of the npc.
            // We should expect the ap script to execute.
            advance(ticks = 1)
            assertEquals(playerCoord.translateX(6), player.coords)
            assertMessageSent("Attack target.")
        }

    private class ApRerouteTestScript : PluginScript() {
        override fun ScriptContext.startUp() {
            onApNpc2(man) { combatAp(it.npc) }
        }

        private fun ProtectedAccess.combatAp(target: Npc) {
            if (isWithinApRange(target, distance = 7)) {
                mes("Attack target.")
                opNpc2(target)
            }
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

        private val sheep =
            npcTypeFactory.create {
                name = "Sheep"
                op[0] = "Shear"
                size = 1
            }

        private val man =
            npcTypeFactory.create {
                name = "Man"
                op[1] = "Attack"
                size = 1
                maxRange = 100
            }

        private val dummy =
            npcTypeFactory.create {
                name = "Dummy"
                op[0] = "Talk-to"
                size = 1
                wanderRange = 0
            }

        private val crate =
            locTypeFactory.create {
                name = "Crate"
                op[0] = "Search"
                width = 1
                length = 1
            }
    }
}
