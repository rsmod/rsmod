package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The [Sheep (penguins)](https://oldschool.runescape.wiki/w/Sheep_(penguins)) in Fred the Farmer's
 * pen: two penguins in a sheep costume ("The Thing"). It cannot be sheared, and only talks during
 * Cold War, which is not implemented.
 *
 * @see <a href="https://oldschool.runescape.wiki/w/Transcript:Sheep_(penguins)">Transcript</a>
 */
class PenguinSheep : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.penguin_sheep) { shear() }
        onOpNpc3(lumbridge_npcs.penguin_sheep) { talkTo(it.npc) }
    }

    private fun ProtectedAccess.shear() {
        mes("The... whatever it is... manages to get away from you!")
    }

    private suspend fun ProtectedAccess.talkTo(npc: Npc) {
        startDialogue(npc) {
            chatPlayer(confused, "That's a sheep...I think. I can't talk to sheep.")
        }
    }
}
