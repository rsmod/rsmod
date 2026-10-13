package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The Wizard camped in Lumbridge Swamp. Dialogue follows
 * https://oldschool.runescape.wiki/w/Transcript:Wizard_(Lost_City) ("Standard dialogue before Lost
 * City").
 */
class LostCityWizard : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.wizard) {
            startDialogue(it.npc) {
                chatPlayer(quiz, "Why are all of you standing around here?")
                chatNpc(
                    laugh,
                    "Hahaha you dare talk to a mighty wizard such as myself? I bet you can't " +
                        "even cast windstrike yet amateur!",
                )
                chatPlayer(angry, "...You're an idiot.")
            }
        }
    }
}
