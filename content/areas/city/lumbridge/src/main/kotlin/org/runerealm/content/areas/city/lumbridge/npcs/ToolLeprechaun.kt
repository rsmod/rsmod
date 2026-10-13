package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class ToolLeprechaun : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.tool_leprechaun) { leprechaunDialogue(it.npc) }
        onOpNpc3(lumbridge_npcs.tool_leprechaun) { openToolStore() }
    }

    // TODO(content): Tool storage (deposit/withdraw) and noting produce are not implemented;
    //  this only opens the store interface.
    private fun ProtectedAccess.openToolStore() {
        ifOpenMainSidePair(ToolLeprechaunInterfaces.farming_tools, ToolLeprechaunInterfaces.side)
    }

    private suspend fun ProtectedAccess.leprechaunDialogue(npc: Npc) {
        var openStore = false
        startDialogue(npc) { openStore = toolLeprechaun() }
        if (openStore) {
            openToolStore()
        }
    }

    /** @return `true` if the player asked to use the store. */
    private suspend fun Dialogue.toolLeprechaun(): Boolean {
        chatNpc(
            happy,
            "Ah, 'tis a foine day to be sure! Were yez wantin' me to store yer tools, or " +
                "maybe ye might be wantin' yer stuff back from me?",
        )
        while (true) {
            val choice =
                choice4(
                    "Yes please.",
                    1,
                    "What can you store?",
                    2,
                    "What do you do with the tools you're storing?",
                    3,
                    "No thanks, I'll keep hold of my stuff.",
                    4,
                )
            when (choice) {
                1 -> return true
                2 -> whatCanYouStore()
                3 -> whatDoYouDo()
                4 -> {
                    chatPlayer(neutral, "No thanks, I'll keep hold of my stuff.")
                    chatNpc(
                        laugh,
                        "Ye must be dafter than ye look if ye likes luggin' yer tools " +
                            "everywhere ye goes!",
                    )
                    return false
                }
            }
        }
    }

    private suspend fun Dialogue.whatCanYouStore() {
        chatPlayer(quiz, "What can you store?")
        chatNpc(
            happy,
            "We'll hold onto yer rake, yer seed dibber, yer spade, yer secateurs, yer " +
                "waterin' can and yer trowel - but mind it's not one of them fancy trowels " +
                "only archaeologists use!",
        )
        chatNpc(
            happy,
            "We'll take a few buckets off yer hands too, and even yer compost, supercompost " +
                "an' ultracompost! Also plant cure vials.",
        )
        chatNpc(
            happy,
            "Aside from that, if ye hands me yer farming produce, I can mebbe change it " +
                "into banknotes for ye.",
        )
        chatNpc(quiz, "So... do ye want to be using the store?")
    }

    private suspend fun Dialogue.whatDoYouDo() {
        chatPlayer(
            quiz,
            "What do you do with the tools you're storing? They can't possibly all fit in " +
                "your pockets!",
        )
        chatNpc(
            happy,
            "We leprechauns have a shed where we keep 'em. It's a magic shed, so ye can get " +
                "yer items back from any of us leprechauns whenever ye want. Saves ye havin' " +
                "to carry loads of stuff around the country!",
        )
        chatNpc(quiz, "So... do ye want to be using the store?")
    }
}

internal object ToolLeprechaunInterfaces : InterfaceReferences() {
    val farming_tools = find("farming_tools")
    val side = find("farming_tools_side")
}
