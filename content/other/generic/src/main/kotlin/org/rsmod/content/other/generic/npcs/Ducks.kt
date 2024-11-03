package org.rsmod.content.other.generic.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.random.GameRandom
import org.rsmod.api.script.onAiTimer
import org.rsmod.api.script.onNpcQueue
import org.rsmod.api.type.refs.queue.QueueReferences
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Ducks @Inject constructor(private val random: GameRandom, private val npcList: NpcList) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onAiTimer(content.duck) { npc.duckTimer() }
        onAiTimer(content.duckling) { npc.ducklingTimer() }
        onNpcQueue(DuckQueues.duck_say) { npc.duckSay() }
        onNpcQueue(DuckQueues.duckling_say) { npc.ducklingSay() }
    }

    private fun Npc.duckTimer() {
        setNextTimer()
        queue(DuckQueues.duck_say, 0)
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
            duck.queue(DuckQueues.duckling_say, 2)
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

internal object DuckQueues : QueueReferences() {
    val duckling_say = find("duckling_say")
    val duck_say = find("duck_say")
}
