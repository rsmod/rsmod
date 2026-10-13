package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Dialogue follows https://oldschool.runescape.wiki/w/Transcript:Father_Urhney ("Before The
 * Restless Ghost" - the quest is not implemented yet).
 */
class FatherUrhney : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.father_urhney) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { urhneyDialogue() }

    private suspend fun Dialogue.urhneyDialogue() {
        chatNpc(angry, "Go away! I'm meditating!")
        val option =
            choice2(
                "Well, that's friendly.",
                ::thatsFriendly,
                "I've come to repossess your house.",
                ::repossessHouse,
            )
        option()
    }
}

private suspend fun Dialogue.thatsFriendly() {
    chatPlayer(neutral, "Well, that's friendly.")
    chatNpc(angry, "I SAID go AWAY.")
    chatPlayer(shifty, "Okay, okay... sheesh, what a grouch.")
}

private suspend fun Dialogue.repossessHouse() {
    chatPlayer(neutral, "I've come to repossess your house.")
    chatNpc(shocked, "Under what grounds???")
    val option =
        choice2(
            "Repeated failure on mortgage repayments.",
            ::mortgageRepayments,
            "I don't know, I just wanted this house.",
            ::justWantedHouse,
        )
    option()
}

private suspend fun Dialogue.mortgageRepayments() {
    chatPlayer(neutral, "Repeated failure on mortgage repayments.")
    chatNpc(confused, "What?")
    chatNpc(angry, "But... I don't have a mortgage! I built this house myself!")
    chatPlayer(
        worried,
        "Sorry. I must have got the wrong address. All the houses look the same around here.",
    )
    chatNpc(confused, "What? What houses? What ARE you talking about???")
    chatPlayer(neutral, "Never mind.")
}

private suspend fun Dialogue.justWantedHouse() {
    chatPlayer(shifty, "I don't know. I just wanted this house...")
    chatNpc(angry, "Oh... go away and stop wasting my time!")
}
