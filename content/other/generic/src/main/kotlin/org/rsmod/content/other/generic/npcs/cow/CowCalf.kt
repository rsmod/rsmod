package org.rsmod.content.other.generic.npcs.cow

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.script.onAiTimer
import org.rsmod.api.script.onOpNpcU
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CowCalf
@Inject
constructor(private val worldRepo: WorldRepository, private val random: GameRandom) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onAiTimer(content.cow_calf) { npc.calfTimer() }
        onOpNpcU(content.cow_calf) { mes("The calf doesn't want that.") }
        onOpNpcU(content.cow_calf, objs.bucket_empty) { mes("Calves are too young to be milked.") }
    }

    private fun Npc.calfTimer() {
        val next = random.of(15..34)
        aiTimer(next)

        if (random.randomBoolean(4)) {
            sayFlavourText()
        }
    }

    private fun Npc.sayFlavourText() {
        worldRepo.soundArea(coords, synths.cow_atmospheric, radius = 10)
        say("Moo")
        anim(seqs.cow_eat_grass)
    }
}
