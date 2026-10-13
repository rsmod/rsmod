package org.runerealm.content.areas.wilderness

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.player.vars.VarPlayerIntMapSetter
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.map.CoordGrid

class WildernessDitchTest {
    private val ditchCoords = CoordGrid(0, 50, 50, 20, 22)

    @Test
    fun GameTestState.`entering shows the warning and jumps north on confirm`() =
        runGameTest(WildernessDitch::class) {
            val ditch = placeDitch()
            val south = ditchCoords.translateZ(-1)
            player.teleport(south)
            player.opLoc1(ditch)
            advance(ticks = 2)
            assertTrue(player.ui.containsModal(ditch_interfaces.warning))

            player.resumePauseButton(ditch_components.universe, sub = 1)
            advance(ticks = 4)
            assertFalse(player.ui.containsModal(ditch_interfaces.warning))
            assertEquals(south.translateZ(3), player.coords)
        }

    @Test
    fun GameTestState.`declining the warning stays put`() =
        runGameTest(WildernessDitch::class) {
            val ditch = placeDitch()
            val south = ditchCoords.translateZ(-1)
            player.teleport(south)
            player.opLoc1(ditch)
            advance(ticks = 2)
            player.resumePauseButton(ditch_components.universe, sub = 0)
            advance(ticks = 4)
            assertEquals(south, player.coords)
        }

    @Test
    fun GameTestState.`disabled warning and leaving the wilderness skip the screen`() =
        runGameTest(WildernessDitch::class) {
            val ditch = placeDitch()
            VarPlayerIntMapSetter.set(player, ditch_varbits.ditch_warning, 7)
            val south = ditchCoords.translateZ(-1)
            player.teleport(south)
            player.opLoc1(ditch)
            advance(ticks = 4)
            assertEquals(south.translateZ(3), player.coords)

            VarPlayerIntMapSetter.set(player, ditch_varbits.ditch_warning, 0)
            val north = player.coords
            player.opLoc1(ditch)
            advance(ticks = 4)
            assertFalse(player.ui.containsModal(ditch_interfaces.warning))
            assertEquals(north.translateZ(-3), player.coords)
        }

    private fun GameTestScope.placeDitch(): BoundLocInfo {
        allocZoneCollision(ditchCoords)
        return placeMapLoc(ditchCoords, locTypes.getValue(ditch_locs.ditch.id))
    }
}
