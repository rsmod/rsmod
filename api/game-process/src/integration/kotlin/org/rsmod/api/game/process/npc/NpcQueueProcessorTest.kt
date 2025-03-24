package org.rsmod.api.game.process.npc

import org.junit.jupiter.api.Test
import org.rsmod.api.combat.commons.npc.queueCombatRetaliate
import org.rsmod.api.death.plugin.NpcDeathScript
import org.rsmod.api.hit.plugin.NpcHitScript
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.hit.HitType
import org.rsmod.map.CoordGrid

class NpcQueueProcessorTest {
    @Test
    fun GameTestState.`no death queue speed-up if last hit kills npc`() =
        runGameTest(NpcHitScript::class, NpcDeathScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 0, 0), man)
            player.placeAt(npc.coords.translateZ(1))

            npc.queueCombatRetaliate(player, delay = 1)
            npc.queueHit(player, delay = 1, HitType.Melee, damage = 5)
            npc.queueHit(player, delay = 1, HitType.Melee, damage = 7)

            // Though the npc has 0 hitpoints in this upcoming cycle, the death script is queued for
            // the cycle after it. This means the npc is not yet delayed from the death sequence.
            advance(ticks = 1)
            check(npc.hitpoints == 0)
            assertFalse(npc.isDelayed)

            // The death sequence will be invoked during this cycle; the npc should be delayed.
            advance(ticks = 1)
            assertTrue(npc.isDelayed)

            advanceUntil(npc::isNotDelayed)
            assertEquals(NpcMode.None, npc.mode)
            assertFalse(npc.isSlotAssigned)
        }

    @Test
    fun GameTestState.`trigger death queue speed-up if first hit kills npc`() =
        runGameTest(NpcHitScript::class, NpcDeathScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 0, 0), man)
            player.placeAt(npc.coords.translateZ(1))

            npc.queueCombatRetaliate(player, delay = 1)
            npc.queueHit(player, delay = 1, HitType.Melee, damage = 7)
            npc.queueHit(player, delay = 1, HitType.Melee, damage = 5)

            // On the next cycle (advance by 1), both hits (7 and 5 damage) are processed.
            // The 7 damage hit reduces the npc's hitpoints to 0 and queues the death sequence.
            // Because the death queue is added at the end of the list, and the queue that submitted
            // it was not the last in the list, the death queue ends up being processed in the same
            // cycle - after the remaining queues - instead of waiting for the next cycle.
            // This is the "speed-up" bug/quirk, where (death) queues are triggered sooner than they
            // normally would.
            advance(ticks = 1)
            check(npc.hitpoints == 0)
            check(!npc.hasMovedPreviousCycle)

            // If death queue script was triggered, the npc should now be delayed.
            assertTrue(npc.isDelayed)
            assertEquals(NpcMode.None, npc.mode)
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
