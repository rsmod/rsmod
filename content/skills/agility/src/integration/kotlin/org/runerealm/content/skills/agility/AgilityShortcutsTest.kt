package org.runerealm.content.skills.agility

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.stats
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.loc.LocAngle
import org.runerealm.game.loc.LocShape
import org.runerealm.map.CoordGrid

class AgilityShortcutsTest {
    @Test
    fun GameTestState.`varrock fence jumps to the other side`() =
        runGameTest(AgilityShortcuts::class) {
            val fence =
                place(
                    "lumbridge_sc_fencejump",
                    CoordGrid(3240, 3335, 0),
                    LocShape.WallStraight,
                    LocAngle.South,
                )
            player.stats[stats.agility] = 13
            player.teleport(CoordGrid(3240, 3334, 0))
            player.opLoc1(fence)
            advance(ticks = 5)
            assertEquals(CoordGrid(3240, 3335, 0), player.coords)

            player.opLoc1(fence)
            advance(ticks = 5)
            assertEquals(CoordGrid(3240, 3334, 0), player.coords)
        }

    @Test
    fun GameTestState.`shortcut requires the agility level`() =
        runGameTest(AgilityShortcuts::class) {
            val fence =
                place(
                    "lumbridge_sc_fencejump",
                    CoordGrid(3240, 3335, 0),
                    LocShape.WallStraight,
                    LocAngle.South,
                )
            player.stats[stats.agility] = 12
            player.teleport(CoordGrid(3240, 3334, 0))
            player.opLoc1(fence)
            advance(ticks = 1)
            assertMessageSent("You need an Agility level of 13 to negotiate this obstacle.")
            assertEquals(CoordGrid(3240, 3334, 0), player.coords)
        }

    @Test
    fun GameTestState.`falador underwall tunnel crosses the wall`() =
        runGameTest(AgilityShortcuts::class) {
            val south =
                place(
                    "falador_sc_castlewall_south",
                    CoordGrid(2948, 3310, 0),
                    LocShape.CentrepieceStraight,
                    LocAngle.North,
                )
            player.stats[stats.agility] = 26
            player.teleport(CoordGrid(2948, 3309, 0))
            player.opLoc1(south)
            advance(ticks = 6)
            assertEquals(CoordGrid(2948, 3313, 0), player.coords)
        }

    @Test
    fun GameTestState.`one-way yanille rocks climb out of yanille`() =
        runGameTest(AgilityShortcuts::class) {
            val rocks =
                place(
                    "watchshortcut",
                    CoordGrid(2561, 3108, 0),
                    LocShape.CentrepieceStraight,
                    LocAngle.North,
                )
            player.stats[stats.agility] = 5
            val startXp = player.statMap.getXP(stats.agility)
            player.teleport(CoordGrid(2561, 3107, 0))
            player.opLoc1(rocks)
            advance(ticks = 5)
            assertEquals(CoordGrid(2561, 3109, 0), player.coords)
            assertEquals(25, player.statMap.getXP(stats.agility) - startXp)
        }

    @Test
    fun GameTestState.`wintertodt gap also needs firemaking`() =
        runGameTest(AgilityShortcuts::class) {
            val gap =
                place(
                    "wint_pillar_jump",
                    CoordGrid(1628, 4023, 0),
                    LocShape.CentrepieceStraight,
                    LocAngle.South,
                )
            player.stats[stats.agility] = 60
            player.stats[stats.firemaking] = 49
            player.teleport(CoordGrid(1627, 4023, 0))
            player.opLoc1(gap)
            advance(ticks = 1)
            assertMessageSent("You need a Firemaking level of 50 to negotiate this obstacle.")
        }

    private fun GameTestScope.place(
        internal: String,
        coords: CoordGrid,
        shape: LocShape,
        angle: LocAngle,
    ): BoundLocInfo {
        allocZoneCollision(coords)
        val type = findLocTypes { it.internalName == internal }.single()
        return placeMapLoc(coords, type, shape, angle)
    }
}
