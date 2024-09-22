package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.dialogue.Dialogue
import org.rsmod.api.dialogue.Dialogues
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.woodcuttingLvl
import org.rsmod.api.script.advanced.onUnimplementedOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeNpcs
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeObjs.bank_icon
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeObjs.woodcutting_icon
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class WoodsmanTutor @Inject constructor(private val dialogues: Dialogues) : PluginScript() {
    override fun ScriptContext.startUp() {
        onUnimplementedOpNpc1(LumbridgeNpcs.woodsman_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        dialogues.start(this, npc) { woodsmanDialogue() }
    }

    private suspend fun Dialogue.woodsmanDialogue() {
        // TODO(content): Get dialogue for missing level-specific condition.
        when {
            player.woodcuttingLvl >= 99 -> {}
            player.woodcuttingLvl in 29..98 -> highLevelMenu()
            player.woodcuttingLvl in 20..28 -> intermediateLevelMenu()
            else -> lowLevelMenu()
        }
    }

    private suspend fun Dialogue.lowLevelMenu() {
        val choice =
            choice2(
                "Can you teach me the basics of Woodcutting and Firemaking, please?",
                1,
                "What is that cape you're wearing?",
                2,
            )
        if (choice == 1) {
            noviceBasics()
        } else if (choice == 2) {
            capeExplanationNonMastery()
        }
    }

    private suspend fun Dialogue.noviceBasics() {
        chatPlayer(quiz, "Can you teach me the basics of Woodcutting and Firemaking, please?")
        objbox(
            woodcutting_icon,
            "Of course... look for this icon on your minimap to find areas of trees.",
        )
        giveAxeIfRequired()
        chatNpc(
            neutral,
            "When you see a likely looking tree, appreciate its " +
                "beauty, then simply click on it to chop it down.",
        )
        chatNpc(
            happy,
            "When you have a full inventory of logs, you have a " +
                "choice. You can take it to the bank, there's one on the " +
                "roof of the castle in Lumbridge, or you can burn them.",
        )
        objbox(
            bank_icon,
            "To find a bank, look for this symbol on your minimap " +
                "after climbing the stairs of the Lumbridge Castle to the " +
                "top. There are banks all over the world with this symbol.",
        )
        giveTinderboxIfRequired()
        if (objs.tinderbox in player.inv) {
            chatNpc(
                happy,
                "Click on your tinderbox, then click on one of the logs in " +
                    "your inventory, this will attempt to light a fire that you " +
                    "can use for cooking.",
            )
        } else {
            chatNpc(
                happy,
                "If you had a tinderbox with you, click on the tinderbox, " +
                    "then click on one of the logs in your inventory, this will " +
                    "attempt to light a fire that you can use for cooking.",
            )
        }
    }

    private suspend fun Dialogue.intermediateLevelMenu() {
        val choice =
            choice4(
                "I already know a bit about Woodcutting and Firemaking, any tips?",
                1,
                "Tell me about different trees and axes.",
                2,
                "What is that cape you're wearing?",
                3,
                "Goodbye.",
                4,
            )
        when (choice) {
            1 -> intermediateAdvice()
            2 -> treeAndAxeInquiry()
            3 -> capeExplanationNonMastery()
            4 -> goodbye()
        }
    }

    private suspend fun Dialogue.intermediateAdvice() {
        chatPlayer(quiz, "I already know about the basics of woodcutting, got any tips?")
        chatNpc(
            happy,
            "Choose carefully where and what you chop, you can get " +
                "different trees in different places throughout the land.",
        )
        chatNpc(
            happy,
            "Make sure you hang on to your logs, don't throw them " +
                "away as you can get valuable firemaking experience " +
                "from them! Look out for other things too...",
        )
        chatNpc(
            shifty,
            "Watch out for the tree spirits, they really don't like you " +
                "chopping trees, they can really do some damage if " +
                "you're not paying attention...",
        )
    }

    private suspend fun Dialogue.highLevelMenu() {
        val choice =
            choice4(
                "Any advice for an advanced woodcutter?",
                1,
                "Tell me about different trees and axes.",
                2,
                "What is that cape you're wearing?",
                3,
                "Goodbye.",
                4,
            )
        when (choice) {
            1 -> advancedAdvice()
            2 -> treeAndAxeInquiry()
            3 -> capeExplanationNonMastery()
            4 -> goodbye()
        }
    }

    private suspend fun Dialogue.advancedAdvice() {
        chatPlayer(quiz, "Any advice for an advanced woodcutter?")
        giveAxeIfRequired()
        chatNpc(
            happy,
            "As you get better and better you'll find that you can " +
                "chop trees such as Maple and Yew! These are very " +
                "good for firemaking.",
        )
        chatNpc(
            happy,
            "Also, look out for birds' nests, you never know what our " +
                "feathered friends have been collecting. Clue scrolls " +
                "maybe?",
        )
        highLevelMenu()
    }

    private suspend fun Dialogue.treeAndAxeInquiry() {
        chatPlayer(quiz, "Tell me about different trees and axes.")
        treeAndAxeMenu()
    }

    private suspend fun Dialogue.treeAndAxeMenu() {
        val choice =
            choice4(
                "Oak and Willow",
                1,
                "Maple and Yew",
                2,
                "Axes",
                3,
                "Go back to teaching",
                4,
                title = "Trees",
            )
        when (choice) {
            1 -> oakAndWillowExplanation()
            2 -> mapleAndYewExplanation()
            3 -> axesExplanation()
            4 -> highLevelMenu()
        }
    }

    private suspend fun Dialogue.oakAndWillowExplanation() {
        doubleobjbox(
            objs.logs,
            objs.oak_logs,
            "Almost every tree can be chopped down. Normal logs " +
                "will be produced by chopping 'Trees' and Oak logs will " +
                "come from chopping 'Oak Trees'. You can find Oak " +
                "trees in amongst normal trees scattered about the " +
                "lands.",
        )
        objbox(
            objs.willow_logs,
            "Willow trees will yield willow logs. You'll find willows like " +
                "to grow near water, you can find some south of " +
                "Draynor.",
        )
        treeAndAxeMenu()
    }

    private suspend fun Dialogue.mapleAndYewExplanation() {
        objbox(
            objs.maple_logs,
            "Maple logs can be gleaned from Maple trees. You'll " +
                "usually find Maple trees standing alone amongst other " +
                "trees.",
        )
        doubleobjbox(
            objs.yew_logs,
            woodcutting_icon,
            "Yew trees are few and far between. We do our best to " +
                "cultivate them. Look for the tree icon on your minimap " +
                "to find rare trees. Try North of Port Sarim.",
        )
        treeAndAxeMenu()
    }

    private suspend fun Dialogue.axesExplanation() {
        objbox(
            objs.bronze_axe,
            "Bronze axes are easy to get, simply go visit Bob in his " +
                "shop in Lumbridge, or talk to me if you have mislaid " +
                "yours.",
        )
        chatNpc(
            happy,
            "As you progress in your combat skill you will find you " +
                "can wield your woodcutting axe as a weapon, it's not " +
                "very effective, but it frees up a slot for another log.",
        )
        doubleobjbox(
            objs.iron_axe,
            objs.steel_axe,
            "As your woodcutting skill increases you will find " +
                "yourself able to use better axes to chop trees faster.... " +
                "anything up to steel you can buy from Bob's axe shop.",
        )
        objbox(
            objs.rune_axe,
            "Rune axes can be player made with very high level " +
                "smithing and mining. They can also be obtained through " +
                "killing one of the fearsome tree spirits, though this is " +
                "very rare.",
        )
        treeAndAxeMenu()
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
    }

    private suspend fun Dialogue.giveAxeIfRequired() {
        // NOTE: Once woodcutting axes are defined to the appropriate objs, we can remove the
        // specific bronze axe condition. Or we can keep it; doesn't make a difference.
        if (objs.bronze_axe in player.inv || content.woodcutting_axe in player.inv) {
            return
        }
        val add = player.invAdd(player.inv, objs.bronze_axe)
        if (add.success) {
            chatNpc(
                happy,
                "As you're already here, have an axe so that you can chop the trees around me.",
            )
        } else {
            chatNpc(
                sad,
                "I'd give you an axe to chop trees with but you don't have room in your inventory.",
            )
        }
    }

    private suspend fun Dialogue.giveTinderboxIfRequired() {
        if (objs.tinderbox in player.inv) {
            return
        }
        val add = player.invAdd(player.inv, objs.tinderbox)
        if (add.success) {
            chatNpc(neutral, "Ah, you've lost your tinderbox have you? Have another!")
        } else {
            // Yes - typo is on purpose.
            chatNpc(
                neutral,
                "You don't seem to have any space so I canm't give you another tinderbox.",
            )
        }
    }

    private suspend fun Dialogue.capeExplanationNonMastery() {
        chatPlayer(happy, "What is that cape you're wearing?")
        chatNpc(
            neutral,
            "This is a Skillcape of Woodcutting, wearing one " +
                "increases your chance of finding bird's nests. Only a " +
                "person who has achieved the highest possible level in a " +
                "skill can wear one.",
        )
    }
}
