package org.rsmod.api.repo.region

import org.junit.jupiter.api.Test
import org.rsmod.api.npc.isValidTarget
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.game.entity.Npc
import org.rsmod.map.CoordGrid

class RegionRepositoryTest {
    @Test
    fun GameTestState.`delete respawning npcs on region clear`() = runGameTest {
        val region = createRegion(template)
        val npc = spawnNpc(region.southWest, man).apply { respawns = true }
        val deathCoord = region.southWest.translate(8, 0)

        check(region.slot != -1)
        check(npc.isValidTarget())

        // Place the player in the region to avoid it from being deleted.
        player.placeAt(region.southWest)
        // Move the npc one zone to the right to test respawning in different zones.
        npc.telejump(deathCoord)

        // Despawn the npc in the next cycle.
        advance(ticks = 1)
        npcRepo.despawn(npc, man.respawnRate)

        // Ensure npc is despawned, but can still be found through `findAll`.
        advance(ticks = 1)
        assertFalse(npc.isValidTarget())
        assertEquals(npc, npcRepo.findAll(deathCoord).single())

        // Move the player out of the region so it can be deleted.
        player.telejump(CoordGrid(0, 50, 50, 0, 0))

        // Delete inactive regions after the player has teleported out of the region.
        advance(ticks = 1)
        check(!npc.isValidTarget()) { "Expected npc to be mid-respawn. (npc=$npc)" }
        check(npc.isSlotAssigned) { "Expected npc to still have a slot assigned. (npc=$npc)" }
        removeInactiveRegions()

        // Once the region is deleted, the npc should have been deleted as well even though it was
        // "hidden" and in the middle of a respawn state.
        advance(ticks = 1)
        check(region.slot == -1) { "Expected region to be deleted. (region=$region)" }
        assertFalse(npc.isValidTarget())
        assertEquals(emptyList<Npc>(), npcRepo.findAll(deathCoord).toList())
        assertEquals(emptyList<Npc>(), npcRepo.findAll(npc.spawnCoords).toList())

        // Ensure npc does not respawn into the region.
        advance(ticks = man.respawnRate)
        assertFalse(npc.isValidTarget())
        assertEquals(emptyList<Npc>(), npcRepo.findAll(deathCoord).toList())
        assertEquals(emptyList<Npc>(), npcRepo.findAll(npc.spawnCoords).toList())
    }

    private companion object {
        private val template =
            RegionTemplate.create {
                copy(225, 562, 0) {
                    zoneWidth = 4
                    zoneLength = 4
                }
            }

        private val man =
            npcTypeFactory.create {
                name = "Man"
                op[1] = "Attack"
                size = 1
                hitpoints = 10
                wanderRange = 0
                respawnRate = 10
            }
    }
}
