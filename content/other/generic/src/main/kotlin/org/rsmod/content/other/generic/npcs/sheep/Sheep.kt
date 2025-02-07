package org.rsmod.content.other.generic.npcs.sheep

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onNpcQueue
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpcU
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Sheep @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpNpc1(content.sheep) { shearSheep(it.npc) }
        onOpNpcU(content.sheep, objs.shears) { shearSheep(it.npc) }
        onNpcQueue(content.sheared_sheep, queues.generic_queue1) { npc.queueTransmogReset() }
        onNpcQueue(content.sheared_sheep, queues.generic_queue2) { npc.transmog(npc.type) }
    }

    private suspend fun ProtectedAccess.shearSheep(npc: Npc) {
        if (objs.shears !in inv) {
            mes("You need a set of shears to do this.")
            return
        }
        val sheared = npc.visType.param(params.next_npc_stage)
        anim(seqs.human_shearing)
        soundSynth(synths.shear_sheep, delay = 10)
        delay(1)
        faceEntitySquare(npc)
        npcPlayerFaceClose(npc)
        delay(1)
        npcTransmog(npc, sheared)
        mes("You get some wool.")
        invAddOrDrop(objRepo, objs.wool)
        npc.queue(queues.generic_queue1, cycles = 1)
    }

    private fun Npc.queueTransmogReset() {
        // TODO(content): sound_area(2053, loops = 1, range = 5) "sheep_atmospheric1"
        resetMode()
        say("Baa!")
        queue(queues.generic_queue2, cycles = 49)
    }
}
