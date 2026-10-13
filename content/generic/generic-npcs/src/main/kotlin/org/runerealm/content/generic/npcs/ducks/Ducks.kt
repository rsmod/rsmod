package org.runerealm.content.generic.npcs.ducks

import jakarta.inject.Inject
import org.runerealm.api.config.refs.content
import org.runerealm.api.config.refs.queues
import org.runerealm.api.config.refs.synths
import org.runerealm.api.death.NpcDeath
import org.runerealm.api.hunt.NpcSearch
import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.api.random.GameRandom
import org.runerealm.api.repo.world.WorldRepository
import org.runerealm.api.script.onAiTimer
import org.runerealm.api.script.onNpcQueue
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.npc.NpcMode
import org.runerealm.game.type.hunt.HuntVis
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext
import org.runerealm.routefinder.collision.CollisionFlagMap

class Ducks
@Inject
constructor(
    private val random: GameRandom,
    private val death: NpcDeath,
    private val search: NpcSearch,
    private val worldRepo: WorldRepository,
    private val collision: CollisionFlagMap,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onAiTimer(content.duck) { npc.duckTimer() }
        onAiTimer(content.duckling) { npc.ducklingTimer() }
        onNpcQueue(content.duck, queues.death) { duckDeath() }
        onNpcQueue(content.duck, queues.generic_queue1) { duckSay() }
        onNpcQueue(content.duckling, queues.generic_queue1) { ducklingSay() }
        onNpcQueue(content.duckling, queues.generic_queue2) { ducklingMourn() }
    }

    private fun Npc.duckTimer() {
        setNextTimer()
        queue(queues.generic_queue1, 1)
    }

    private fun StandardNpcAccess.duckSay() {
        say("Quack!")
        worldRepo.soundArea(coords, synths.quack)
    }

    private suspend fun StandardNpcAccess.duckDeath() {
        val ducklings = search.findAll(coords, duck_npcs.duckling, 1, HuntVis.LineOfSight)
        for (duckling in ducklings) {
            duckling.queue(queues.generic_queue2, 2)
        }
        val duck = search.find(coords, duck_npcs.duck, 1, HuntVis.LineOfSight)
        duck?.queue(queues.generic_queue1, 2)
        death.deathNoDrops(this)
    }

    private fun Npc.ducklingTimer() {
        setNextTimer()
        val duck = search.find(coords, duck_npcs.duck_female, 10, HuntVis.LineOfSight) ?: return
        duck.say("Quack?")
        worldRepo.soundArea(coords, synths.quack)
        queue(queues.generic_queue1, 2)
    }

    private fun StandardNpcAccess.ducklingSay() {
        say("Eep!")
        worldRepo.soundArea(coords, synths.quack)
    }

    private fun StandardNpcAccess.ducklingMourn() {
        say("Cheep cheep!")
        if (npc.mode != NpcMode.None) {
            return
        }
        val coord = mapFindSquareNone(coords, minRadius = 1, maxRadius = 9, collision)
        coord?.let(::walk)
        queue(queues.generic_queue1, 2)
    }

    private fun Npc.setNextTimer() {
        val next = random.of(50..100)
        aiTimer(next)
    }
}
