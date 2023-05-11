package org.rsmod.plugins.api.move

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.zone.ZoneKey
import org.rsmod.plugins.api.net.downstream.RebuildNormal
import org.rsmod.plugins.api.refreshBuildArea
import org.rsmod.plugins.api.toBuildArea
import org.rsmod.plugins.testing.GameTestState
import org.rsmod.plugins.testing.assertions.verify
import org.rsmod.plugins.testing.assertions.verifyNull

class PostMovementProcessTest {

    @Test
    fun GameTestState.testBuildAreaRebuild() = runGameTest {
        val process = PostMovementProcess(playerList, xteaRepository)
        val start = Coordinates(3200, 3200)
        val dest = start.translateX(-40)
        withPlayer {
            coords = Coordinates(3200, 3200)
            refreshBuildArea(coords)
            // Test player does not have REBUILD_NORMAL in downstream list.
            verifyNull(RebuildNormal::class)
            // Empty executes to make sure player isn't affected inappropriately.
            repeat(8) { process.execute() }
            // Test player still does not have this packet queued even after process executes.
            verifyNull(RebuildNormal::class)
            // Test player's build area is rebuilt when applicable.
            coords = dest
            process.execute()
            verify(RebuildNormal::class) { it.zone == ZoneKey.from(dest) }
            assertEquals(dest.toBuildArea(), buildArea)
            // Test player's build area is _not_ rebuild further.
            downstream.clear()
            process.execute()
            verifyNull(RebuildNormal::class)
        }
    }
}
