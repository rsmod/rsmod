package org.runerealm.content.skills.magic.teleports

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.vars.VarPlayerIntMapSetter
import org.runerealm.api.testing.GameTestState
import org.runerealm.map.CoordGrid

class HomeTeleportTest {
    private val start = CoordGrid(3200, 3200, 0)

    @Test
    fun GameTestState.`home teleport takes 24 ticks and has no cooldown`() =
        runGameTest(HomeTeleport::class) {
            player.teleport(start)
            player.ifOpenOverlay(interfaces.magic_spellbook, components.toplevel_target_side6)
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 1) // The click is processed and the cast begins.
            advance(ticks = 23)
            assertEquals(start, player.coords)
            advance(ticks = 1)
            assertEquals(LUMBRIDGE_HOME, player.coords)

            // No cooldown: it can be cast again straight away.
            player.teleport(start)
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 25)
            assertEquals(LUMBRIDGE_HOME, player.coords)
        }

    @Test
    fun GameTestState.`cannot cast during combat`() =
        runGameTest(HomeTeleport::class) {
            player.teleport(start)
            player.ifOpenOverlay(interfaces.magic_spellbook, components.toplevel_target_side6)
            advance(ticks = 5)
            VarPlayerIntMapSetter.set(player, varps.lastcombat, mapClock.cycle)
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 1)
            assertMessageSent(RECENT_COMBAT_MESSAGE)
            advance(ticks = 30)
            assertEquals(start, player.coords)
        }

    @Test
    fun GameTestState.`combat interrupts the cast`() =
        runGameTest(HomeTeleport::class) {
            player.teleport(start)
            player.ifOpenOverlay(interfaces.magic_spellbook, components.toplevel_target_side6)
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 10)
            VarPlayerIntMapSetter.set(player, varps.lastcombat, mapClock.cycle)
            advance(ticks = 20)
            assertNotEquals(LUMBRIDGE_HOME, player.coords)
        }

    @Test
    fun GameTestState.`walking away interrupts the cast`() =
        runGameTest(HomeTeleport::class) {
            player.teleport(start)
            player.ifOpenOverlay(interfaces.magic_spellbook, components.toplevel_target_side6)
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 10)
            val walkDest = start.translateX(3)
            player.moveGameClick(walkDest)
            advance(ticks = 30)
            assertNotEquals(LUMBRIDGE_HOME, player.coords)
            assertEquals(walkDest, player.coords)
        }

    @Test
    fun GameTestState.`opening an interface interrupts the cast`() =
        runGameTest(HomeTeleport::class) {
            player.teleport(start)
            player.ifOpenOverlay(interfaces.magic_spellbook, components.toplevel_target_side6)
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 5)
            // Any interface open/close path clears pending weak actions (`clearpendingaction`).
            player.ifClose()
            advance(ticks = 30)
            assertEquals(start, player.coords)
        }

    @Test
    fun GameTestState.`cannot cast within 10 seconds of combat`() =
        runGameTest(HomeTeleport::class) {
            player.teleport(start)
            player.ifOpenOverlay(interfaces.magic_spellbook, components.toplevel_target_side6)
            advance(ticks = 5)
            VarPlayerIntMapSetter.set(player, varps.lastcombat, mapClock.cycle)
            advance(ticks = 12)
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 1)
            assertMessageSent(RECENT_COMBAT_MESSAGE)
            advance(ticks = 30)
            assertEquals(start, player.coords)

            // Once 10 seconds (16 ticks) have passed it may be cast.
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 25)
            assertEquals(LUMBRIDGE_HOME, player.coords)
        }

    @Test
    fun GameTestState.`cannot cast above level 20 wilderness`() =
        runGameTest(HomeTeleport::class) {
            val deep = CoordGrid(3100, 3800, 0)
            player.teleport(deep)
            player.ifOpenOverlay(interfaces.magic_spellbook, components.toplevel_target_side6)
            player.ifButton(home_teleport_components.standard)
            advance(ticks = 30)
            assertEquals(deep, player.coords)
        }
}
