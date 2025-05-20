package org.rsmod.content.areas.city.lumbridge

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_locs
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class LumbridgeScript
@Inject
constructor(private val locRepo: LocRepository, private val objRepo: ObjRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(lumbridge_locs.winch) { operateWinch() }
        onOpLoc1(lumbridge_locs.farmerfred_axe_logs) { takeAxeFromLogs(it.loc) }
    }

    private fun ProtectedAccess.operateWinch() {
        mes("It seems the winch is jammed - I can't move it.")
        soundSynth(synths.lever)
    }

    private suspend fun ProtectedAccess.takeAxeFromLogs(loc: BoundLocInfo) {
        if (content.woodcutting_axe in inv) {
            mesbox("You already have an axe.")
            return
        }

        if (inv.isFull()) {
            mesbox("You don't have enough room for the axe.")
            return
        }

        locRepo.change(loc, lumbridge_locs.farmerfred_logs, 50)
        invAddOrDrop(objRepo, objs.bronze_axe)
        soundSynth(synths.take_axe)
        objbox(objs.bronze_axe, 400, "You take a bronze axe from the logs.")
    }
}
