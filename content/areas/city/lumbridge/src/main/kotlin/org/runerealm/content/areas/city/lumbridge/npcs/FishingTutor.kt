package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.baseFishingLvl
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The Fishing tutor.
 *
 * @see <a href="https://oldschool.runescape.wiki/w/Transcript:Fishing_tutor">Transcript</a>
 */
class FishingTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.fishing_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { mainMenu() }
    }

    private suspend fun Dialogue.mainMenu() {
        val level = player.baseFishingLvl
        val levelOption =
            when {
                level < 29 -> "Can you teach me the basics of fishing please?"
                level < 39 -> "I already know about the basics of fishing, got any tips?"
                else -> "Any advice for an advanced fisher?"
            }
        val choice =
            choice4(
                levelOption,
                1,
                "Tell me about different fish.",
                2,
                "Where and what should I fish?",
                3,
                "Goodbye.",
                4,
            )
        when (choice) {
            1 ->
                when {
                    level < 29 -> basics()
                    level < 39 -> tips()
                    else -> advancedAdvice()
                }
            2 -> differentFish()
            3 -> whereAndWhat()
            4 -> goodbye()
        }
    }

    private suspend fun Dialogue.basics() {
        chatPlayer(quiz, "Can you teach me the basics of fishing please?")
        giveNetIfRequired()
        objbox(
            FishingTutorObjs.fishing_spot_icon,
            "Look for this icon on the minimap to find fishing spots, " +
                "they will move as the fish swim around so you can't be " +
                "lazy.",
        )
        chatNpc(
            happy,
            "Ahoy, to fish, you click on the shrimp fishin' spot while ye'r carrying a net.",
        )
        chatPlayer(quiz, "I see... is that it?")
        chatNpc(
            laugh,
            "There's far more as you progress - not just shrimps. You " +
                "get more equipment, bigger fish and other things too...",
        )
        chatNpc(
            neutral,
            "When you have a full inventory, you can cook it or take it " +
                "to the bank. You can find a bank on the roof of the " +
                "castle in Lumbridge and a cookin' range in the castle " +
                "kitchen.",
        )
        if (vars[FishingTutorVarps.fishingcompo] == 0) {
            chatNpc(
                neutral,
                "Arrr.. if yer lookin' fer quests you should head over to " +
                    "the Mountain Dwarf who lies north-west of Taverley and " +
                    "have a chat with him, I'm sure he can point you in the " +
                    "right direction.",
            )
        }
        showOnMap()
    }

    private suspend fun Dialogue.tips() {
        chatPlayer(quiz, "I already know about the basics of fishing, got any tips?")
        giveNetIfRequired()
        chatNpc(
            neutral,
            "Arrr, chose carefully where and what you fish, you can get " +
                "different fish in different places throughout the land.",
        )
        chatNpc(
            neutral,
            "Make sure you hang on to your fish, don't throw them " +
                "away as you can get valuable cooking experience from " +
                "them, gar! Look out for other things too...",
        )
        chatNpc(neutral, "Then of course there is always the trawler...")
        chatNpc(
            neutral,
            "If you venture to Port Khazard north of Yanille and speaks " +
                "to me cabin boy Murphy, he can let you and a bunch of " +
                "friends use his trawler to fish with... Just make sure you " +
                "take a bailing bucket or two.",
        )
        showOnMap()
    }

    private suspend fun Dialogue.advancedAdvice() {
        chatPlayer(quiz, "Any advice for an advanced fisher?")
        giveNetIfRequired()
        chatNpc(
            happy,
            "Aye, as you get better and better you'll find that you can " +
                "fish for things like Tuna and Swordfish. These be very " +
                "good for combat.",
        )
        chatNpc(
            neutral,
            "Of course, after that you will get Shark, and if you " +
                "venture out onto the Trawler from Port Khazard, you can " +
                "then get Manta Ray or Sea Turtle, very good eatin' on " +
                "one of them.",
        )
        chatNpc(
            neutral,
            "You can also use a fishing potion if you can get your " +
                "hands on one and just need that little edge.. ask " +
                "someone who is familiar with herblore.",
        )
        chatNpc(neutral, "Now.. quests...")
        when {
            vars[FishingTutorVarps.seaslugquest] == 0 -> {
                chatNpc(
                    neutral,
                    "Head over to south of the Legends Guild and East of " +
                        "Ardougne and find Caroline. This will have you " +
                        "investigating some strange happenings on the fishing " +
                        "platform.",
                )
                showOnMap()
            }
            vars[FishingTutorVarps.tbwt_main] == 0 -> {
                chatNpc(
                    neutral,
                    "Head over to Tai Bwo Wannai Village on the Isle of " +
                        "Karamja and speak to Timfraku to solve a strange " +
                        "mystery.",
                )
                showOnMap()
            }
            else -> {
                chatNpc(
                    neutral,
                    "Can't rightly say's there's any I know of that you could " +
                        "handle at the moment, but come back when you're a " +
                        "bit more experienced and I'm sure I can give you a " +
                        "clue.",
                )
                mainMenu()
            }
        }
    }

    private suspend fun Dialogue.showOnMap() {
        chatNpc(happy, "I can show ye on that map of yours if ya like?")
        val choice = choice2("Yes please.", 1, "No thanks.", 2)
        if (choice == 1) {
            // TODO(content): Open the world map and pan to the relevant location.
            return
        }
        mainMenu()
    }

    private suspend fun Dialogue.giveNetIfRequired() {
        val net = FishingTutorObjs.small_fishing_net
        if (net in player.inv) {
            chatNpc(happy, "I see you already have a net.")
            return
        }
        if (!player.inv.hasFreeSpace()) {
            return
        }
        access.invAdd(player.inv, net)
        chatNpc(happy, "Ah, you've lost your net have you? Have another!")
    }

    private suspend fun Dialogue.differentFish() {
        chatPlayer(quiz, "Tell me about different fish.")
        fishMenu()
    }

    private suspend fun Dialogue.fishMenu() {
        val level = player.baseFishingLvl
        val options = buildList {
            if (level < 39) {
                add("Small Net Fish" to FishTopic.SmallNet)
            }
            add("Big Net Fish" to FishTopic.BigNet)
            add("Rod and Fly Fishing" to FishTopic.Rod)
            if (level >= 25) {
                add("Lobster pot" to FishTopic.LobsterPot)
            }
            if (level >= 35) {
                add("Harpoon Fish" to FishTopic.Harpoon)
            }
            add("Tell me about..." to FishTopic.Back)
        }
        when (choose(options)) {
            FishTopic.SmallNet -> smallNetFish()
            FishTopic.BigNet -> bigNetFish()
            FishTopic.Rod -> rodFishing()
            FishTopic.LobsterPot -> lobsterPot()
            FishTopic.Harpoon -> harpoonFish()
            FishTopic.Back -> mainMenu()
        }
    }

    /**
     * Displays [options] in a single choice menu. The dialogue api can show at most five options at
     * once, so larger menus are split with a "More..." option.
     */
    private suspend fun Dialogue.choose(options: List<Pair<String, FishTopic>>): FishTopic {
        if (options.size > 5) {
            val firstPage = options.take(4)
            val selected =
                choice5(
                    firstPage[0].first,
                    firstPage[0].second,
                    firstPage[1].first,
                    firstPage[1].second,
                    firstPage[2].first,
                    firstPage[2].second,
                    firstPage[3].first,
                    firstPage[3].second,
                    "More...",
                    null,
                )
            return selected ?: choose(options.drop(4))
        }
        return when (options.size) {
            1 -> options[0].second
            2 -> choice2(options[0].first, options[0].second, options[1].first, options[1].second)
            3 ->
                choice3(
                    options[0].first,
                    options[0].second,
                    options[1].first,
                    options[1].second,
                    options[2].first,
                    options[2].second,
                )
            4 ->
                choice4(
                    options[0].first,
                    options[0].second,
                    options[1].first,
                    options[1].second,
                    options[2].first,
                    options[2].second,
                    options[3].first,
                    options[3].second,
                )
            else ->
                choice5(
                    options[0].first,
                    options[0].second,
                    options[1].first,
                    options[1].second,
                    options[2].first,
                    options[2].second,
                    options[3].first,
                    options[3].second,
                    options[4].first,
                    options[4].second,
                )
        }
    }

    private suspend fun Dialogue.smallNetFish() {
        objbox(
            FishingTutorObjs.small_fishing_net,
            "Ahoy, small net fishin' you can do just south of Draynor " +
                "Village and in these very spots here. Aye.",
        )
        objbox(
            FishingTutorObjs.shrimps,
            "Shrimp and anchovies can be caught with your small fishin' net.",
        )
        fishMenu()
    }

    private suspend fun Dialogue.bigNetFish() {
        objbox(
            FishingTutorObjs.big_fishing_net,
            "Aye, you can net yourself some big fish in Catherby, which " +
                "is a good place to fish for most things, Gar!",
        )
        objbox(
            FishingTutorObjs.mackerel,
            "Mackrel and Cod will form the backbone of your catch " +
                "when big net fishin'.. except for the added extras...",
        )
        chatNpc(
            neutral,
            "Some rich rewards for big net fishin', make sure you be " +
                "using a big net fishing spot though...",
        )
        fishMenu()
    }

    private suspend fun Dialogue.rodFishing() {
        objbox(
            FishingTutorObjs.fishing_rod,
            "Aye, rod fishin' can be practiced here at these spots, as " +
                "well as south of Draynor Village and in the Lumbridge " +
                "river, depending upon your experience. You can get bait " +
                "at any fishin' shop, there be one in Port Sarim.",
        )
        objbox(
            FishingTutorObjs.pike,
            "With a rod you can catch pike, sardines and herring. Good eating on them.",
        )
        objbox(
            FishingTutorObjs.fly_fishing_rod,
            "The art of fly fishin' can be done in rivers, so the " +
                "Lumbridge river here would suffice.",
        )
        objbox(FishingTutorObjs.salmon, "Aye, you can catch yourself a delicious trout or salmon.")
        fishMenu()
    }

    private suspend fun Dialogue.lobsterPot() {
        objbox(
            FishingTutorObjs.lobster_pot,
            "Arrr, Lobster pots can be used from the pier on the island of Karamja.",
        )
        objbox(
            FishingTutorObjs.lobster,
            "In Lobster pots you can catch lobsters. They're tasty after thems cooked.",
        )
        chatPlayer(quiz, "Is that all?")
        fishMenu()
    }

    private suspend fun Dialogue.harpoonFish() {
        objbox(
            FishingTutorObjs.harpoon,
            "Arrr, you can also use your harpoon from the pier on the island of Karamja.",
        )
        doubleobjbox(
            FishingTutorObjs.tuna,
            FishingTutorObjs.swordfish,
            "With it you can catch Tuna and Swordfish.",
        )
        objbox(FishingTutorObjs.shark, "... and shark.")
        chatNpc(
            happy,
            "Ahoy, there's also the fishin' guild if ya skilled enough, " +
                "you can find it south west of Seers' Village.",
        )
        fishMenu()
    }

    private suspend fun Dialogue.whereAndWhat() {
        chatPlayer(quiz, "Where and what should I fish?")
        val level = player.baseFishingLvl
        when {
            level < 10 -> {
                chatNpc(
                    happy,
                    "A young and enthusiastic fisher. Try south of Draynor " +
                        "Village or the pier on the island of Karamja to fish " +
                        "for Shrimp at level 1 or Sardines at level 5. Aye, me " +
                        "parrot concurs.",
                )
                chatPlayer(confused, "What parrot?")
                chatNpc(laugh, "Me parrot Percy on me shoulder thar! Arr!")
                chatPlayer(shifty, "Oookay...sure, a parrot, on your shoulder.")
                chatNpc(laugh, "Arrr!")
            }
            else -> membersAdvice(level)
        }
        mainMenu()
    }

    private suspend fun Dialogue.membersAdvice(level: Int) {
        val text =
            when {
                level < 16 ->
                    "Herrin' can be fished from Catherby and some other " +
                        "places when you reach level 10."
                level < 23 ->
                    "You can use a big net to catch Mackerel from Catherby " +
                        "when you reach level 16."
                level < 28 ->
                    "Cod can be fished from Catherby and some other places " +
                        "once you reach level 23."
                level < 38 ->
                    "You can use your fishin' rod and some bait to catch " +
                        "Slimy Eel in the swamps at level 28."
                level < 46 ->
                    "You can use your fishin' rod and some bait to catch " +
                        "Cave Eel in the caves below Lumbridge Swamp at level " +
                        "38."
                level < 50 ->
                    "Bass can be caught at level 46 in your big net if you " +
                        "wander along to Catherby."
                else -> null
            }
        if (text != null) {
            chatNpc(neutral, text)
            return
        }
        chatNpc(
            neutral,
            "Tuna and Swordfish can be harpooned - if you're good " +
                "enough - from the thrivin' fishing village of Catherby, or " +
                "if you can get in try the Fishin' Guild. Level 35 for " +
                "Tuna and 50 for Swordfish.",
        )
        chatNpc(
            laugh,
            "You can also go talk to Murphy and see if you can use his " +
                "trawler to catch Manta Ray and Sea Turtle if you're " +
                "really good... Gar!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
    }

    private enum class FishTopic {
        SmallNet,
        BigNet,
        Rod,
        LobsterPot,
        Harpoon,
        Back,
    }
}

internal object FishingTutorObjs : ObjReferences() {
    val fishing_spot_icon = find("fishing_spot_icon_dummy")
    val small_fishing_net = find("net")
    val big_fishing_net = find("big_net")
    val fishing_rod = find("fishing_rod")
    val fly_fishing_rod = find("fly_fishing_rod")
    val lobster_pot = find("lobster_pot")
    val harpoon = find("harpoon")
    val shrimps = find("shrimp")
    val mackerel = find("mackerel")
    val pike = find("pike")
    val salmon = find("salmon")
    val lobster = find("lobster")
    val tuna = find("tuna")
    val swordfish = find("swordfish")
    val shark = find("shark")
}

internal object FishingTutorVarps : VarpReferences() {
    val fishingcompo = find("fishingcompo")
    val seaslugquest = find("seaslugquest")
    val tbwt_main = find("tbwt_main")
}
