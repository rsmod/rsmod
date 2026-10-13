package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The Warrior camped in Lumbridge Swamp. Dialogue follows
 * https://oldschool.runescape.wiki/w/Transcript:Warrior_(Lost_City) ("Standard dialogue before Lost
 * City"). The quest is not implemented, so accepting it ends with a "not available" message.
 */
class LostCityWarrior : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.warrior) { startDialogue(it.npc) { warriorDialogue() } }
    }
}

private suspend fun Dialogue.warriorDialogue() {
    chatNpc(happy, "Hello there traveller.")
    val option =
        choice2(
            "What are you camped out here for?",
            ::campedHere,
            "Do you know any good adventures I can go on?",
            ::goodAdventures,
        )
    option()
}

private suspend fun Dialogue.campedHere() {
    chatPlayer(quiz, "What are you camped here for?")
    lookingForZanaris()
}

private suspend fun Dialogue.lookingForZanaris() {
    chatNpc(
        shocked,
        "We're looking for Zanaris...GAH! I mean we're not here for any particular reason at all.",
    )
    zanarisOptions()
}

private suspend fun Dialogue.zanarisOptions() {
    val option =
        choice3(
            "Who's Zanaris?",
            ::whosZanaris,
            "What's Zanaris?",
            ::whatsZanaris,
            "What makes you think it's out here?",
            ::whatMakesYouThink,
        )
    option()
}

private suspend fun Dialogue.whosZanaris() {
    chatPlayer(quiz, "Who's Zanaris?")
    chatNpc(
        laugh,
        "Ahahahaha! Zanaris isn't a person! It's a magical hidden city filled with treasures " +
            "and rich.. uh, nothing. It's nothing.",
    )
    val option =
        choice2(
            "If it's hidden how are you planning to find it?",
            ::howToFindIt,
            "There's no such thing.",
            ::noSuchThing,
        )
    option()
}

private suspend fun Dialogue.whatsZanaris() {
    chatPlayer(quiz, "What's Zanaris?")
    chatNpc(
        worried,
        "I don't think we want other people competing with us to find it. Forget I said anything.",
    )
    val option = choice2("Please tell me.", ::pleaseTellMe, "Oh well. Never mind.", ::neverMind)
    option()
}

private suspend fun Dialogue.whatMakesYouThink() {
    chatPlayer(quiz, "What makes you think it's out here?")
    chatNpc(
        shifty,
        "Don't you know of the legends that tell of the magical city, hidden in the swam... " +
            "Uh, no, you're right, we're wasting our time here.",
    )
    val option =
        choice2(
            "If it's hidden how are you planning to find it?",
            ::howToFindIt,
            "There's no such thing!",
            ::noSuchThing,
        )
    option()
}

private suspend fun Dialogue.howToFindIt() {
    chatPlayer(quiz, "If it's hidden how are you planning to find it?")
    chatNpc(
        shifty,
        "Well, we don't want to tell anyone else about that, because we don't want anyone " +
            "else sharing in all that glory and treasure.",
    )
    val option =
        choice2(
            "Please tell me.",
            ::pleaseTellMe,
            "Looks like you don't know either.",
            ::dontKnowEither,
        )
    option()
}

private suspend fun Dialogue.dontKnowEither() {
    chatPlayer(
        laugh,
        "Well, it looks to me like YOU don't know EITHER seeing as you're all just sat around " +
            "here.",
    )
    chatNpc(
        angry,
        "Of course we know! We just haven't found which tree the stupid leprechaun's hiding " +
            "in yet!",
    )
    val accepted = choice2("Yes.", true, "No", false, title = "Start the Lost City quest?")
    if (!accepted) {
        chatPlayer(neutral, "Right... Well good luck with it.")
        return
    }
    mesbox("This quest is not available yet.")
}

private suspend fun Dialogue.noSuchThing() {
    chatPlayer(laugh, "There's no such thing!")
    chatNpc(
        angry,
        "When we've found Zanaris you'll... GAH! I mean, we're not here for any particular " +
            "reason at all.",
    )
    zanarisOptions()
}

private suspend fun Dialogue.neverMind() {
    chatPlayer(neutral, "Oh well. Never mind.")
}

private suspend fun Dialogue.goodAdventures() {
    chatPlayer(quiz, "Do you know any good adventures I can go on?")
    chatNpc(
        neutral,
        "Well we're on an adventure right now. Mind you, this is OUR adventure and we don't " +
            "want to share it - find your own!",
    )
    val option =
        choice2(
            "Please tell me.",
            ::pleaseTellMe,
            "I don't think you've found a good adventure at all!",
            ::notAGoodAdventure,
        )
    option()
}

private suspend fun Dialogue.pleaseTellMe() {
    chatPlayer(quiz, "Please tell me?")
    chatNpc(angry, "No.")
    chatPlayer(sad, "Please?")
    chatNpc(angry, "No!")
    chatPlayer(sad, "PLEEEEEEEEEEEEEEEEEEEEEASE?")
    chatNpc(verymad, "NO!")
}

private suspend fun Dialogue.notAGoodAdventure() {
    chatPlayer(laugh, "I don't think you've found a good adventure at all!")
    chatNpc(
        angry,
        "Hah! Adventurers of our calibre don't just hang around in forests for fun, whelp!",
    )
    chatPlayer(quiz, "Oh really?")
    chatPlayer(quiz, "What are you camped here for?")
    lookingForZanaris()
}
