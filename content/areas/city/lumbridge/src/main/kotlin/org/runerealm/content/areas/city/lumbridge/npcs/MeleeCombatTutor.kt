package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.output.ClientScripts.toplevelSidebuttonSwitch
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.baseDefenceLvl
import org.runerealm.api.player.stat.statBase
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpcU
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.type.obj.ObjType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Harlan, the Melee combat tutor.
 *
 * @see <a href="https://oldschool.runescape.wiki/w/Transcript:Melee_combat_tutor">Transcript</a>
 */
class MeleeCombatTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.melee_tutor) { startDialogue(it.npc) }
        onOpNpcU(lumbridge_npcs.melee_tutor, MeleeTutorObjs.training_sword) {
            returnItem(
                it.npc,
                it.invSlot,
                MeleeTutorObjs.training_sword,
                "Training sword",
                "training sword",
            )
        }
        onOpNpcU(lumbridge_npcs.melee_tutor, MeleeTutorObjs.training_shield) {
            returnItem(
                it.npc,
                it.invSlot,
                MeleeTutorObjs.training_shield,
                "Training shield",
                "training shield",
            )
        }
        onOpNpcU(lumbridge_npcs.melee_tutor) { noUse(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) {
            if (player.baseDefenceLvl >= 99) {
                masteryGreeting()
            } else {
                chatNpc(
                    happy,
                    "Greetings adventurer, I am the Melee combat tutor. Is " +
                        "there anything I can do for you?",
                )
                mainMenu()
            }
        }
    }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice5(
                "Tell me about melee combat.",
                1,
                "Tell me about different weapon types I can use.",
                2,
                "Tell me about skillcapes.",
                3,
                "I'd like a training sword and shield.",
                4,
                "Goodbye.",
                5,
            )
        when (choice) {
            1 -> meleeCombat()
            2 -> weaponTypes()
            3 -> skillcapes()
            4 -> trainingEquipment()
            5 -> goodbye()
        }
    }

    private suspend fun Dialogue.otherMenu() {
        val choice =
            choice5(
                "What if I wanted to fight something a bit more... human.",
                1,
                "Tell me about different weapon types I can use.",
                2,
                "Tell me about skillcapes.",
                3,
                "I'd like a training sword and shield.",
                4,
                "Goodbye.",
                5,
            )
        when (choice) {
            1 -> pvp()
            2 -> weaponTypes()
            3 -> skillcapes()
            4 -> trainingEquipment()
            5 -> goodbye()
        }
    }

    private suspend fun Dialogue.meleeCombat() {
        chatPlayer(quiz, "Tell me about melee combat.")
        chatNpc(
            neutral,
            "Well adventurer, the first thing you will need is a sword " +
                "and a shield appropriate for your level.",
        )
        toplevelSidebuttonSwitch(player, SIDE_WORN)
        chatNpc(
            neutral,
            "Make sure to equip your sword and shield. Click on them " +
                "in your inventory, they will disappear from your " +
                "inventory and move to your worn items. You can see " +
                "your worn items in the worn items tab here.",
        )
        toplevelSidebuttonSwitch(player, SIDE_COMBAT)
        chatNpc(
            neutral,
            "When you are wielding your sword you will then be able " +
                "to see the correct options in the combat interface.",
        )
        chatNpc(
            neutral,
            "There are four different melee styles. Accurate, " +
                "aggressive, defensive and controlled. Not all weapons " +
                "will have all four styles though.",
        )
        chatPlayer(quiz, "Interesting, what does each style do?")
        chatNpc(
            happy,
            "Well I am glad you asked. The accurate style will give " +
                "you experience points in your Attack skill, you will also " +
                "find you will deal damage more frequently as a result of " +
                "being, well, more accurate.",
        )
        chatNpc(
            neutral,
            "Next we have the aggressive style. This style will give " +
                "you experience points in your Strength skill. When using " +
                "this style you will notice that your attacks will hit a " +
                "little harder.",
        )
        chatNpc(
            neutral,
            "Now for the defensive style, this style will give you " +
                "experience points in your Defensive skill. When using " +
                "this style you will notice that you get hit less often.",
        )
        chatNpc(
            neutral,
            "Finally, we have the controlled style. This style will give " +
                "you the same amount of experience as the other styles " +
                "would but shared across all three of the combat skills.",
        )
        chatNpc(
            neutral,
            "If you were using the training sword for example, there " +
                "are four different attack types. Stab, lunge, slash and " +
                "block.",
        )
        chatNpc(
            neutral,
            "Each type uses one of the attack styles. Stab uses " +
                "accurate, lunge and slash use aggressive and block uses " +
                "defensive.",
        )
        chatNpc(
            neutral,
            "To find out which style an attack type uses, hover your " +
                "mouse cursor over the style button.",
        )
        // TODO(content): Adventure paths eligibility line ("If you need some more advice, you
        //  should talk to Adventurer Jon about Adventure paths...") once adventure paths exist.
        chatNpc(happy, "Is there anything else you would like to know?")
        otherMenu()
    }

    private suspend fun Dialogue.pvp() {
        chatPlayer(quiz, "What if I wanted to fight something a bit more... human.")
        chatNpc(neutral, "Well adventurer, there are a few places you might be able to do this.")
        chatNpc(
            neutral,
            "You could try your luck at castle wars. Here, two teams " +
                "fight each other to defend their respective flags. To win " +
                "the game you will need to get the other team's flag and " +
                "return it to your flag stand.",
        )
        chatPlayer(happy, "Capture the flag, sounds like a lot of fun.")
        chatNpc(
            neutral,
            "If you are in a clan, you should gather some clan " +
                "members and try out clan wars. There you can see which " +
                "clan is better than the other by fighting each other in an " +
                "arena.",
        )
        toplevelSidebuttonSwitch(player, SIDE_GROUPING)
        chatNpc(
            neutral,
            "Both activities are safe minigame, which means if you die " +
                "you will not loose any of your items. You can get to " +
                "them by using the teleport option in your minigames tab.",
        )
        chatNpc(
            worried,
            "There is also the wilderness. The wilderness is north of " +
                "Varrock and you can fight other players there. But bare " +
                "in mind if you die to another player in the wilderness " +
                "you will lose your stuff.",
        )
        chatNpc(
            neutral,
            "But this also means that if you kill another player you " +
                "will be able to take their stuff too.",
        )
        chatNpc(
            neutral,
            "Only go into the wilderness with items you are willing to " +
                "lose and pay attention to the wilderness level you are " +
                "in. The higher the level you go, more player will be able " +
                "to attack you.",
        )
        chatNpc(neutral, "You can find which player can attack you by checking your combat level.")
        chatNpc(
            neutral,
            "Minus the wilderness level from your combat level to find " +
                "the lowest level that you can attack, then add the " +
                "wilderness level to your combat level to find the highest " +
                "level that you can attack.",
        )
        chatNpc(happy, "Is there anything else you would like to know?")
        otherMenu()
    }

    private suspend fun Dialogue.weaponTypes() {
        chatPlayer(quiz, "Tell me about different weapon types I can use")
        chatNpc(
            neutral,
            "Well let me see now...There are stabbing type weapons " +
                "such as daggers, then you have swords which are " +
                "slashing, maces that have great crushing abilities, battle " +
                "axes which are powerful.",
        )
        chatNpc(
            neutral,
            "It depends a lot on how you want to fight. Experiment " +
                "and find out what is best for you. Never be scared to try " +
                "out a new weapon; you never know, you might like it!",
        )
        chatNpc(
            happy,
            "While I tried all of them for a while, I settled on this rather good sword.",
        )
        chatNpc(
            neutral,
            "You might also find that different weapon types are more " +
                "accurate against different monsters.",
        )
        chatNpc(happy, "Is there anything else you would like to know?")
        otherMenu()
    }

    private suspend fun Dialogue.skillcapes() {
        chatPlayer(quiz, "Tell me about skillcapes.")
        chatNpc(
            happy,
            "Of course. Skillcapes are a symbol of achievement. Only " +
                "people who have mastered a skill and reached level 99 " +
                "can get their hands on them and gain the benefits they " +
                "carry.",
        )
        chatNpc(
            neutral,
            "The Cape of Defence will act as ring of life, saving you " +
                "from combat if your hitpoints become low.",
        )
        chatNpc(happy, "Is there anything else you would like to know?")
        otherMenu()
    }

    private suspend fun Dialogue.trainingEquipment() {
        chatPlayer(quiz, "I'd like a training sword and shield.")
        val sword = MeleeTutorObjs.training_sword
        val shield = MeleeTutorObjs.training_shield
        val hasSword = owns(sword)
        val hasShield = owns(shield)
        when {
            hasSword && hasShield -> {
                chatNpc(
                    neutral,
                    "You already have a training sword and shield. Save " +
                        "some for the other adventurers.",
                )
            }
            hasSword -> {
                if (!giveItems(shield)) return
                objbox(shield, "Harlan gives you a Training shield.")
                chatNpc(happy, "There you go, use it well.")
            }
            hasShield -> {
                if (!giveItems(sword)) return
                objbox(sword, "Harlan gives you a Training sword.")
                chatNpc(happy, "There you go, use it well.")
            }
            else -> {
                if (!giveItems(sword, shield)) return
                doubleobjbox(shield, sword, "Harlan gives you a Training sword and shield.")
                chatNpc(happy, "There you go, use it well.")
            }
        }
        chatNpc(happy, "Is there anything else I can help you with?")
        otherMenu()
    }

    /**
     * Adds every obj in [types] to the player's inventory. If there is not enough room, nothing is
     * given and a message is shown instead.
     *
     * _Note: The wiki transcript does not document the full-inventory case for this tutor._
     */
    private suspend fun Dialogue.giveItems(vararg types: ObjType): Boolean {
        if (player.inv.freeSpace() < types.size) {
            mesbox("You don't have enough room in your inventory.")
            return false
        }
        for (type in types) {
            access.invAdd(player.inv, type)
        }
        return true
    }

    private fun Dialogue.owns(type: ObjType): Boolean =
        type in player.inv || type in player.worn || type in access.bank

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
    }

    private suspend fun Dialogue.masteryGreeting() {
        chatNpc(
            happy,
            "Ah, but I can see you're already a master in the fine art " +
                "of Defence. Perhaps you have come to me to purchase a " +
                "Skillcape of Defence, and thus join the elite few who " +
                "have mastered this exacting skill?",
        )
        chatNpc(
            neutral,
            "In recognition of your defensive abilities, when you have " +
                "it equipped it will act as ring of life, saving you from " +
                "combat if your hitpoints become low.",
        )
        val choice =
            choice2(
                "Yes, please sell me a Skillcape of Defence.",
                1,
                "Can you tell me about different weapon types I can use?",
                2,
            )
        when (choice) {
            1 -> buySkillcape()
            2 -> weaponTypes()
        }
    }

    private suspend fun Dialogue.buySkillcape() {
        chatPlayer(quiz, "May I buy a Skillcape of Defence, please?")
        chatNpc(
            neutral,
            "You wish to join the elite defenders of this world? I'm " +
                "afraid such things do not come cheaply - in fact they " +
                "cost 99000 coins, to be precise!",
        )
        val choice =
            choice2(
                "99000 coins? That's much too expensive.",
                1,
                "I think I have the money right here, actually.",
                2,
            )
        if (choice == 1) {
            chatPlayer(sad, "99000 coins? That's much too expensive.")
            chatNpc(
                neutral,
                "Not at all; there are many other adventurers who would " +
                    "love the opportunity to purchase such a prestigious " +
                    "item! You can find me here if you change your mind.",
            )
            return
        }
        chatPlayer(happy, "I think I have the money right here, actually.")
        val coins = access.invTotal(player.inv, objs.coins)
        if (coins < SKILLCAPE_PRICE) {
            chatPlayer(sad, "But, unfortunately, I was mistaken.")
            chatNpc(neutral, "Well, come back and see me when you do.")
            return
        }
        // Paying the exact amount frees up the coins' slot.
        val requiredSpace = if (coins == SKILLCAPE_PRICE) 1 else 2
        if (player.inv.freeSpace() < requiredSpace) {
            chatNpc(
                neutral,
                "Unfortunately all Skillcapes are only available with a free " +
                    "hood, it's part of a skill promotion deal; buy one get " +
                    "one free, you know. So you'll need to free up some " +
                    "inventory space before I can sell you one.",
            )
            return
        }
        val cape =
            if (player.hasOtherMastery()) objs.defence_skillcape_t else objs.defence_skillcape
        val paid = access.invDel(player.inv, objs.coins, SKILLCAPE_PRICE)
        if (!paid.success) {
            return
        }
        access.invAdd(player.inv, cape)
        access.invAdd(player.inv, objs.defence_hood)
        chatNpc(happy, "Excellent! Wear that cape with pride my friend.")
    }

    /** Whether the player has reached level 99 in any skill other than Defence. */
    private fun Player.hasOtherMastery(): Boolean =
        ALL_STATS.any { it != stats.defence && statBase(it) >= 99 }

    private suspend fun ProtectedAccess.returnItem(
        npc: Npc,
        invSlot: Int,
        type: ObjType,
        name: String,
        lowercase: String,
    ) {
        startDialogue(npc) {
            chatPlayer(quiz, "I no longer need my $name, would you like it back?")
            chatNpc(
                happy,
                "Sure thing, I can give this to another adventurer who is in need of training.",
            )
            val count = player.inv[invSlot]?.count ?: return@startDialogue
            access.invDel(player.inv, type, count = count, slot = invSlot)
            // Typo ("you") is on purpose; it matches the game.
            objbox(type, "You give back you $lowercase to the Melee combat tutor.")
        }
    }

    private suspend fun ProtectedAccess.noUse(npc: Npc) {
        startDialogue(npc) { chatNpc(neutral, "I have no use for that, sorry.") }
    }

    private companion object {
        const val SKILLCAPE_PRICE = 99_000

        const val SIDE_COMBAT = 0
        const val SIDE_WORN = 4
        const val SIDE_GROUPING = 7

        val ALL_STATS by lazy {
            listOf(
                stats.attack,
                stats.defence,
                stats.strength,
                stats.hitpoints,
                stats.ranged,
                stats.prayer,
                stats.magic,
                stats.cooking,
                stats.woodcutting,
                stats.fletching,
                stats.fishing,
                stats.firemaking,
                stats.crafting,
                stats.smithing,
                stats.mining,
                stats.herblore,
                stats.agility,
                stats.thieving,
                stats.slayer,
                stats.farming,
                stats.runecrafting,
                stats.hunter,
                stats.construction,
            )
        }
    }
}

internal object MeleeTutorObjs : ObjReferences() {
    val training_sword = find("aide_shortsword")
    val training_shield = find("aide_shield")
}
