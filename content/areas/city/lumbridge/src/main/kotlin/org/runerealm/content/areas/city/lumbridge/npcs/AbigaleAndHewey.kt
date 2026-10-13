package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.mesanim.UnpackedMesAnimType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Misthalin Mystery start dialogue, following
 * https://oldschool.runescape.wiki/w/Transcript:Misthalin_Mystery ("Talking to Abigale or Hewey").
 * Both npcs share the same conversation, in which Abigale and Hewey each speak.
 *
 * `mistmyst_abigale` and `mistmyst_hewey` are multinpcs (visible variants `*_lum_vis` at varbit 0);
 * handlers are registered on the base types, which is what op events are keyed by.
 *
 * The quest itself is not implemented, so accepting it ends with a "not available" message.
 */
class AbigaleAndHewey : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.abigale) { startDialogue(it.npc, Speaker.Abigale) }
        onOpNpc1(lumbridge_npcs.hewey) { startDialogue(it.npc, Speaker.Hewey) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc, clicked: Speaker) =
        startDialogue(npc) { misthalinMysteryStart(clicked) }
}

private enum class Speaker {
    Abigale,
    Hewey,
}

private suspend fun Dialogue.misthalinMysteryStart(clicked: Speaker) {
    suspend fun abigale(mesanim: UnpackedMesAnimType, text: String) {
        if (clicked == Speaker.Abigale) {
            chatNpc(mesanim, text)
        } else {
            chatNpcSpecific("Abigale", misthalin_mystery_npcs.abigale_lum_vis, mesanim, text)
        }
    }

    abigale(worried, "Help! Help!")
    val accepted = choice2("Yes.", true, "No.", false, title = "Start the Misthalin Mystery quest?")
    if (!accepted) {
        chatPlayer(angry, "I'm not getting involved in this!")
        return
    }
    // Accepting starts the quest; the rest of the introduction (in which Hewey also speaks) and
    // the "talking again" dialogue only apply once the quest has been started.
    mesbox("This quest is not available yet.")
}

internal object MisthalinMysteryNpcs : NpcReferences() {
    val abigale_lum_vis = find("mistmyst_abigale_lum_vis")
}

private typealias misthalin_mystery_npcs = MisthalinMysteryNpcs
