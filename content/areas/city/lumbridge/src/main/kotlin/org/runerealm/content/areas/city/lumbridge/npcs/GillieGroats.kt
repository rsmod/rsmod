package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class GillieGroats : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.gillie) { gillieDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.gillieDialogue(npc: Npc) =
        startDialogue(npc) { gillieGroats() }

    // TODO(content): Replacement cowbell amulet dialogue (Cold War).
    private suspend fun Dialogue.gillieGroats() {
        chatNpc(happy, "Hello, I'm Gillie the Milkmaid. What can I do for you?")
        val choice =
            choice3(
                "Who are you?",
                1,
                "Can you tell me how to milk a cow?",
                2,
                "I'm fine, thanks.",
                3,
            )
        when (choice) {
            1 -> whoAreYou()
            2 -> howToMilk()
            3 -> chatPlayer(neutral, "I'm fine, thanks.")
        }
    }

    private suspend fun Dialogue.whoAreYou() {
        chatNpc(
            happy,
            "My name is Gillie Groats. My father is a farmer and I milk the cows for him.",
        )
        chatPlayer(quiz, "Do you have any buckets of milk spare?")
        chatNpc(
            neutral,
            "I'm afraid not. We need all of our milk to sell to market, but you can milk " +
                "the cow yourself if you need the milk.",
        )
        chatPlayer(happy, "Thanks.")
    }

    private suspend fun Dialogue.howToMilk() {
        chatPlayer(quiz, "So how do you get milk from a cow then?")
        chatNpc(happy, "It's very easy. First you need an empty bucket to hold the milk.")
        chatNpc(neutral, "Then find a dairy cow to milk - you can't milk just any cow.")
        chatPlayer(quiz, "How do I find a dairy cow?")
        chatNpc(
            neutral,
            "They are easy to spot - they are dark brown and white, unlike beef cows, " +
                "which are light brown and white. We also tether them to a post to stop " +
                "them wandering around all over the place.",
        )
        chatNpc(happy, "There are a couple very near, in this field.")
        chatNpc(
            happy,
            "Then just milk the cow and your bucket will fill with tasty, nutritious milk.",
        )
    }
}
