package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Larry only appears in Lumbridge (by the sheep pen) during Cold War: `peng_multi_larry_lumb` is a
 * multinpc on varbit `peng_multi_larry` (3298) that resolves to nothing at 0 and to the visible
 * Larry at 1, so he stays hidden for players who have not reached that point of the quest.
 *
 * Talk-to follows "Searching for the penguins at Lumbridge" from
 * https://oldschool.runescape.wiki/w/Transcript:Cold_War. Tuxedo-time (op3) needs the clockwork
 * penguin suit and quest progress, so it is not implemented.
 */
class Larry : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.larry) { startDialogue(it.npc) { larryDialogue() } }
    }
}

private suspend fun Dialogue.larryDialogue() {
    chatPlayer(quiz, "So, we're in Lumbridge. Where are these penguins?")
    chatNpc(
        neutral,
        "I was chatting with a local farmer, and there is a rumour about a 'thing' that " +
            "lives around here. Maybe it's the penguin and these simple people don't " +
            "recognise them.",
    )
    chatPlayer(neutral, "Hmm, I suppose.")
}
