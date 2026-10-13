package org.runerealm.content.generic.locs.coops

import org.runerealm.api.config.refs.content
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.seqs
import org.runerealm.api.config.refs.synths
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpLoc1
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class ChickenCoop : PluginScript() {
    override fun ScriptContext.startup() {
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
