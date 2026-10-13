package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class DukeHoracio : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.duke_horacio) { dukeDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.dukeDialogue(npc: Npc) = startDialogue(npc) { greeting() }
}

private suspend fun Dialogue.greeting() {
    chatNpc(neutral, "Greetings. Welcome to my castle.")
    val hasShield =
        DukeObjs.anti_dragon_shield in player.inv || DukeObjs.anti_dragon_shield in player.worn
    val next =
        if (hasShield) {
            choice2(
                "Have you any quests for me?",
                ::anyQuests,
                "Where can I find money?",
                ::findMoney,
            )
        } else {
            choice3(
                "I seek a shield that will protect me from dragonbreath.",
                ::seekShield,
                "Have you any quests for me?",
                ::anyQuests,
                "Where can I find money?",
                ::findMoney,
            )
        }
    next()
}

private suspend fun Dialogue.seekShield() {
    chatPlayer(quiz, "I seek a shield that will protect me from dragonbreath.")
    chatNpc(quiz, "A knight going on a dragon quest, hmm? What dragon do you intend to slay?")
    // The "Elvarg, the dragon of Crandor island!" option is only shown during Dragon Slayer I,
    // and the shield is only handed out freely once that quest has been completed. Neither is
    // possible until the quest is implemented.
    chatPlayer(neutral, "Oh, no dragon in particular. I just feel like killing a dragon.")
    chatNpc(
        neutral,
        "I don't have an infinite supply of these shields, you know. I'll only give one " +
            "for a truly worthy cause.",
    )
}

private suspend fun Dialogue.anyQuests() {
    chatPlayer(quiz, "Have you any quests for me?")
    chatNpc(
        neutral,
        "Well, it's not really a quest but I recently discovered this strange talisman.",
    )
    chatNpc(
        neutral,
        "It seems to be mystical and I have never seen anything like it before. Would " +
            "you take it to the head wizard at",
    )
    chatNpc(
        neutral,
        "the Wizards' Tower for me? It's just south-west of here and should not take you " +
            "very long at all. I would be awfully grateful.",
    )
    val accept = choice2("Yes.", true, "No.", false)
    if (accept) {
        chatPlayer(happy, "Sure, no problem.")
        mesbox("This quest is not available yet.")
    } else {
        chatPlayer(neutral, "Not right now.")
        chatNpc(
            sad,
            "As you wish, stranger, although I have this strange feeling that it is " +
                "important. Unfortunately, I cannot leave my castle unattended.",
        )
    }
}

private suspend fun Dialogue.findMoney() {
    chatPlayer(quiz, "Where can I find money?")
    chatNpc(
        neutral,
        "I've heard that the blacksmiths are prosperous amongst the peasantry. Maybe you " +
            "could try your hand at that?",
    )
}

internal object DukeObjs : ObjReferences() {
    val anti_dragon_shield = find("antidragonbreathshield")
}
