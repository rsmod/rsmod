package org.rsmod.content.generic.locs.coops

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class ChickenCoop : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.chicken_coop) { searchCoop() }
    }

    private suspend fun ProtectedAccess.searchCoop() {
        arriveDelay()
        val add = invAdd(inv, objs.egg)
        if (add.failure) {
            mes("You search the coop and find an egg but you don't have room to take it.")
            return
        }
        mes("You search the coop and find an egg.")
        anim(seqs.human_pickuptable)
        soundSynth(synths.pick)
    }
}
