package org.runerealm.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.runerealm.api.config.refs.invs
import org.runerealm.api.config.refs.objs
import org.runerealm.api.invtx.invAdd
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.righthand
import org.runerealm.api.player.stat.baseFishingLvl
import org.runerealm.api.player.stat.baseSmithingLvl
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.Wearpos
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * [Lumbridge Guide](https://oldschool.runescape.wiki/w/Transcript:Lumbridge_Guide) (Phileas).
 *
 * Quests are not implemented, so the quest advice is always given as if the player has not started
 * any of the beginner quests. The Amulet of the Eye options are omitted for the same reason.
 */
class LumbridgeGuide @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.guide) { guideDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.guideDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(
                happy,
                "Greetings, adventurer. I am Phileas, the Lumbridge Guide. I am here to give " +
                    "information and directions to new players. Is there anything I can help " +
                    "you with?",
            )
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice5(
                "Where can I find a quest to go on?",
                1,
                "What monsters should I fight?",
                2,
                "Where can I make money?",
                3,
                "Where can I find more information?",
                4,
                "More options...",
                5,
            )
        when (choice) {
            1 -> findQuest()
            2 -> {
                chatPlayer(quiz, "What monsters should I fight?")
                monsters()
            }
            3 -> makeMoney()
            4 -> moreInformation()
            5 -> moreOptions()
        }
    }

    private suspend fun Dialogue.moreOptions() {
        val choice =
            choice4(
                "I'd like to know more about security.",
                1,
                "Where can I find a bank?",
                2,
                "I don't need any help.",
                3,
                "Previous options...",
                4,
            )
        when (choice) {
            1 -> security()
            2 -> findBank()
            3 -> chatPlayer(neutral, "No, I can find things myself thank you.")
            4 -> mainMenu()
        }
    }

    /* Shared endings */

    private suspend fun Dialogue.anythingElse() {
        chatNpc(quiz, "Is there anything else you need help with?")
        val more = choice2("Yes please.", true, "No thank you.", false)
        if (more) {
            chatPlayer(happy, "Yes please.")
            mainMenu()
        } else {
            noThankYou()
        }
    }

    private suspend fun Dialogue.noThankYou() {
        chatPlayer(neutral, "No thank you.")
    }

    private suspend fun Dialogue.somethingElse() {
        chatPlayer(quiz, "I'd like to know about something else.")
        chatNpc(happy, "Well, what would you like to know?")
        mainMenu()
    }

    /* Quests */

    private suspend fun Dialogue.findQuest() {
        chatPlayer(quiz, "Where can I find a quest to go on?")
        chatNpc(
            happy,
            "Well, I've heard my friend the cook is in need of a spot of help. He'll be in the " +
                "kitchen of this here castle. Just talk to him and he'll set you off.",
        )
        objbox(
            GuideObjs.quest_start_icon,
            "The minimap in the top right corner of the screen has various icons to show " +
                "different points of interest. Look for the icon to the left to find quest " +
                "start points.",
        )
        anythingElse()
    }

    /* Combat */

    private suspend fun Dialogue.monsters() {
        if (player.combatLevel >= EXPERIENCED_COMBAT_LEVEL) {
            chatNpc(
                neutral,
                "You're strong enough to work out what monsters to fight for yourself now, but " +
                    "the combat tutors might help you with any questions you have about the " +
                    "skills; they're just over there to the south of the general store.",
            )
            objbox(
                GuideObjs.combat_tutor_icon,
                "The minimap in the top right corner of the game screen has various icons " +
                    "showing the locations of things. This icon to the left shows where Combat " +
                    "tutors are.",
            )
            mainMenu()
            return
        }
        if (hasWeapon()) {
            fightAdvice()
            return
        }
        chatNpc(
            neutral,
            "It's going to be tough fighting monsters without a weapon. I suggest you find one " +
                "of those first.",
        )
        val choice =
            choice3(
                "How can I get a weapon?",
                1,
                "Could I get a staff like yours?",
                2,
                "Yes, but what should I fight once I have got a weapon?",
                3,
            )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "How can I get a weapon?")
                chatNpc(neutral, "Well you can either make them, find them or buy them.")
                gettingWeaponsMenu()
            }
            2 -> staffLikeYours()
            3 -> {
                chatPlayer(quiz, "Yes, but what should I fight once I have got a weapon?")
                fightAdvice()
            }
        }
    }

    private fun Dialogue.hasWeapon(): Boolean {
        if (player.righthand != null) {
            return true
        }
        return player.inv.any { it != null && objTypes[it].wearpos1 == Wearpos.RightHand.slot }
    }

    private suspend fun Dialogue.gettingWeaponsMenu() {
        val choice =
            choice3(
                "Where can I find a weapon?",
                1,
                "How do I make a weapon?",
                2,
                "Where can I buy a weapon?",
                3,
            )
        when (choice) {
            1 -> findWeapon()
            2 -> makeWeapon()
            3 -> buyWeapon()
        }
    }

    private suspend fun Dialogue.weaponsFollowUp() {
        chatNpc(quiz, "Is there anything else you need help with?")
        val choice =
            choice3(
                "No thank you.",
                1,
                "I'd like to know more about getting weapons.",
                2,
                "I'd like to know about something else.",
                3,
            )
        when (choice) {
            1 -> noThankYou()
            2 -> gettingWeaponsMenu()
            3 -> somethingElse()
        }
    }

    private suspend fun Dialogue.findWeapon() {
        chatPlayer(quiz, "Where can I find a weapon?")
        chatNpc(
            neutral,
            "If you visit the combat tutors south of the general store they may be able to " +
                "help you with that. Failing that there are a few cheap weapons lying around " +
                "in the world. For example, you can get an iron dagger if you",
        )
        chatNpc(
            neutral,
            "cross the bridge and look in the little hut where some goblins live. If it's not " +
                "there it probably means someone else has taken it. If you wait a while a new " +
                "one should appear. As you explore you may find other weapons,",
        )
        chatNpc(neutral, "too.")
        weaponsFollowUp()
    }

    private suspend fun Dialogue.makeWeapon() {
        chatPlayer(quiz, "How do I make a weapon?")
        if (player.baseSmithingLvl >= EXPERIENCED_SMITHING_LEVEL) {
            chatNpc(
                neutral,
                "You already have a reasonable Smithing level, so I think you know that already.",
            )
            return
        }
        chatNpc(
            neutral,
            "The Smithing skill allows you to make armour and weapons. Talk to the boy who " +
                "smelts metal in the furnace, I'm sure he can help.",
        )
        whereCanISmith()
    }

    private suspend fun Dialogue.whereCanISmith() {
        chatPlayer(quiz, "Where can I smith?")
        chatNpc(
            neutral,
            "You will find a helpful Smithing tutor in the west of Varrock - that's north of " +
                "here. Follow the path across the river and head north.",
        )
        objbox(objs.smithing_icon, "Look for this icon in the west of Varrock.")
        objbox(
            GuideObjs.mining_site_icon,
            "The minimap in the top right corner of the game screen has various icons showing " +
                "the locations of things. This icon to the left shows where Mining sites are.",
        )
        chatNpc(
            neutral,
            "I suggest you go and mine some ore; find the Mining symbol - with the guide " +
                "symbol near it - in the swamp south of here. The Mining guide there can teach " +
                "you how to mine ore.",
        )
        anythingElse()
    }

    private suspend fun Dialogue.buyWeapon() {
        chatPlayer(quiz, "Where can I buy a weapon?")
        chatNpc(
            neutral,
            "You can get a weapon free from the combat tutors if they think you need it. " +
                "Failing that, the nearest shop that would sell you something of that nature " +
                "is Bob's Brilliant Axes in this very town. If you want a bigger",
        )
        chatNpc(
            neutral,
            "choice I suggest you head to one of the big cities. If you follow the road east " +
                "over the bridge and then head north you will eventually reach Varrock, where " +
                "you can buy all manner of things. You can use the world map",
        )
        chatNpc(neutral, "to help you find your way.")
        weaponsFollowUp()
    }

    private suspend fun Dialogue.staffLikeYours() {
        chatPlayer(quiz, "Could I get a staff like yours?")
        chatNpc(
            neutral,
            "There is no other staff like this in all the land. It's a very important staff. " +
                "It shows who holds the job as the Lumbridge Guide, and that's me.",
        )
        chatNpc(quiz, "Is there anything else you need help with?")
        val choice =
            choice3(
                "I'd like to know more about getting weapons.",
                1,
                "I'd like to know about something else.",
                2,
                "No thank you.",
                3,
            )
        when (choice) {
            1 -> gettingWeaponsMenu()
            2 -> somethingElse()
            3 -> noThankYou()
        }
    }

    private suspend fun Dialogue.fightAdvice() {
        chatNpc(
            neutral,
            "There's lots of beasts to fight in the woods around here, especially to the west. " +
                "There are certainly some goblins and spiders that are pests and could do with " +
                "being cleared out. There's also a chicken farm or two up the",
        )
        chatNpc(
            neutral,
            "road for some fairly easy pickings. Non-player characters usually appear as yellow " +
                "dots on your minimap, although there are some that you won't be able to " +
                "fight, such as myself. A monster's combat level is",
        )
        chatNpc(
            neutral,
            "shown next to their 'Attack' option. If that level is coloured green it means the " +
                "monster is weaker than you. If it is red, it means that the monster is tougher " +
                "than you.",
        )
        chatNpc(
            neutral,
            "Remember, you will do better if you have better armour and weapons and it's always " +
                "worth carrying a bit of food to heal yourself.",
        )
        combatMenu()
    }

    private suspend fun Dialogue.combatMenu() {
        val choice =
            choice4(
                "Where can I get food to heal myself?",
                1,
                "Where can I get better armour and weapons?",
                2,
                "Okay, thanks, I will go and kill things.",
                3,
                "Can I kill other players?",
                4,
            )
        when (choice) {
            1 -> food()
            2 -> betterEquipment()
            3 -> chatPlayer(happy, "Okay, thanks, I will go and kill things.")
            4 -> killPlayers()
        }
    }

    private suspend fun Dialogue.betterEquipment() {
        chatNpc(
            neutral,
            "Well, you can make them, buy them or talk to the combat tutors just west of here.",
        )
        val choice =
            choice3(
                "How do I make a weapon?",
                1,
                "Where can I buy a weapon?",
                2,
                "Could I get a staff like yours?",
                3,
            )
        when (choice) {
            1 -> makeWeapon()
            2 -> buyWeapon()
            3 -> staffLikeYours()
        }
    }

    private suspend fun Dialogue.killPlayers() {
        chatPlayer(quiz, "Can I kill other players?")
        chatNpc(
            neutral,
            "Well, you can go to the Wilderness. That is the area for killing other players. " +
                "Just keep heading north and eventually you will be bound to reach it. Be very " +
                "careful, though. Player-killing is not for the unprepared.",
        )
        chatNpc(
            neutral,
            "I'd suggest that you leave any items you don't want to lose in the bank and take " +
                "a good supply of food with you.",
        )
        chatNpc(quiz, "Is there anything else you need help with?")
        val choice =
            choice3(
                "No thank you.",
                1,
                "I'd like to know more about combat.",
                2,
                "I'd like to know about something else.",
                3,
            )
        when (choice) {
            1 -> noThankYou()
            2 -> combatMenu()
            3 -> somethingElse()
        }
    }

    /* Food */

    private suspend fun Dialogue.food() {
        chatPlayer(quiz, "Where can I get food to heal myself?")
        chatNpc(
            neutral,
            "There are many different foods in the game such as cabbage, fish, meat and many " +
                "more. Which do you wish to hear about?",
        )
        foodMenu()
    }

    private suspend fun Dialogue.foodMenu() {
        val choice =
            choice3("How do I get cabbages?", 1, "How do I fish?", 2, "Where can I find meat?", 3)
        when (choice) {
            1 -> cabbages()
            2 -> fishing(fromFoodMenu = true)
            3 -> meat()
        }
    }

    private suspend fun Dialogue.foodFollowUp() {
        chatNpc(quiz, "Is there anything else you need help with?")
        val choice =
            choice3(
                "I'd like to know about other food.",
                1,
                "I'd like to know about something else.",
                2,
                "No thank you.",
                3,
            )
        when (choice) {
            1 -> foodMenu()
            2 -> somethingElse()
            3 -> noThankYou()
        }
    }

    private suspend fun Dialogue.cabbages() {
        chatPlayer(quiz, "How do I get cabbages?")
        chatNpc(
            neutral,
            "There is a field a little distance to the north of here packed full of cabbages " +
                "which are there for the picking.",
        )
        chatNpc(
            neutral,
            "You could also farm your own cabbages; there's a Farming patch just south of " +
                "Falador.",
        )
        foodFollowUp()
    }

    private suspend fun Dialogue.fishing(fromFoodMenu: Boolean) {
        chatPlayer(quiz, "How do I fish?")
        if (player.baseFishingLvl >= EXPERIENCED_FISHING_LEVEL) {
            chatNpc(
                confused,
                "I am confused. You are already an experienced fisher. You should know the " +
                    "answer to that already.",
            )
            return
        }
        chatNpc(
            neutral,
            "Fishing spots require different levels and equipment to use. To start Fishing, " +
                "you'll want to talk to the Fishing tutor who can be found in the swamps south " +
                "of here. He will also give you a small fishing net if you don't",
        )
        chatNpc(neutral, "own one already.")
        objbox(
            GuideObjs.fishing_spot_icon,
            "The minimap in the top right corner of the game screen has various icons showing " +
                "the locations of things. This icon to the left shows where Fishing spots are.",
        )
        objbox(
            GuideObjs.small_fishing_net,
            "You will need some Fishing equipment. At the Fishing spots to the south you can " +
                "only use a small fishing net.",
        )
        chatPlayer(quiz, "Where could I find one of those?")
        chatNpc(
            neutral,
            "You can get them from a Fishing shop or our Fishing Tutor south of here in the " +
                "swamp. There is a Fishing shop in Port Sarim; you can find it on the world " +
                "map. Port Sarim is some way to the west of here, beyond",
        )
        chatNpc(neutral, "the village of Draynor.")
        if (fromFoodMenu) {
            foodFollowUp()
        }
    }

    private suspend fun Dialogue.meat() {
        chatPlayer(quiz, "Where can I find meat?")
        chatNpc(
            neutral,
            "I suggest you go and kill some chickens. The roads on either side of this river " +
                "eventually go past a chicken farm. When you have killed some chickens, cook " +
                "them. You could either make a fire or use a range.",
        )
        chatNpc(
            neutral,
            "There is a range at the southern end in this town and a Cooking tutor in south " +
                "Lumbridge near Bob's Brilliant Axes shop.",
        )
        objbox(
            GuideObjs.range_icon,
            "The minimap in the top right corner of the game screen has various icons showing " +
                "the locations of things. This icon to the left shows where Cooking ranges are.",
        )
        foodFollowUp()
    }

    /* Money */

    private suspend fun Dialogue.makeMoney() {
        chatPlayer(quiz, "Where can I make money?")
        chatNpc(
            neutral,
            "There are many ways to make money in the game. I would suggest either killing " +
                "monsters or doing a trade skill such as Smithing or Fishing.",
        )
        chatNpc(
            neutral,
            "Please don't try to get money by begging off other players. It will make you " +
                "unpopular. Nobody likes a beggar. It is very irritating to have other players " +
                "asking for your hard-earned cash.",
        )
        val choice =
            choice3(
                "Where can I smith?",
                1,
                "How do I fish?",
                2,
                "What monsters should I fight?",
                3,
            )
        when (choice) {
            1 -> whereCanISmith()
            2 -> fishing(fromFoodMenu = false)
            3 -> {
                chatPlayer(quiz, "What monsters should I fight?")
                monsters()
            }
        }
    }

    /* Information */

    private suspend fun Dialogue.moreInformation() {
        chatPlayer(quiz, "Where can I find more information?")
        chatNpc(
            happy,
            "What you'll want is the OSRS Wiki! I've taken the liberty of opening the useful " +
                "links section of the account management tab for you. Here you can find a link " +
                "to the wiki. There's information and guides on almost",
        )
        chatNpc(
            happy,
            "everything, all easily searched and it's even written and maintained by players.",
        )
        anythingElse()
    }

    private suspend fun Dialogue.findBank() {
        chatPlayer(quiz, "Where can I find a bank?")
        chatNpc(neutral, "You'll find a bank upstairs in Lumbridge Castle - go right to the top.")
        objbox(
            objs.bank_icon,
            "The minimap in the top right corner of the game screen has various icons showing " +
                "the locations of things. This icon to the left shows where banks are.",
        )
        anythingElse()
    }

    /* Security */

    private suspend fun Dialogue.security() {
        chatPlayer(quiz, "I'd like to know more about security.")
        chatNpc(
            neutral,
            "First I must warn you to take every precaution to keep your RuneRealm password " +
                "and PIN secure. The most important thing to remember is to never give your " +
                "password to, or share your account with, anyone.",
        )
        chatNpc(
            neutral,
            "I can also tell you about password security, avoiding item scamming and in-game " +
                "moderation. I can also tell you about a place called the Stronghold of " +
                "Security, where you can learn more about account security and",
        )
        chatNpc(neutral, "have a bit of an adventure at the same time.")
        val choice =
            choice4(
                "I'd like to know about password security.",
                1,
                "I'd like to know more about avoiding item scamming.",
                2,
                "I'd like to know more about in-game moderation.",
                3,
                "I'd like to know about the Stronghold of Security.",
                4,
            )
        when (choice) {
            1 -> passwordSecurity()
            2 -> itemScamming()
            3 -> moderation()
            4 -> strongholdOfSecurity()
        }
    }

    private suspend fun Dialogue.passwordSecurity() {
        chatPlayer(quiz, "I'd like to know about password security.")
        chatNpc(
            neutral,
            "Well, the first thing to remember with password security, which I can't stress " +
                "enough, is to never tell your password, bank PIN or authenticator details to " +
                "anyone, not even if they claim to be Jagex staff. Real",
        )
        chatNpc(
            neutral,
            "Jagex staff will NEVER ask for your password. Sharing your account is a very bad " +
                "idea, even if you think that you know and trust the person you are sharing " +
                "with. Players have lost items and even their",
        )
        chatNpc(neutral, "account this way. Sharing accounts is also against the rules.")
        chatPlayer(quiz, "Is there anything else to be aware of?")
        chatNpc(
            neutral,
            "Yes, also be careful where you enter your password. Be aware that fake Old School " +
                "RuneRealm websites exist. These are sites that claim to give things such as " +
                "item/stat upgrades, free items, beta testing and",
        )
        chatNpc(neutral, "moderator applications. Jagex do not offer any of these services.")
        chatNpc(
            neutral,
            "Also be aware of trojans and keyloggers. These nasty programs can send everything " +
                "you type into your device back to the computers of malicious people, so you " +
                "can lose more than just your RuneRealm",
        )
        chatNpc(
            neutral,
            "password in this way. To avoid getting these on your device, be very wary of what " +
                "you download, especially when it comes to downloading third party software. " +
                "Also be very wary of email attachments and instant",
        )
        chatNpc(neutral, "messenger file transfers, which can contain such things.")
        chatNpc(
            neutral,
            "Finally, remember the game doesn't censor your password or bank PIN when you type " +
                "them; anyone who tells you otherwise is probably trying to steal your " +
                "character or items.",
        )
        chatNpc(quiz, "Is there anything else you need help with?")
        val choice =
            choice3(
                "Tell me more about security.",
                1,
                "I'd like to know more about something else.",
                2,
                "No thank you.",
                3,
            )
        when (choice) {
            1 -> security()
            2 -> somethingElse()
            3 -> noThankYou()
        }
    }

    private suspend fun Dialogue.securityFollowUp() {
        chatNpc(quiz, "Is there anything else you need help with?")
        val choice =
            choice3(
                "Tell me more about security.",
                1,
                "I'd like to know about something else.",
                2,
                "No thank you.",
                3,
            )
        when (choice) {
            1 -> security()
            2 -> somethingElse()
            3 -> noThankYou()
        }
    }

    private suspend fun Dialogue.itemScamming() {
        chatPlayer(quiz, "I'd like to know more about avoiding item scamming.")
        chatNpc(
            neutral,
            "There are many nice and helpful players in RuneRealm; unfortunately, as " +
                "in real life, there are some who aren't so honest. Some people may try to " +
                "trick you out of your items. Try not to fall for this as",
        )
        chatNpc(
            neutral,
            "Jagex policy is to never return lost items to players that have been scammed. " +
                "Doing so just encourages users to claim they lost items they never had to gain " +
                "an unfair advantage. However, people carrying out such",
        )
        chatNpc(neutral, "scams will get banned as it is against the rules.")
        chatNpc(
            neutral,
            "When trading, you have a second trade confirmation screen. Always double check " +
                "that you are getting the items you expect on this page as some people may try " +
                "and change what they are trading at the last minute on",
        )
        chatNpc(
            neutral,
            "the first trade screen. Remember when you have clicked accept on the second trade " +
                "screen, the trade is not reversible.",
        )
        chatNpc(
            neutral,
            "Try and find out from someone else what an item is before spending a lot of money " +
                "on it. Not all merchants are honest about the rarity or the uses of their " +
                "wares. For example, some people try to pass off spinach rolls as",
        )
        chatNpc(neutral, "rare when in fact they're as common as cabbages!")
        chatNpc(
            neutral,
            "Be wary of people offering to improve your items. For example, some people will " +
                "claim they can add a trim to your armour or upgrade your sword to a more " +
                "powerful one. This is not possible and they are trying",
        )
        chatNpc(neutral, "to steal your hard-earned equipment.")
        securityFollowUp()
    }

    private suspend fun Dialogue.moderation() {
        chatPlayer(quiz, "I'd like to know more about in-game moderation.")
        chatNpc(
            neutral,
            "You will, from time to time, see moderator characters in game. Remember: a real " +
                "staff member will never ask for your password or any other personal " +
                "information. There are two types of moderator. You may see Jagex",
        )
        chatNpc(
            neutral,
            "staff characters in game - these can be identified by a gold crown next to their " +
                "name and are the people who work in the Jagex offices. They might be from " +
                "Customer Support, or they might be a tester or a",
        )
        chatNpc(
            neutral,
            "programmer. You may also see player moderators - these can be identified by a " +
                "silver crown next to their name. Player moderators are trusted players of Old " +
                "School RuneRealm that help keep in-game behaviour in",
        )
        chatNpc(neutral, "line with the RuneRealm rules.")
        chatPlayer(quiz, "How do I become a player moderator?")
        chatNpc(
            neutral,
            "The only way to become a player moderator is to know and play by the rules laid " +
                "down by Jagex. Report those who break the rules, help players who need it and " +
                "one day you might find an invite to become a moderator in",
        )
        chatNpc(
            neutral,
            "your RuneRealm inbox. Note that Jagex will never contact you in game " +
                "or by email to become a player moderator.",
        )
        chatNpc(quiz, "Is there anything else you need help with?")
        val choice =
            choice3("No, thank you.", 1, "Yes, please.", 2, "Tell me more about security.", 3)
        when (choice) {
            1 -> noThankYou()
            2 -> {
                chatPlayer(happy, "Yes please.")
                mainMenu()
            }
            3 -> security()
        }
    }

    private suspend fun Dialogue.strongholdOfSecurity() {
        chatPlayer(quiz, "I'd like to know about the Stronghold of Security.")
        chatNpc(
            neutral,
            "An exciting new discovery has been made in the Barbarian Village. Soon after they " +
                "started mining in the middle of the village, one of the miners got caught in " +
                "a cave.",
        )
        chatPlayer(quiz, "Sounds painful, did he fall on his head?")
        chatNpc(neutral, "No, but it turned out that he had discovered a place unlike any other.")
        chatPlayer(quiz, "How so?")
        chatNpc(
            neutral,
            "Well an explorer went to look down there, it's full of new and exciting creatures " +
                "and other surprises on top of that. It turns out this place can also help you " +
                "to secure your account more effectively.",
        )
        chatPlayer(happy, "Wow, that sounds good. Where can I find the explorer to learn more?")
        chatNpc(
            sad,
            "Sadly the explorer went missing and has not been found. However, here is some " +
                "information to get you started on securing your account. You can find the " +
                "Barbarian Village north of Falador and west of Varrock.",
        )
        if (!ownsSecurityBook() && player.invAdd(player.inv, GuideObjs.security_book).success) {
            objbox(GuideObjs.security_book, "The guide hands you a book.")
        }
        securityFollowUp()
    }

    private fun Dialogue.ownsSecurityBook(): Boolean {
        val bank = player.invMap[invs.bank]
        return GuideObjs.security_book in player.inv ||
            (bank != null && GuideObjs.security_book in bank)
    }

    private companion object {
        /*
         * The wiki transcript only describes these thresholds as "X"; these values are the
         * points at which the guide's beginner advice stops being relevant.
         */
        const val EXPERIENCED_COMBAT_LEVEL = 30
        const val EXPERIENCED_SMITHING_LEVEL = 20
        const val EXPERIENCED_FISHING_LEVEL = 20
    }
}

internal object GuideObjs : ObjReferences() {
    val quest_start_icon = find("quest_start_icon_dummy")
    val combat_tutor_icon = find("combat_tutor_icon_dummy")
    val mining_site_icon = find("mining_site_icon_dummy")
    val fishing_spot_icon = find("fishing_spot_icon_dummy")
    val range_icon = find("range_icon_dummy")
    val small_fishing_net = find("net")
    val security_book = find("sos_security_book")
}
