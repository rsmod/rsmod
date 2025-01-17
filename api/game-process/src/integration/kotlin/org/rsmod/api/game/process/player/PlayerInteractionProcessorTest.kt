package org.rsmod.api.game.process.player

import net.rsprot.protocol.game.outgoing.misc.player.MessageGame
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.testing.GameTestState
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.map.CoordGrid

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
        // Player should not receive any game message as the interaction should have been cancelled
        // since the loc was replaced.
        assertTrue(client.noneOf<MessageGame>())
    }
}
