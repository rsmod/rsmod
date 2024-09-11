package org.rsmod.api.game.process.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.entityFactory
import org.rsmod.api.testing.factory.npcFactory
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.game.map.collision.add
import org.rsmod.game.map.collision.get
import org.rsmod.game.map.collision.set
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.pathfinder.flag.CollisionFlag

class NpcMovementProcessorTest {
    /**
     * Tests that an NPC re-applies its collision flag underneath its current position when it is
     * unable to reach its next route tile due to a blockage (e.g., blocked by a loc).
     *
     * This ensures that if the NPC's path is blocked, and it cannot proceed, it continuously
     * re-applies the collision flag under its current position every cycle, even if no movement is
     * possible.
     *
     * For example, if a player temporarily occupies the NPC's tile and then moves away, the NPC
     * should restore its collision flag as long as it has an active route destination.
     */
    @Test
    fun GameTestState.`reapply collision flag without steps but valid route destination`() =
        runGameTest {
            withCollisionState {
                val targetCoords = CoordGrid(0, 0, 0, 0, 7)
                val startCoords = CoordGrid(0, 0, 0, 0, 0)
                val type = npcTypeFactory.create()
                val npc = npcFactory.create(type, startCoords)
                val target = entityFactory.createAvatar(targetCoords)
                it.allocateCollision(ZoneKey(0, 0, 0))
                it.collision.add(CoordGrid(0, 0, 0, 0, 3), CollisionFlag.LOC)
                withNpc(npc) {
                    val movement = NpcMovementProcessor(it.collision, it.stepFactory)
                    check(target.coords == targetCoords)
                    check(routeDestination.isEmpty())

                    fun process() {
                        previousCoords = coords
                        currentMapClock++
                        movement.process(this)
                    }

                    walk(target)

                    // Walk until npc reaches loc.
                    repeat(2) { index ->
                        val steps = 1 + index
                        process()
                        assertEquals(startCoords.translateZ(steps), coords)
                        assertEquals(targetCoords.translateZ(-1), routeDestination.peekLast())
                    }

                    // Cache current coords to verify npc does not move.
                    val stopCoords = coords

                    // The npc should be blocked by the loc collision and unable to move.
                    process()
                    assertEquals(stopCoords, coords)
                    // The collision flag underneath the npc should've been set by said npc.
                    assertEquals(CollisionFlag.BLOCK_NPCS, it.collision[coords])

                    // Manually remove the collision flag under the npc.
                    it.collision[coords] = 0
                    check(it.collision[coords] == 0)

                    // After one call to process, the npc should have re-applied its collision flag
                    // and at this point should have also stayed on the same coords right behind
                    // the loc blocking its path.
                    process()
                    assertEquals(CollisionFlag.BLOCK_NPCS, it.collision[coords])
                    assertEquals(stopCoords, coords)
                }
            }
        }
}
