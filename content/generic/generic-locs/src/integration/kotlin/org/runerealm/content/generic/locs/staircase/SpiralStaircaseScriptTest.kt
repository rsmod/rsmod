package org.runerealm.content.generic.locs.staircase

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.loc.LocType
import org.runerealm.map.CoordGrid

class SpiralStaircaseScriptTest {
    private val base = CoordGrid(0, 50, 50, 20, 20)

    @Test
    fun GameTestState.`only lumbridge castle's end floors have top-floor and bottom-floor`() =
        runGameTest {
            fun ops(type: LocType) = locTypes[type].op.toList()
            // Lumbridge Castle: ground floor, middle floor and top floor.
            assertEquals(
                listOf("Climb-up", "Top-floor", null, null, null),
                ops(staircase_locs.lumbridge_spiral_bottom),
            )
            assertEquals(
                listOf("Climb", "Climb-up", "Climb-down", null, null),
                ops(staircase_locs.spiral_middle),
            )
            assertEquals(
                listOf("Climb-down", "Bottom-floor", null, null, null),
                ops(staircase_locs.lumbridge_spiral_top),
            )
            // Every other spiral staircase only has the regular climb options.
            val climbUp = listOf("Climb-up", null, null, null, null)
            val climbDown = listOf("Climb-down", null, null, null, null)
            assertEquals(climbUp, ops(staircase_locs.spiral_bottom))
            assertEquals(climbUp, ops(staircase_locs.wooden_spiral_bottom))
            assertEquals(climbDown, ops(staircase_locs.spiral_top))
            assertEquals(climbDown, ops(staircase_locs.wooden_spiral_top))
            assertEquals(
                listOf("Climb", "Climb-up", "Climb-down", null, null),
                ops(staircase_locs.wooden_spiral_middle),
            )
        }

    @Test
    fun GameTestState.`lumbridge castle top-floor and bottom-floor skip the middle floor`() =
        runGameTest(SpiralStaircaseScript::class) {
            val (bottom, _, top) = placeLumbridgeCastleStack()

            player.teleport(base.translateX(-1))
            player.opLoc2(bottom)
            advance(ticks = 3)
            assertEquals(2, player.level)

            player.teleport(base.copy(level = 2).translateX(-1))
            player.opLoc2(top)
            advance(ticks = 6) // Top staircases force their approach side; walk around first.
            assertEquals(0, player.level)
        }

    @Test
    fun GameTestState.`lumbridge castle middle floor climbs one floor at a time`() =
        runGameTest(SpiralStaircaseScript::class) {
            val (_, middle) = placeLumbridgeCastleStack()

            player.teleport(base.copy(level = 1).translateX(-1))
            player.opLoc2(middle)
            advance(ticks = 3)
            assertEquals(2, player.level)

            player.teleport(base.copy(level = 1).translateX(-1))
            player.opLoc3(middle)
            advance(ticks = 3)
            assertEquals(0, player.level)

            // The middle floor has no Top-floor/Bottom-floor ops, so they do nothing.
            player.teleport(base.copy(level = 1).translateX(-1))
            player.opLoc4(middle)
            advance(ticks = 3)
            assertEquals(1, player.level)
            player.opLoc5(middle)
            advance(ticks = 3)
            assertEquals(1, player.level)
        }

    @Test
    fun GameTestState.`regular staircases have no top-floor op`() =
        runGameTest(SpiralStaircaseScript::class) {
            val bottom = placeStack(listOf(staircase_locs.spiral_bottom, staircase_locs.spiral_top))
            player.teleport(base.translateX(-1))
            player.opLoc2(bottom.first())
            advance(ticks = 3)
            assertEquals(0, player.level)

            player.opLoc1(bottom.first())
            advance(ticks = 3)
            assertEquals(1, player.level)
        }

    private fun GameTestScope.placeLumbridgeCastleStack(): List<BoundLocInfo> =
        placeStack(
            listOf(
                staircase_locs.lumbridge_spiral_bottom,
                staircase_locs.spiral_middle,
                staircase_locs.lumbridge_spiral_top,
            )
        )

    private fun GameTestScope.placeStack(types: List<LocType>): List<BoundLocInfo> =
        types.mapIndexed { level, type ->
            allocZoneCollision(base.copy(level = level))
            placeMapLoc(base.copy(level = level), locTypes.getValue(type.id))
        }
}
