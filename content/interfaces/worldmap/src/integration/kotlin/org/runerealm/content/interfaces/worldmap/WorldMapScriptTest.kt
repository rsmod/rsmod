package org.runerealm.content.interfaces.worldmap

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.ui.UserInterface

class WorldMapScriptTest {
    @Test
    fun GameTestState.`floating world map opens in the floater and closes`() =
        runGameTest(WorldMapScript::class) {
            player.ifOpenOverlay(interfaces.orbs, components.toplevel_target_orbs)

            player.ifButton(worldmap_components.orb, op = IfButtonOp.Op2)
            advance(ticks = 1)
            assertTrue(player.ui.containsOverlay(worldmap_interfaces.worldmap))
            assertEquals(
                worldmap_interfaces.worldmap.id,
                player.ui.getOverlayOrNull(components.toplevel_target_floater)?.packed,
            )
            // The 8 resize/move handles are draggable and the map toggles transmit their op.
            assertTrue(player.ui.hasEvent(worldmap_components.content, 7, IfEvent.Depth1))
            assertTrue(player.ui.hasEvent(worldmap_components.toggles, 4, IfEvent.Op1))

            player.ifButton(worldmap_components.close)
            advance(ticks = 1)
            assertFalse(player.ui.containsOverlay(worldmap_interfaces.worldmap))

            // Op2 becomes "Close Floating panel" while the map is open.
            player.ifButton(worldmap_components.orb, op = IfButtonOp.Op2)
            advance(ticks = 1)
            player.ifButton(worldmap_components.orb, op = IfButtonOp.Op2)
            advance(ticks = 1)
            assertFalse(player.ui.containsOverlay(worldmap_interfaces.worldmap))
        }

    @Test
    fun GameTestState.`fullscreen world map switches toplevel and restores it on close`() =
        runGameTest(WorldMapScript::class) {
            val gameframe = interfaces.toplevel_osrs_stretch
            player.gameframeTopLevel = gameframe.id
            player.ui.topLevel = UserInterface(gameframe.id)
            player.ifOpenOverlay(interfaces.orbs, components.toplevel_target_orbs)

            player.ifButton(worldmap_components.orb, op = IfButtonOp.Op3)
            advance(ticks = 1)
            assertEquals(UserInterface(worldmap_interfaces.toplevel_display.id), player.ui.topLevel)
            assertEquals(
                worldmap_interfaces.worldmap.id,
                player.ui.getOverlayOrNull(worldmap_components.displaymodal)?.packed,
            )

            player.ifButton(worldmap_components.esckey)
            advance(ticks = 1)
            assertFalse(player.ui.containsOverlay(worldmap_interfaces.worldmap))
            assertEquals(UserInterface(gameframe.id), player.ui.topLevel)
        }

    @Test
    fun GameTestState.`map toggles and key panel are saved`() =
        runGameTest(WorldMapScript::class) {
            player.ifOpenOverlay(interfaces.orbs, components.toplevel_target_orbs)
            player.ifButton(worldmap_components.orb, op = IfButtonOp.Op2)
            advance(ticks = 1)

            player.ifButton(worldmap_components.toggles, comsub = 2)
            advance(ticks = 1)
            assertEquals(1 shl 2, player.vars[worldmap_varbits.toggles])

            player.ifButton(worldmap_components.toggles, comsub = 2)
            advance(ticks = 1)
            assertEquals(0, player.vars[worldmap_varbits.toggles])

            player.ifButton(worldmap_components.key_toggle)
            advance(ticks = 1)
            assertEquals(1, player.vars[worldmap_varbits.disable_key])
        }
}

private var Player.gameframeTopLevel by intVarBit(varbits.gameframe_toplevel)
