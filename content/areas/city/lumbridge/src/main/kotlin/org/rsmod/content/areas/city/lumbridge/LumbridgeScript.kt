package org.rsmod.content.areas.city.lumbridge

import jakarta.inject.Inject
import org.rsmod.api.config.refs.synths
import org.rsmod.api.npc.spawn.ParsedNpcSpawner
import org.rsmod.api.obj.spawns.ParsedObjSpawner
import org.rsmod.api.player.mes
import org.rsmod.api.player.soundSynth
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.utils.io.InputStreams.readAllBytes
import org.rsmod.content.areas.city.lumbridge.locs.LumbridgeLocs
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class LumbridgeScript
@Inject
constructor(private val npcSpawner: ParsedNpcSpawner, private val objSpawner: ParsedObjSpawner) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(LumbridgeLocs.winch) { player.operateWinch() }
        addSpawns()
    }

    private fun Player.operateWinch() {
        mes("It seems the winch is jammed - I can't move it.")
        soundSynth(synths.lever)
    }

    private fun addSpawns() {
        val npcSpawns = npcSpawnGetter()
        val objSpawns = objSpawnGetter()
        npcSpawner.addStaticSpawns(npcSpawns)
        objSpawner.addStaticSpawns(objSpawns)
    }

    private fun npcSpawnGetter(): () -> ByteArray = { readAllBytes<LumbridgeScript>("npcs.toml") }

    private fun objSpawnGetter(): () -> ByteArray = { readAllBytes<LumbridgeScript>("objs.toml") }
}
