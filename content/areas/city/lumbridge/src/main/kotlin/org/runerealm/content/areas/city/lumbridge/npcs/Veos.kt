package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Veos waits in The Sheared Ram until the player progresses X Marks the Spot. His Lumbridge npc
 * (`veos_lumbridge`) is a multinpc driven by `veos_lumbridge_vis`: `0` = hidden, `1` =
 * `veos_visible_amulet`, `2` = `veos_visible` (amuletless, before the quest).
 *
 * Op triggers are published under the base npc type's id, so handlers are registered on
 * `veos_lumbridge` rather than the visible variant.
 */
class Veos : PluginScript() {
    private var Player.veosLumbridgeVis by intVarBit(VeosVarBits.lumbridge_vis)

    override fun ScriptContext.startup() {
        // TODO(content): Derive from X Marks the Spot progress once the quest is implemented.
        onPlayerLogin { player.veosLumbridgeVis = VIS_NO_AMULET }
        onOpNpc1(lumbridge_npcs.veos) { veosDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.veosDialogue(npc: Npc) = startDialogue(npc) { veos() }

    // TODO(content): "Visited Great Kourend" variant and X Marks the Spot progress dialogue.
    private suspend fun Dialogue.veos() {
        chatNpc(happy, "Hello there.")
        val choice = choice3("Who are you?", 1, "I'm looking for a quest.", 2, "I have to go.", 3)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Who are you?")
                chatNpc(
                    happy,
                    "The name's Veos. I'm a treasure hunter from the wonderous Kingdom of " +
                        "Great Kourend.",
                )
                greatKourend()
            }
            2 -> {
                chatNpc(
                    neutral,
                    "Hmmm. Well now that you mention it, I could do with some help. The name's " +
                        "Veos. I'm a treasure hunter from the wonderous Kingdom of Great Kourend.",
                )
                greatKourend()
            }
            3 -> chatPlayer(neutral, "I have to go.")
        }
    }

    private suspend fun Dialogue.greatKourend() {
        chatPlayer(quiz, "Great Kourend? Where's that?")
        chatNpc(happy, "Across the sea to the far west. It is a truly magnificent place.")
        chatPlayer(quiz, "Interesting. So what brings you to Lumbridge?")
        chatNpc(
            neutral,
            "I'm here on a bit of a hunt. The hunt for treasure. Back in my home of Great " +
                "Kourend, I came across a scroll. I believe it will lead me to something of " +
                "great value.",
        )
        chatNpc(
            sad,
            "Alas, I've hit a bit of a blocker. The scroll has led me here but as I don't " +
                "really know the area, I'm not sure what to do next.",
        )
        val help = choice2("Can I help?", true, "Well good luck with it.", false)
        if (!help) {
            chatPlayer(neutral, "Well good luck with it.")
            return
        }
        chatNpc(
            neutral,
            "Hmmm. Maybe you can. You probably know this area better than I do. You might be " +
                "able to work the scroll out. I'll be able to reward you if you help.",
        )
        val accept = choice2("Sounds good, what should I do?", true, "I'm good thanks.", false)
        if (!accept) {
            chatPlayer(neutral, "I'm good thanks.")
            chatNpc(neutral, "Fair enough. I'll be here if you change your mind.")
            return
        }
        chatPlayer(happy, "Sounds good, what should I do?")
        mesbox("This quest is not available yet.")
    }

    private companion object {
        const val VIS_NO_AMULET = 2
    }
}

internal object VeosVarBits : VarBitReferences() {
    val lumbridge_vis = find("veos_lumbridge_vis")
}
