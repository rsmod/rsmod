package org.rsmod.content.other.generic.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.queues
import org.rsmod.api.random.GameRandom
import org.rsmod.api.script.onAiTimer
import org.rsmod.api.script.onNpcQueue
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Ducks @Inject constructor(private val random: GameRandom, private val npcList: NpcList) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onAiTimer(content.duck) { npc.duckTimer() }
        onAiTimer(content.duckling) { npc.ducklingTimer() }
        onNpcQueue(content.duck, queues.generic_queue1) { npc.duckSay() }
        onNpcQueue(content.duckling, queues.generic_queue1) { npc.ducklingSay() }
    }

    private fun Npc.duckTimer() {
        setNextTimer()
        queue(queues.generic_queue1, 0)
    }

    private fun Npc.duckSay() {
        say("Quack!")
        // TODO: sound_area
    }

    private fun Npc.ducklingTimer() {
        setNextTimer()
        // TODO: Duckling huntmode for nearby ducks.
        val duck = facingTarget(npcList)
        if (duck != null && duck.matches(content.duckling)) {
            duck.say("Quack?")
            duck.queue(queues.generic_queue1, 2)
            return
        }
    }

    private fun Npc.ducklingSay() {
        say("Eep!")
        // TODO: sound_area
    }

    private fun Npc.setNextTimer() {
        val next = random.of(50..100)
        aiTimer(next)
    }
}
