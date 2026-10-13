package org.runerealm.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.repo.world.WorldRepository
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.type.refs.spot.SpotanimReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * [Count Check](https://oldschool.runescape.wiki/w/Count_Check), the Lumbridge security tutor.
 *
 * Jagex Accounts and Bank PINs do not exist on this server, so the account check always behaves as
 * if the player has neither.
 */
class CountCheck @Inject constructor(private val worldRepo: WorldRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.count_check) { countDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.countDialogue(npc: Npc) {
        var teleport = false
        startDialogue(npc) {
            chatNpc(
                laugh,
                "Ahahahaha! I am Count Check, the renowned security expert. Would you like me " +
                    "to check your account to see if it is secure?",
            )
            teleport = mainMenu(npc)
        }
        if (teleport) {
            vars[count_varbits.teleported_by_count] = 1
            telejump(STRONGHOLD_ENTRANCE)
        }
    }

    /** @return `true` if the player accepted the teleport to the Stronghold of Security. */
    private suspend fun Dialogue.mainMenu(npc: Npc): Boolean {
        val choice =
            choice5(
                "Check my account, Count Check!",
                CHECK,
                "Can you give me any advice?",
                ADVICE,
                "Where can I learn more about security?",
                LEARN,
                "Who are you?",
                WHO,
                "I'll see you another time.",
                BYE,
            )
        return handle(choice, npc)
    }

    private suspend fun Dialogue.handle(choice: Int, npc: Npc): Boolean =
        when (choice) {
            CHECK -> {
                checkAccount()
                false
            }
            ADVICE -> {
                advice(npc)
                mainMenu(npc)
            }
            LEARN -> learnMore(npc)
            WHO -> whoAreYou(npc)
            else -> {
                chatPlayer(neutral, "I'll see you another time.")
                false
            }
        }

    private suspend fun Dialogue.checkAccount() {
        chatPlayer(happy, "Check my account, Count Check!")
        chatNpc(
            sad,
            "You do not have a Jagex Account, nor a Bank PIN, so so you fail my checks! Please " +
                "upgrade before we meet again.",
        )
    }

    private suspend fun Dialogue.advice(npc: Npc) {
        chatPlayer(quiz, "Can you give me any advice?")
        chatNpc(happy, "I can! Count with the Count as I count the steps to securing your account:")
        chatNpc(laugh, "ONE... unique password!")
        chatNpc(
            neutral,
            "Never use the same password for different accounts, or on different websites. If " +
                "it got leaked, all of your accounts would become vulnerable at once!",
        )
        chatNpc(laugh, "TWO... step verification for your email account!")
        chatNpc(
            neutral,
            "Good email providers let you set up two-step verification, maybe by generating " +
                "security codes on a second device, or by sending codes to your phone. Make " +
                "sure you've enabled this.",
        )
        chatNpc(confused, "THREE... Um... Use a Jagex Account.")
        chatNpc(neutral, "Visit the RuneRealm website and log in to update your Account settings.")
        val question = choice2("Carry on.", false, "What's that got to do with THREE?", true)
        if (question) {
            chatPlayer(confused, "What's that got to do with THREE?")
            chatNpc(
                angry,
                "Look, I'd got something for ONE, TWO and FOUR, and I like counting, okay? " +
                    "Don't interrupt my flow!",
            )
            chatPlayer(neutral, "...")
        } else {
            chatPlayer(neutral, "Carry on.")
        }
        chatNpc(laugh, "FOUR... digit Bank PIN!")
        chatNpc(
            neutral,
            "Next time you visit a bank, talk to a banker and ask to check your PIN settings.",
        )
        chatNpc(madlaugh, "Muhahahahhaa! Such fun it is to count!")
        access.spotanimMap(worldRepo, count_spotanims.lightning, npc.coords)
    }

    private suspend fun Dialogue.learnMore(npc: Npc): Boolean {
        chatPlayer(quiz, "Where can I learn more about security?")
        if (vars[count_varbits.teleported_by_count] != 0) {
            chatNpc(
                neutral,
                "The Stronghold of Security, in Barbarian Village. There is much to learn " +
                    "there. But while you are here, may I check your account for you?",
            )
            return mainMenu(npc)
        }
        chatNpc(
            neutral,
            "The Stronghold of Security, in Barbarian Village. I see you have not visited it. " +
                "Would you like to? I can send you straight there - but only once.",
        )
        val teleport =
            choice2("Yes", true, "No", false, title = "Teleport to the Stronghold of Security?")
        if (teleport) {
            return true
        }
        chatNpc(quiz, "So may I check your account for you?")
        return mainMenu(npc)
    }

    private suspend fun Dialogue.whoAreYou(npc: Npc): Boolean {
        chatPlayer(quiz, "Who are you?")
        chatNpc(
            neutral,
            "As you may see, I am a vampyre, from Morytania. I left there when I heard the " +
                "terrible news and now I choose to spend my days warning others of the perils " +
                "that lay in wait for them.",
        )
        chatPlayer(confused, "The perils?")
        chatNpc(
            worried,
            "Yes, be warned! The travellers of these lands face danger from outside. Accounts " +
                "can be stolen, and when that happens one can lose everything one has worked " +
                "for.",
        )
        chatNpc(
            neutral,
            "I've left my vampyric ways behind me, and now I spend my days letting everyone " +
                "know of this terrible danger.",
        )
        chatNpc(
            neutral,
            "In return for my dedication, I have been blessed with the ability to check if " +
                "adventurers are ready to defend themselves against these account thieves.",
        )
        // The "If you're a vampyre, how did you cross the Salve?" option requires Priest in Peril.
        val choice =
            choice4(
                "Check my account, Count Check!",
                CHECK,
                "Can you give me any advice?",
                ADVICE,
                "Where can I learn more about security?",
                LEARN,
                "I'll see you another time.",
                BYE,
            )
        return handle(choice, npc)
    }

    private companion object {
        const val CHECK = 1
        const val ADVICE = 2
        const val LEARN = 3
        const val WHO = 4
        const val BYE = 5

        val STRONGHOLD_ENTRANCE = CoordGrid(0, 48, 53, 9, 29)
    }
}

internal object count_varbits : VarBitReferences() {
    val teleported_by_count = find("sos_teleported_by_count")
}

internal object count_spotanims : SpotanimReferences() {
    val lightning = find("saradomin_lightning")
}
