package org.runerealm.content.interfaces.settings

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.ui.ifCloseOverlay
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.interfaces.settings.scripts.XpDropsScript
import org.runerealm.content.interfaces.settings.scripts.xpdrops_components
import org.runerealm.content.interfaces.settings.scripts.xpdrops_interfaces
import org.runerealm.content.interfaces.settings.scripts.xpdrops_varbits
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfButtonOp

class XpDropsTest {
    @Test
    fun GameTestState.`orb toggles xp drops and opens setup`() =
        runGameTest(XpDropsScript::class) {
            player.ifOpenOverlay(interfaces.orbs, components.toplevel_target_orbs)
            assertEquals(0, player.xpDropsEnabled)

            player.ifCloseOverlay(interfaces.xp_drops, eventBus)
            assertFalse(player.ui.containsOverlay(interfaces.xp_drops))

            player.ifButton(xpdrops_components.orb, op = IfButtonOp.Op1)
            advance()
            assertEquals(1, player.xpDropsEnabled)
            assertTrue(player.ui.containsOverlay(interfaces.xp_drops))

            player.ifButton(xpdrops_components.orb, op = IfButtonOp.Op1)
            advance()
            assertEquals(0, player.xpDropsEnabled)
            assertFalse(player.ui.containsOverlay(interfaces.xp_drops))

            player.ifButton(xpdrops_components.orb, op = IfButtonOp.Op2)
            advance()
            assertTrue(player.ui.containsModal(xpdrops_interfaces.setup))

            player.ifButton(xpdrops_components.size_options, comsub = 3)
            advance()
            assertEquals(2, player.xpDropsSize)

            player.ifButton(xpdrops_components.save)
            advance()
            assertFalse(player.ui.containsModal(xpdrops_interfaces.setup))
        }
}

private val Player.xpDropsEnabled by intVarBit(xpdrops_varbits.enabled)
private val Player.xpDropsSize by intVarBit(xpdrops_varbits.size)
