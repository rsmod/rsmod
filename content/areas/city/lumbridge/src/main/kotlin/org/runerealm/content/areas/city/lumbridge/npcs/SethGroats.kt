package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class SethGroats : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.seth_groats) { sethDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.sethDialogue(npc: Npc) =
        startDialogue(npc) { chatNpc(happy, "M'arnin'....going to milk me cowsies!") }
}
