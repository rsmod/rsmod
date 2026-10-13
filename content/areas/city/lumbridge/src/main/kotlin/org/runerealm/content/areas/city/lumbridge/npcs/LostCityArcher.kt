package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The Archer camped in Lumbridge Swamp. Dialogue follows
 * https://oldschool.runescape.wiki/w/Transcript:Archer_(Lost_City) ("Standard dialogue before Lost
 * City").
 */
class LostCityArcher : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.archer) {
            startDialogue(it.npc) {
                chatPlayer(quiz, "Why are you guys hanging around here?")
                chatNpc(angry, "(ahem)...'Guys'?")
                chatPlayer(
                    worried,
                    "Uh... yeah, sorry about that. Why are you all standing around out here?",
                )
                chatNpc(neutral, "Well, that's really none of your business.")
            }
        }
    }
}
