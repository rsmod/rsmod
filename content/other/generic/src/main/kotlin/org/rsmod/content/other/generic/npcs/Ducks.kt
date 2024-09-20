package org.rsmod.content.other.generic.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.random.GameRandom
import org.rsmod.api.script.onAiTimer
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Ducks @Inject constructor(private val random: GameRandom, private val npcList: NpcList) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onAiTimer(content.duck) { npc.duckTimer() }
        onAiTimer(content.duckling) { npc.ducklingTimer() }
    }

    private fun Npc.duckTimer() {
        setNextTimer()
        // TODO: say should be an npc_queue
        say("Quack!")
        // TODO: sound_area
    }

    private fun Npc.ducklingTimer() {
        setNextTimer()
        // NOTE: Ducklings should hunt for nearby ducks.
        val duck = facingTarget(npcList)
        if (duck != null && duck.matches(content.duckling)) {
            duck.say("Quack?")
            // TODO: duck.sound_area
            // TODO: npc_queue: after 2 cycles duckling should say "Eep!"
            return
        }
    }

    private fun Npc.setNextTimer() {
        val next = random.of(50..100)
        aiTimer(next)
    }
}
