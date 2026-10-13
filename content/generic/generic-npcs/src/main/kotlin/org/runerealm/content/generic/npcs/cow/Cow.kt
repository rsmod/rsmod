package org.runerealm.content.generic.npcs.cow

import jakarta.inject.Inject
import org.runerealm.api.config.refs.content
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.seqs
import org.runerealm.api.config.refs.synths
import org.runerealm.api.random.GameRandom
import org.runerealm.api.repo.world.WorldRepository
import org.runerealm.api.script.onAiTimer
import org.runerealm.api.script.onOpNpcU
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class Cow
@Inject
constructor(private val worldRepo: WorldRepository, private val random: GameRandom) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onAiTimer(content.cow) { npc.cowTimer() }
        onOpNpcU(content.cow) { mes("The cow doesn't want that.") }
        onOpNpcU(content.cow, objs.bucket_empty) {
            mes("Only dairy cows are suitable for milking.")
        }
    }

    private fun Npc.cowTimer() {
        val next = random.of(15..34)
        aiTimer(next)

        if (random.randomBoolean(4)) {
            sayFlavourText()
        }
    }

    private fun Npc.sayFlavourText() {
        worldRepo.soundArea(coords, synths.cow_atmospheric, radius = 10)
        say("Moo")
        anim(seqs.cow_update_graze)
    }
}
