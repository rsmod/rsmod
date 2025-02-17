package org.rsmod.content.generic.npcs.person

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class GenericPerson : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpNpc1(content.person) { personDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.personDialogue(npc: Npc) =
        startDialogue(npc) {
            chatPlayer(happy, "Hello, how's it going?")
            if (rollBobsFlyer(128)) {
                giveBobsAxeFlyer()
                return@startDialogue
            }
            when (random.of(maxExclusive = 22)) {
                0 -> randomGenericDialogue1()
                1 -> randomGenericDialogue2()
                2 -> randomGenericDialogue3()
                3 -> randomGenericDialogue4()
                4 -> randomGenericDialogue5()
                5 -> randomGenericDialogue6()
                6 -> randomGenericDialogue7()
                7 -> randomGenericDialogue8()
                8 -> randomGenericDialogue9()
                9 -> randomGenericDialogue10()
                10 -> randomGenericDialogue11()
                11 -> randomGenericDialogue12()
                12 -> randomGenericDialogue13()
                13 -> randomGenericDialogue14()
                14 -> randomGenericDialogue15()
                15 -> randomGenericDialogue16()
                16 -> randomGenericDialogue17()
                17 -> randomGenericDialogue18()
                18 -> randomGenericDialogue19()
                19 -> randomGenericDialogue20()
                20 -> randomGenericDialogue21()
                21 -> randomGenericDialogue22()
            }
        }

    private fun ProtectedAccess.rollBobsFlyer(rate: Int): Boolean =
        random.randomBoolean(rate) && inv.hasFreeSpace() && objs.bobs_axe_flyer !in inv

    private suspend fun Dialogue.giveBobsAxeFlyer() {
        chatNpc(happy, "Have this flyer...")
        player.invAdd(player.inv, objs.bobs_axe_flyer)
    }

    private suspend fun Dialogue.randomGenericDialogue1() {
        chatNpc(happy, "I'm fine, how are you?")
        chatPlayer(happy, "Very well thank you.")
    }

    private suspend fun Dialogue.randomGenericDialogue2() {
        chatNpc(neutral, "I think we need a new king. The one we've got isn't very good.")
    }

    private suspend fun Dialogue.randomGenericDialogue3() {
        chatNpc(neutral, "How can I help you?")
        val choice =
            choice3(
                "Do you wish to trade?",
                1,
                "I'm in search of a quest.",
                2,
                "I'm in search of enemies to kill.",
                3,
            )
        when (choice) {
            1 -> tradeDialogue()
            2 -> searchingQuestDialogue()
            3 -> searchingEnemiesDialogue()
        }
    }

    private suspend fun Dialogue.randomGenericDialogue4() {
        chatNpc(angry, "None of your business.")
    }

    private suspend fun Dialogue.randomGenericDialogue5() {
        chatNpc(happy, "Not too bad thanks.")
    }

    private suspend fun Dialogue.randomGenericDialogue6() {
        chatNpc(
            neutral,
            "Not too bad, but I'm a little worried about the increase of goblins these days.",
        )
        chatPlayer(happy, "Don't worry, I'll kill them.")
    }

    private suspend fun Dialogue.randomGenericDialogue7() {
        chatNpc(confused, "Who are you?")
        chatPlayer(happy, "I'm a bold adventurer.")
        chatNpc(happy, "Ah, a very noble profession.")
    }

    private suspend fun Dialogue.randomGenericDialogue8() {
        chatNpc(happy, "I'm very well thank you.")
    }

    private suspend fun Dialogue.randomGenericDialogue9() {
        chatNpc(happy, "Hello there! Nice weather we've been having.")
    }

    private suspend fun Dialogue.randomGenericDialogue10() {
        chatNpc(happy, "Hello, how's it going?")
        searchingEnemiesDialogue()
    }

    private suspend fun Dialogue.randomGenericDialogue11() {
        chatNpc(happy, "Hello, how's it going?")
        chatPlayer(happy, "I'm in search of a quest.")
        chatNpc(neutral, "I'm sorry I can't help you there.")
    }

    private suspend fun Dialogue.randomGenericDialogue12() {
        chatNpc(happy, "Hello, how's it going?")
        chatPlayer(neutral, "Do you wish to trade?")
        chatNpc(
            neutral,
            "No, I have nothing I wish to get rid of. If you want to " +
                "do some trading, there are plenty of shops and market " +
                "stalls around though.",
        )
    }

    private suspend fun Dialogue.randomGenericDialogue13() {
        chatNpc(happy, "Hello.")
    }

    private suspend fun Dialogue.randomGenericDialogue14() {
        chatNpc(confused, "Do I know you? I'm in a hurry!")
    }

    private suspend fun Dialogue.randomGenericDialogue15() {
        chatNpc(
            neutral,
            "I'm a little worried - I've heard there's lots of people " +
                "going about, killing citizens at random.",
        )
    }

    private suspend fun Dialogue.randomGenericDialogue16() {
        chatNpc(angry, "Get out of my way, I'm in a hurry!")
    }

    private suspend fun Dialogue.randomGenericDialogue17() {
        chatNpc(neutral, "I'm busy right now.")
    }

    private suspend fun Dialogue.randomGenericDialogue18() {
        chatNpc(angry, "Are you asking for a fight?")
        // TODO(content): attack player here. seems like this should be skipped under certain
        //  circumstances. might be an indoor check.
    }

    private suspend fun Dialogue.randomGenericDialogue19() {
        chatNpc(happy, "Yo, wassup!")
    }

    private suspend fun Dialogue.randomGenericDialogue20() {
        chatNpc(neutral, "That is classified information.")
    }

    private suspend fun Dialogue.randomGenericDialogue21() {
        chatNpc(neutral, "No I don't have any spare change.")
    }

    private suspend fun Dialogue.randomGenericDialogue22() {
        chatNpc(angry, "No, I don't want to buy anything!")
    }

    private suspend fun Dialogue.tradeDialogue() {
        chatPlayer(neutral, "Do you wish to trade?")
        chatNpc(
            neutral,
            "No, I have nothing I wish to get rid of. If you want to " +
                "do some trading, there are plenty of shops and market " +
                "stalls around though.",
        )
    }

    private suspend fun Dialogue.searchingQuestDialogue() {
        chatPlayer(happy, "I'm in search of a quest.")
        chatNpc(neutral, "I'm sorry I can't help you there.")
    }

    private suspend fun Dialogue.searchingEnemiesDialogue() {
        chatPlayer(neutral, "I'm in search of enemies to kill.")
        chatNpc(
            neutral,
            "I've heard there are many fearsome creatures that dwell under the ground...",
        )
    }
}
