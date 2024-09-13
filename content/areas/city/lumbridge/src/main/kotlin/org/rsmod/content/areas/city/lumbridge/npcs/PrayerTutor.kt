package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.dialogue.Dialogue
import org.rsmod.api.dialogue.Dialogues
import org.rsmod.api.dialogue.startDialogue
import org.rsmod.api.player.output.ClientScripts.toplevelSidebuttonSwitch
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class PrayerTutor @Inject constructor(private val dialogues: Dialogues) : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpNpc1(LumbridgeNpcs.prayer_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(dialogues, npc) {
            chatPlayer(happy, "Good day, sister.")
            chatNpc(happy, "Greetings, ${player.displayName}. Can I help you with anything, today?")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice3(
                "How can I train my prayer?",
                1,
                "What is prayer useful for?",
                2,
                "No, thank you.",
                3,
            )
        when (choice) {
            1 -> prayerTraining()
            2 -> prayerUsefulness()
            3 -> noThanks()
        }
    }

    private suspend fun Dialogue.prayerTraining() {
        chatPlayer(quiz, "How can I train my prayer?")
        chatNpc(
            happy,
            "The most common way to train prayer is by either " +
                "burying bones, or offering them to the gods at some " +
                "kind of an altar.",
        )
        chatNpc(
            neutral,
            "Lots of adventurers build such altars in their own " +
                "homes, or there are a few frequent places of worship " +
                "around the world.",
        )
        chatNpc(
            happy,
            "Different kinds of bones will help you to train faster. " +
                "Generally speaking, the bigger they are and the more " +
                "frightening a creature they come from, the better they " +
                "are for it.",
        )
        chatNpc(happy, "Is there anything else you would like to know?")
        val choice = choice2("What is prayer useful for?", 1, "No, thank you.", 2)
        if (choice == 1) {
            prayerUsefulness()
        } else if (choice == 2) {
            noThanks()
        }
    }

    private suspend fun Dialogue.prayerUsefulness() {
        chatPlayer(quiz, "What is prayer useful for?")
        chatNpc(
            happy,
            "The gods look kindly upon their devout followers. There " +
                "are all kinds of benefits they may provide, if you pray " +
                "for them!",
        )
        chatPlayer(quiz, "Really? What kind of benefits?")
        chatNpc(
            happy,
            "They could help you in combat, help your wounds to " +
                "heal more quickly, protect your belongings... There's a " +
                "lot they can do for you!",
        )
        player.toplevelSidebuttonSwitch(constants.toplevel_prayer)
        chatNpc(happy, "You can find out more by looking in your prayer book.")
        chatPlayer(happy, "Wow! That sounds great.")
        chatNpc(
            neutral,
            "You need to be careful that your prayers don't run " +
                "out, though. You can get prayer potions to help you " +
                "recharge, or you can pray at an altar whenever one's " +
                "nearby.",
        )
        chatNpc(happy, "Is there anything else you would like to know?")
        val choice = choice2("How can I train my prayer?", 1, "No, thank you.", 2)
        if (choice == 1) {
            prayerTraining()
        } else if (choice == 2) {
            noThanks()
        }
    }

    private suspend fun Dialogue.noThanks() {
        chatPlayer(neutral, "No, thank you.")
        chatNpc(happy, "Very well. Saradomin be with you!")
    }
}
