package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class FredTheFarmer : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.fred_the_farmer) { fredDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.fredDialogue(npc: Npc) =
        startDialogue(npc) { fredTheFarmer() }

    // TODO(content): Sheep Shearer quest progress and "The Thing" (Cold War) dialogue.
    private suspend fun Dialogue.fredTheFarmer() {
        chatNpc(
            angry,
            "What are you doing on my land? You're not the one who keeps leaving all my " +
                "gates open and letting out all my sheep, are you?",
        )
        val choice =
            choice3(
                "I'm looking for a quest.",
                1,
                "I'm looking for something to kill.",
                2,
                "I'm lost.",
                3,
                title = "Select an option",
            )
        when (choice) {
            1 -> lookingForQuest()
            2 -> {
                chatPlayer(neutral, "I'm looking for something to kill.")
                chatNpc(angry, "What, on my land? Leave my livestock alone you scoundrel!")
            }
            3 -> {
                chatPlayer(confused, "I'm lost.")
                chatNpc(
                    neutral,
                    "How can you be lost? Just follow the road east and south. You'll end " +
                        "up in Lumbridge fairly quickly.",
                )
            }
        }
    }

    private suspend fun Dialogue.lookingForQuest() {
        chatPlayer(quiz, "I'm looking for a quest.")
        chatNpc(neutral, "You're after a quest, you say? Actually, I could do with a bit of help.")
        chatNpc(
            neutral,
            "My sheep are getting mighty woolly. I'd be much obliged if you could shear " +
                "them. And while you're at it, spin the wool for me too.",
        )
        chatNpc(
            neutral,
            "Yes, that's it. Bring me 20 balls of wool. And I'm sure I could sort out " +
                "some sort of payment. Of course, there's the small matter of The Thing.",
        )
        chatPlayer(quiz, "What do you mean, The Thing?")
        chatNpc(
            neutral,
            "Well now, no one has ever seen The Thing. That's why we call it The Thing, " +
                "'cos we don't know what it is.",
        )
        chatNpc(
            worried,
            "Some say it's a black hearted shapeshifter, hungering for the souls of hard " +
                "working decent folk like me. Others say it's just a sheep.",
        )
        chatNpc(
            angry,
            "Well I don't have all day to stand around and gossip. Are you going to " +
                "shear my sheep or what!",
        )
        val start = choice2("Yes.", true, "No.", false, title = "Start the Sheep Shearer quest?")
        if (start) {
            chatPlayer(happy, "Yes, okay. I can do that.")
            mesbox("This quest is not available yet.")
        } else {
            chatPlayer(neutral, "No, I'll give it a miss.")
            chatNpc(neutral, "Suit yourself.")
        }
    }
}
