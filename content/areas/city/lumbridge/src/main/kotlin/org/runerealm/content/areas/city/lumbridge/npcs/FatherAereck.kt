package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class FatherAereck : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.father_aereck) { aereckDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.aereckDialogue(npc: Npc) = startDialogue(npc) { welcome() }
}

private suspend fun Dialogue.welcome() {
    chatNpc(happy, "Welcome to the church of holy Saradomin.")
    val next =
        choice3(
            "Who's Saradomin?",
            ::whosSaradomin,
            "Nice place you've got here.",
            ::nicePlace,
            "I'm looking for a quest!",
            ::lookingForQuest,
        )
    next()
}

private suspend fun Dialogue.whosSaradomin() {
    chatPlayer(quiz, "Who's Saradomin?")
    chatNpc(confused, "Surely you have heard of the god, Saradomin?")
    chatNpc(
        angry,
        "He who creates the forces of goodness and purity in this world? I cannot " +
            "believe your ignorance!",
    )
    chatNpc(
        neutral,
        "This is the god with more followers than any other! ...At least in this part " +
            "of the world.",
    )
    chatNpc(neutral, "He who created this world along with his brothers Guthix and Zamorak?")
    val next =
        choice2(
            "Oh, THAT Saradomin...",
            ::thatSaradomin,
            "Oh, sorry. I'm not from this world.",
            ::notFromThisWorld,
        )
    next()
}

private suspend fun Dialogue.thatSaradomin() {
    chatPlayer(neutral, "Oh, THAT Saradomin...")
    chatNpc(confused, "There... is only one Saradomin...")
    chatPlayer(shifty, "Yeah... I, uh, thought you said something else.")
}

private suspend fun Dialogue.notFromThisWorld() {
    chatPlayer(sad, "Oh, sorry. I'm not from this world.")
    chatNpc(confused, "...")
    chatNpc(confused, "That's... strange.")
    chatNpc(
        confused,
        "I thought things not from this world were all... You know. Slime and tentacles.",
    )
    val disguise =
        choice2(
            "You don't understand. This is an online game!",
            false,
            "I am - do you like my disguise?",
            true,
        )
    if (disguise) {
        chatPlayer(laugh, "I am - do you like my disguise?")
        chatNpc(
            shocked,
            "Aargh! Avaunt foul creature from another dimension! Avaunt! Begone in the " +
                "name of Saradomin!",
        )
        chatPlayer(neutral, "Ok, ok, I was only joking...")
    } else {
        chatPlayer(neutral, "You don't understand. This is an online game!")
        chatNpc(confused, "I... beg your pardon?")
        chatPlayer(neutral, "Never mind.")
    }
}

private suspend fun Dialogue.nicePlace() {
    chatPlayer(happy, "Nice place you've got here.")
    chatNpc(happy, "It is, isn't it? It was built over 230 years ago.")
}

private suspend fun Dialogue.lookingForQuest() {
    chatPlayer(quiz, "I'm looking for a quest.")
    chatNpc(happy, "That's lucky, I need someone to do a quest for me.")
    val accept = choice2("Yes", true, "No", false, title = "Start The Restless Ghost quest?")
    if (accept) {
        chatPlayer(happy, "Okay, let me help then.")
        mesbox("This quest is not available yet.")
    } else {
        chatPlayer(neutral, "Sorry, I don't have time right now.")
        chatNpc(
            neutral,
            "Oh well. If you do have some spare time on your hands, come back and talk to me.",
        )
    }
}
