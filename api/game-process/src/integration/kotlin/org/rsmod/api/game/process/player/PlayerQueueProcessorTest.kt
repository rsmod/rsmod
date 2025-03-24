package org.rsmod.api.game.process.player

import org.junit.jupiter.api.Test
import org.rsmod.api.combat.commons.npc.queueCombatRetaliate
import org.rsmod.api.combat.commons.player.queueCombatRetaliate
import org.rsmod.api.config.refs.stats
import org.rsmod.api.death.plugin.PlayerDeathScript
import org.rsmod.api.hit.plugin.PlayerHitScript
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.hit.HitType
import org.rsmod.map.CoordGrid

class PlayerQueueProcessorTest {
    @Test
    fun GameTestState.`no death queue speed-up if last hit kills player`() =
        runGameTest(PlayerHitScript::class, PlayerDeathScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 0, 0), man)
            player.placeAt(npc.coords.translateZ(1))
            player.setCurrentLevel(stats.hitpoints, 7)

            player.queueCombatRetaliate(npc, delay = 1)
            player.queueHit(npc, delay = 1, HitType.Typeless, damage = 5)
            player.queueHit(npc, delay = 1, HitType.Typeless, damage = 7)

            advance(ticks = 1)
            check(player.hitpoints == 0)
            assertFalse(player.isDelayed)

            advance(ticks = 1)
            assertTrue(player.isDelayed)
        }

    @Test
    fun GameTestState.`trigger death queue speed-up if first hit kills player`() =
        runGameTest(PlayerHitScript::class, PlayerDeathScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 0, 0), man)
            player.placeAt(npc.coords.translateZ(1))
            player.setCurrentLevel(stats.hitpoints, 7)

            player.queueCombatRetaliate(npc, delay = 1)
            player.queueHit(npc, delay = 1, HitType.Typeless, damage = 7)
            player.queueHit(npc, delay = 1, HitType.Typeless, damage = 5)

            check(player.isNotDelayed)

            // On the next cycle (advance by 1), both hits (7 and 5 damage) are processed.
            // The 7 damage hit reduces the player's hitpoints to 0 and queues the death sequence.
            // Because the death queue is added at the end of the list, and the queue that submitted
            // it was not the last in the list, the death queue ends up being processed in the same
            // cycle - after the remaining queues - instead of waiting for the next cycle.
            // This is the "speed-up" bug/quirk, where (death) queues are triggered sooner than they
            // normally would.
            advance(ticks = 1)
            check(player.hitpoints == 0)
            check(!player.hasMovedPreviousCycle)

            // If death queue script was triggered, the player should now be delayed.
            assertTrue(player.isDelayed)
        }

    private companion object {
        private val man =
            npcTypeFactory.create {
                name = "Man"
                op[1] = "Attack"
                size = 1
                hitpoints = 7
                defaultMode = NpcMode.None
            }
    }
}
