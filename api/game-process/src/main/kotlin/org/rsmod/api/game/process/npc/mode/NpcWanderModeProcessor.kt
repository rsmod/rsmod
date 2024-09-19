package org.rsmod.api.game.process.npc.mode

import jakarta.inject.Inject
import org.rsmod.api.random.CoreRandom
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Npc
import org.rsmod.pathfinder.collision.CollisionFlagMap

public class NpcWanderModeProcessor
@Inject
constructor(@CoreRandom private val random: GameRandom, private val collision: CollisionFlagMap) {
    private val Npc.shouldRespawn: Boolean
        get() = wanderIdleCycles >= RESPAWN_IDLE_REQUIREMENT

    public fun process(npc: Npc) {
        val wanderRange = npc.wanderRange
        if (wanderRange > 0) {
            npc.updateIdleCycles()
            npc.processWanderRange(wanderRange)
        } else {
            npc.updateIdleCycles()
            npc.processSpawnRetreat()
        }
    }

    private fun Npc.updateIdleCycles() {
        if (hasMovedPreviousCycle) {
            wanderIdleCycles = 0
        } else {
            wanderIdleCycles++
        }
    }

    private fun Npc.processWanderRange(wanderRadius: Int) {
        if (shouldRespawn) {
            respawn()
            return
        }
        if (random.randomBoolean(8)) {
            val dest = random.of(spawnCoords, wanderRadius)
            walk(dest)
        }
    }

    private fun Npc.processSpawnRetreat() {
        if (coords == spawnCoords) {
            return
        }
        if (shouldRespawn) {
            respawn()
            return
        }
        walk(spawnCoords)
    }

    private fun Npc.respawn() {
        teleport(collision, spawnCoords)
    }

    public companion object {
        public const val RESPAWN_IDLE_REQUIREMENT: Int = 500
    }
}
