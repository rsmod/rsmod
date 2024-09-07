package org.rsmod.content.areas.city.lumbridge

import jakarta.inject.Inject
import org.rsmod.api.npc.spawn.ParsedNpcSpawner
import org.rsmod.api.obj.spawns.ParsedObjSpawner
import org.rsmod.api.utils.io.InputStreams.readAllBytes
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.plugin.scripts.SimplePluginScript

class LumbridgeScript
@Inject
constructor(private val npcSpawner: ParsedNpcSpawner, private val objSpawner: ParsedObjSpawner) :
    SimplePluginScript() {
    override fun ScriptContext.startUp() {
        addSpawns()
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
