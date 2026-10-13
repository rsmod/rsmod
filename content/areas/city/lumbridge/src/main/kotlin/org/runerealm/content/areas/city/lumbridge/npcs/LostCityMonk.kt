package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The Monk camped in Lumbridge Swamp. Dialogue follows
 * https://oldschool.runescape.wiki/w/Transcript:Monk_(Lost_City) ("Standard dialogue before Lost
 * City").
 */
class LostCityMonk : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.monk) {
            startDialogue(it.npc) {
                chatPlayer(quiz, "Why are all of you standing around here?")
                chatNpc(angry, "None of your business. Get lost.")
            }
        }
    }
}
