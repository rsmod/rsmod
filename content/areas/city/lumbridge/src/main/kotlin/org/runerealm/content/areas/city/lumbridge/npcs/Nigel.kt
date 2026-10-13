package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc4
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Dialogue follows https://oldschool.runescape.wiki/w/Transcript:Nigel ("Standard dialogue") for a
 * regular (non-Deadman) world. The Deadman: Annihilation conversion and XP-lock options only appear
 * during temporary Deadman events, and the exit-portal blueprint option requires the Wilderness
 * house theme; none of those are available here.
 *
 * `deadman_nigel` is a multinpc whose regular variant (`deadman_nigel_regular`) has Talk-to (op1)
 * and Trade (op4). Handlers are registered on the base type, which op events are keyed by.
 *
 * The Deadman Reward Store is not implemented, so it is treated as closed.
 */
class Nigel : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.nigel) { startDialogue(it.npc) { nigelDialogue() } }
        onOpNpc4(lumbridge_npcs.nigel) { startDialogue(it.npc) { storeClosed() } }
    }
}

private suspend fun Dialogue.nigelDialogue() {
    chatPlayer(quiz, "Hey, who are you?")
    chatNpc(neutral, "I'm Nigel. It is my job to tell you about Deadman Mode.")
    while (true) {
        val option =
            choice3(
                "I have a question about Deadman mode.",
                1,
                "Can I purchase some Deadman rewards?",
                2,
                "Goodbye.",
                3,
            )
        when (option) {
            1 -> {
                chatPlayer(quiz, "I have a question about Deadman mode.")
                chatNpc(neutral, "What would you like to know?")
                deadmanQuestions()
            }
            2 -> {
                storeClosed()
                return
            }
            else -> {
                chatPlayer(neutral, "Goodbye.")
                return
            }
        }
    }
}

private suspend fun Dialogue.storeClosed() {
    chatNpc(sad, "Unfortunately the store is not currently available.")
}

/** Each answer returns to Nigel's initial option menu. */
private suspend fun Dialogue.deadmanQuestions() {
    while (true) {
        val option =
            choice5(
                "What is Deadman mode?",
                1,
                "How do I play Deadman mode?",
                2,
                "What happens when I die in Deadman mode?",
                3,
                "What happens when I kill a player in Deadman mode?",
                4,
                "More options...",
                5,
            )
        when (option) {
            1 -> whatIsDeadman()
            2 -> howToPlayDeadman()
            3 -> dyingInDeadman()
            4 -> killingInDeadman()
            else -> {
                val back =
                    choice4(
                        "How can I protect my items in Deadman mode?",
                        1,
                        "How can I protect my stats in Deadman mode?",
                        2,
                        "What are safe zones in Deadman mode?",
                        3,
                        "Previous options...",
                        4,
                    )
                when (back) {
                    1 -> protectItems()
                    2 -> protectStats()
                    3 -> safeZones()
                    else -> continue
                }
            }
        }
        return
    }
}

private suspend fun Dialogue.whatIsDeadman() {
    chatPlayer(quiz, "What is Deadman mode?")
    chatNpc(
        neutral,
        "Deadman mode is an incredibly dangerous survival game mode. Anyone can attack " +
            "anyone, anywhere. Death comes at a huge cost and killing other players can be " +
            "extremely lucrative.",
    )
    chatPlayer(quiz, "Lucrative, you say?")
    chatNpc(
        neutral,
        "Killing a player will earn you all of the items they had on them, plus the 10 most " +
            "valuable items from their bank. The unfortunate soul will also lose half of " +
            "their experience in most skills, or a quarter if they were not skulled. " +
            "Regardless, it's quite the set-back.",
    )
    chatPlayer(shocked, "Wow, that is brutal!")
    chatNpc(neutral, "It is, but it is no simple task to kill someone in Deadman mode.")
    chatNpc(
        neutral,
        "Very high level guards patrol every major city and bank. They will kill anyone who " +
            "has a skull. Getting yourself a kill is only half the battle.",
    )
}

private suspend fun Dialogue.howToPlayDeadman() {
    chatPlayer(quiz, "How do I play Deadman mode?")
    chatNpc(
        neutral,
        "You can log into Deadman mode by selecting a Deadman mode world from the world " +
            "list before you log in.",
    )
    chatPlayer(quiz, "So I don't need a new account to play Deadman mode?")
    chatNpc(
        neutral,
        "Nope! You can use your existing account on a Deadman world, providing you have " +
            "membership.",
    )
    chatPlayer(quiz, "Do my stats and items transfer over to Deadman mode?")
    chatNpc(
        neutral,
        "No, they do not. Your Deadman mode progress is completely separate from normal Old " +
            "School worlds.",
    )
}

private suspend fun Dialogue.dyingInDeadman() {
    chatPlayer(quiz, "What happens when I die in Deadman mode?")
    chatNpc(
        neutral,
        "If you die to a player you will lose the 10 most valuable stacks of items from " +
            "your bank, all items that you have on your person and half of the experience " +
            "you have gained in any unprotected skills.",
    )
    chatNpc(
        neutral,
        "If you die to a monster when you have a PK skull you will lose the 10 most " +
            "valuable stacks of items from your bank, all items that you have on your " +
            "person and half of the experience you have gained in any unprotected skills.",
    )
    chatNpc(
        neutral,
        "If you die to a monster when you do not have a PK skull you will lose all of the " +
            "items on your person except for the 3 most valuable. You will not lose " +
            "experience or any items from your bank.",
    )
}

private suspend fun Dialogue.killingInDeadman() {
    chatPlayer(quiz, "What happens when I kill a player in Deadman mode?")
    chatNpc(
        neutral,
        "When you kill a player you will receive any items they have on their person and " +
            "you will also be given a key. You can use this key on Deadman chests found in " +
            "various banks across the map. Within the chest you will",
    )
    chatNpc(
        neutral,
        "find the 10 most valuable stacks of items from the bank of the player you struck " +
            "down. From here you will be able to choose the items you would like to keep.",
    )
}

private suspend fun Dialogue.protectItems() {
    chatPlayer(quiz, "How can I protect my items in Deadman mode?")
    chatNpc(
        neutral,
        "You can protect up to 10 items by placing them in your safe deposit box. Items in " +
            "your safe deposit box will not be lost to a player who kills you, unlike those " +
            "found in your bank. Remember that you can only place 10 single items into your " +
            "safe deposit box, no stacks.",
    )
}

private suspend fun Dialogue.protectStats() {
    chatPlayer(quiz, "How can I protect my stats in Deadman mode?")
    chatNpc(
        neutral,
        "You can protect your levels in up to 2 combat skills and 3 non-combat skills. You " +
            "can choose which by opening your items lost on death interface found in your " +
            "equipment tab. Choose wisely!",
    )
    chatNpc(
        neutral,
        "You should be warned however. If you get killed by a guard, you will lose a tenth " +
            "of your experience in those skills too!",
    )
    chatNpc(
        neutral,
        "Gelin over there also has some deals on insuring your hitpoints, speak to her to " +
            "find out more about that.",
    )
}

private suspend fun Dialogue.safeZones() {
    chatPlayer(quiz, "What are safe zones in Deadman mode?")
    chatNpc(
        neutral,
        "Safe zones are areas patrolled by very high level guards which can be found in " +
            "various places across the map. The guards watch out for any player with a PK " +
            "skull and will attack them if they're spotted. It is still possible to be " +
            "attacked inside safe zones but the person doing the attacking will not stand a " +
            "chance of making it out alive.",
    )
}
