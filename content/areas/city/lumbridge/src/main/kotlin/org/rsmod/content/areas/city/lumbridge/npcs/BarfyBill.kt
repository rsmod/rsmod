package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.woodcuttingLvl
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BarfyBill : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpNpc1(lumbridge_npcs.barfy_bill) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { barfyBillDialogue() }
    }

    private suspend fun Dialogue.barfyBillDialogue() {
        chatPlayer(neutral, "Hello there.")
        chatNpc(neutral, "Oh! Hello there.")
        mainDialogueOptions()
    }

    private suspend fun Dialogue.mainDialogueOptions() {
        val selection = choice2("Who are you?", 1, "Can you teach me about Canoeing?", 2)

        if (selection == 1) {
            whoAreYou()
        } else if (selection == 2) {
            askAboutCanoeing()
        }
    }

    private suspend fun Dialogue.whoAreYou() {
        chatPlayer(neutral, "Who are you?")
        chatNpc(neutral, "My name is Ex Sea Captain Barfy Bill.")
        chatPlayer(quiz, "Ex sea captain?")
        chatNpc(
            sad,
            "Yeah, I bought a lovely ship and was planning to make " +
                "a fortune running her as a merchant vessel.",
        )
        chatPlayer(quiz, "Why are you not still sailing?")
        chatNpc(
            sad,
            "Chronic sea sickness. My first, and only, voyage was " +
                "spent dry heaving over the rails.",
        )
        chatNpc(
            neutral,
            "If I had known about the sea sickness I could have saved myself a lot of money.",
        )
        chatPlayer(confused, "What are you up to now then?")
        chatNpc(
            shifty,
            "Well my ship had a little fire related problem. Fortunately it was well insured.",
        )
        chatNpc(
            neutral,
            "Anyway, I don't have to work anymore so I've taken to canoeing on the river.",
        )
        chatNpc(happy, "I don't get river sick!")
        chatNpc(quiz, "Would you like to know how to make a canoe?")

        val askAboutCanoeing = choice2("Yes", true, "No", false)
        if (askAboutCanoeing) {
            askAboutCanoeing()
        } else {
            chatPlayer(neutral, "No thanks, not right now.")
        }
    }

    private suspend fun Dialogue.askAboutCanoeing() {
        chatPlayer(quiz, "Could you teach me about canoes?")

        val woodcuttingLevel = player.woodcuttingLvl
        if (woodcuttingLevel < 12) {
            chatNpc(neutral, "Well, you don't look like you have the skill to make a canoe.")
            chatNpc(neutral, "You need to have at least level 12 woodcutting.")
            chatNpc(
                neutral,
                "Once you are able to make a canoe it makes travel along the river much quicker!",
            )
            return
        }

        chatNpc(
            neutral,
            "It's really quite simple. Just walk down to that tree on the bank and chop it down.",
        )
        chatNpc(
            neutral,
            "When you have done that you can shape the log further with your axe to make a canoe.",
        )

        when (woodcuttingLevel) {
            in 12..26 -> logCanoeing()
            in 27..41 -> dugoutCanoeing()
            in 42..56 -> stableDugoutCanoeing()
            else -> wakaCanoeing()
        }
    }

    private suspend fun Dialogue.logCanoeing() {
        chatNpc(happy, "Hah! I can tell just by looking that you lack talent in woodcutting.")
        chatPlayer(quiz, "What do you mean?")
        chatNpc(happy, "No Callouses! No Splinters!  No camp fires littering the trail behind you.")
        chatNpc(
            happy,
            "Anyway, the only 'canoe' you can make is a log. You'll " +
                "be able to travel 1 stop along the river with a log canoe.",
        )
    }

    // TODO(content): Correct mesanims. The dialogue is from the wiki transcript.
    private suspend fun Dialogue.dugoutCanoeing() {
        chatNpc(
            happy,
            "With your skill in woodcutting you could make my favourite " +
                "canoe, the Dugout. They might not be the best canoe on the " +
                "river, but they get you where you're going.",
        )
        chatPlayer(quiz, "How far will I be able to go in a Dugout canoe?")
        chatNpc(neutral, "You will be able to travel 2 stops on the river.")
    }

    // TODO(content): Correct mesanims. The dialogue is from the wiki transcript.
    private suspend fun Dialogue.stableDugoutCanoeing() {
        chatNpc(
            happy,
            "The best canoe you can make is a Stable Dugout, one step beyond a normal Dugout.",
        )
        chatNpc(happy, "With a Stable Dugout you can travel to any place on the river.")
        chatPlayer(quiz, "Even into the Wilderness?")
        chatNpc(
            worried,
            "Not likely! I've heard tell of a man up near Edgeville who " +
                "claims he can use a Waka to get up into the Wilderness.",
        )
        chatNpc(
            neutral,
            "I can't think why anyone would wish to venture into that hellish landscape though.",
        )
    }

    private suspend fun Dialogue.wakaCanoeing() {
        chatNpc(happy, "Hoo! You look like you know which end of an axe is which!")
        chatNpc(
            neutral,
            "You can easily build one of those Wakas. Be careful if " +
                "you travel into the Wilderness though.",
        )
        chatNpc(worried, "I've heard tell of great evil in that blasted wasteland.")
        chatPlayer(neutral, "Thanks for the warning Bill.")
    }
}
