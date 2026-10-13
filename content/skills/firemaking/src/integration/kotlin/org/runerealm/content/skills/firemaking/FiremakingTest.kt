package org.runerealm.content.skills.firemaking

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.stats
import org.runerealm.api.inv.HeldUOpScript
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.inv.InvObj
import org.runerealm.map.CoordGrid

@Execution(ExecutionMode.SAME_THREAD)
class FiremakingTest {
    private val start = CoordGrid(0, 50, 50, 22, 18)

    @Test
    fun GameTestState.`tinderbox on logs lights a fire and steps west`() =
        runGameTest(Firemaking::class, HeldUOpScript::class) {
            allocZoneCollision(start)
            player.teleport(start)
            player.observerUUID = 1 // Normally assigned on login; required to own ground objs.
            player.stats[stats.firemaking] = 1
            player.inv[0] = InvObj(firemaking_objs.tinderbox)
            player.inv[1] = InvObj(firemaking_objs.logs)
            random.next = 0 // Succeed on the first attempt.

            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertMessageSent("You attempt to light the logs.")
            assertNull(player.inv[1])

            advance(ticks = 4)
            assertMessageSent("The fire catches and the logs begin to burn.")
            assertEquals(40, player.statMap.getXP(stats.firemaking))
            assertNotNull(findLoc(start, FiremakingLocs.fire))

            advance(ticks = 2)
            assertEquals(start.translateX(-1), player.coords)
        }

    @Test
    fun GameTestState.`firemaking level is required for the logs`() =
        runGameTest(Firemaking::class, HeldUOpScript::class) {
            allocZoneCollision(start)
            player.teleport(start)
            player.stats[stats.firemaking] = 14
            player.inv[0] = InvObj(firemaking_objs.tinderbox)
            player.inv[1] = InvObj(firemaking_objs.oak_logs)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertMessageSent("You need a Firemaking level of 15 to burn these logs.")
            assertEquals(firemaking_objs.oak_logs.id, player.inv[1]?.id)
        }
}
