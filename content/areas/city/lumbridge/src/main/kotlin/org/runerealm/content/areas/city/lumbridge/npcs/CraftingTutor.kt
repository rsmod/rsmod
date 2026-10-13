package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.baseCraftingLvl
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Cadmus, the Crafting tutor.
 *
 * @see <a href="https://oldschool.runescape.wiki/w/Transcript:Crafting_tutor">Transcript</a>
 */
class CraftingTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.crafting_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) {
            chatPlayer(happy, "Hello.")
            chatNpc(happy, "Hello there! Are you interested in hearing all about crafting?")
            mainMenu()
        }
    }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice3(
                "How can I train my crafting?",
                1,
                "What kinds of things can be crafted?",
                2,
                "No, thank you.",
                3,
            )
        when (choice) {
            1 -> trainCrafting()
            2 -> craftables()
            3 -> noThanks()
        }
    }

    private suspend fun Dialogue.anythingElse() {
        chatNpc(happy, "Is there anything else you want to know?")
        mainMenu()
    }

    private suspend fun Dialogue.trainCrafting() {
        chatPlayer(quiz, "How can I train my crafting?")
        val level = player.baseCraftingLvl
        when {
            level < 10 -> beginnerAdvice()
            level < 20 -> intermediateAdvice()
            else -> advancedAdvice(level)
        }
    }

    private suspend fun Dialogue.beginnerAdvice() {
        chatNpc(
            neutral,
            "To get started, you might like to try your hand at crafting " +
                "some armour from cow's leather. There's a tanner just " +
                "over in Al Kharid who could tan any hides for you.",
        )
        chatNpc(
            neutral,
            "Once you've managed to get your hand on some kind of " +
                "leather, you just need a needle and some thread to get " +
                "to work!",
        )
        chatNpc(
            neutral,
            "If that doesn't take your fancy, you could try your hand at " +
                "spinning wool right here on the spinning wheel.",
        )
        chatNpc(
            neutral,
            "Come to think of it... Farmer Fred, just north of here, " +
                "sounds like he could use a hand shearing some of his " +
                "sheep. Maybe you could help him out, and get some " +
                "practice while you're at it.",
        )
        chatPlayer(happy, "Thanks!")
        anythingElse()
    }

    private suspend fun Dialogue.intermediateAdvice() {
        chatNpc(
            neutral,
            "Lots of budding craftsmen seem to spend their time here " +
                "spinning Flax into bow strings on the wheel, if that " +
                "takes your fancy.",
        )
        chatNpc(
            neutral,
            "Or you might like to try your hand at crafting some " +
                "armour from cow's leather. There's a tanner just over in " +
                "Al Kharid who could tan any hides for you.",
        )
        chatPlayer(happy, "Thanks!")
        anythingElse()
    }

    private suspend fun Dialogue.advancedAdvice(level: Int) {
        if (level >= 99) {
            chatNpc(
                laugh,
                "I can't help but feel like I should be the one asking you! " +
                    "At this point, the world's your oyster!",
            )
        } else {
            chatNpc(
                happy,
                "Now that you've got a little experience under your belt, the world's your oyster!",
            )
        }
        chatNpc(
            neutral,
            "If you wanted to try your hand at making some jewellery, " +
                "you'll want to get your hand on some gold and some kind " +
                "of gem, with a mould to work with and a chisel at hand.",
        )
        chatNpc(
            neutral,
            "But if that's not your thing, maybe you'd like to try making some more armour!",
        )
        chatNpc(
            neutral,
            "Cow's leather's a good place to start, but you can try all " +
                "other kinds of hides, like a dragon's!",
        )
        chatNpc(
            neutral,
            "Glass blowing seems to be a popular trade, too. You can " +
                "forge molten glass from some sand and soda ash, then " +
                "make all kinds of things using a glass blowing pipe.",
        )
        chatPlayer(happy, "Thanks!")
        chatNpc(happy, "Is there anything else you want to know")
        mainMenu()
    }

    private suspend fun Dialogue.craftables() {
        chatPlayer(quiz, "What kinds of things can be crafted?")
        chatNpc(
            happy,
            "All kinds of things, really! You can make armour from " +
                "leather, make some pottery, try your hand at glass " +
                "blowing, make some jewellery, weapons even...",
        )
        chatNpc(happy, "What would you like to hear about?")
        craftablesMenu(askedAlready = false)
    }

    private suspend fun Dialogue.craftablesMenu(askedAlready: Boolean) {
        val choice =
            if (askedAlready) {
                // The game lists six options here; the dialogue api supports at most five, so
                // the last two are split onto a second page.
                choice5(
                        "Tell me about crafting armour.",
                        1,
                        "Tell me about pottery.",
                        2,
                        "Tell me about glass blowing.",
                        3,
                        "Tell me about making jewellery.",
                        4,
                        "More...",
                        0,
                    )
                    .let {
                        if (it != 0) {
                            it
                        } else {
                            choice2("Tell me about crafting weapons.", 5, "Not right now.", 6)
                        }
                    }
            } else {
                choice5(
                    "Tell me about crafting armour.",
                    1,
                    "Tell me about pottery.",
                    2,
                    "Tell me about glass blowing.",
                    3,
                    "Tell me about making jewellery.",
                    4,
                    "Tell me about crafting weapons.",
                    5,
                )
            }
        when (choice) {
            1 -> armour()
            2 -> pottery()
            3 -> glassBlowing()
            4 -> jewellery()
            5 -> weapons()
            6 -> notRightNow()
        }
    }

    private suspend fun Dialogue.anythingElseMentioned() {
        chatNpc(happy, "Would you like to hear about anything else I mentioned?")
        craftablesMenu(askedAlready = true)
    }

    private suspend fun Dialogue.armour() {
        chatPlayer(quiz, "Tell me about crafting armour.")
        chatNpc(
            happy,
            "Sure thing! Most armour you can craft just involves " +
                "taking a needle and some thread to whatever material " +
                "you can get your hands on.",
        )
        chatNpc(
            neutral,
            "You might like to try with different kinds of animal hide, " +
                "from cows, yaks, snakes, dragons... Or any other sort of " +
                "fabric you can find!",
        )
        chatNpc(
            neutral,
            "Some crafty types have started making some leather- " +
                "covered wooden shields recently, too!",
        )
        chatNpc(
            neutral,
            "You might find some creatures make for a pretty sturdy " +
                "helmet, too. The best way to make something like that's " +
                "just to take a good old chisel to it!",
        )
        chatPlayer(happy, "That sounds great.")
        anythingElseMentioned()
    }

    private suspend fun Dialogue.pottery() {
        chatPlayer(quiz, "Tell me about pottery.")
        chatNpc(
            happy,
            "Of course! Pottery's all kind of the same, it just takes a lot of getting used to.",
        )
        chatNpc(
            neutral,
            "All that there really is to it is getting your hands on " +
                "some clay, getting it wet and getting to work on a " +
                "potter's wheel.",
        )
        chatNpc(
            neutral,
            "Once you've got the shape you want out of it, you want to " +
                "put it in a pottery oven to fire it up.",
        )
        chatNpc(
            neutral,
            "The barbarians west of Varrock seem to be pretty keen on " +
                "their pottery. I'd take a look around there, if you " +
                "wanted to try it out!",
        )
        chatNpc(
            neutral,
            "Once you get the hang of it, you can make pots, pie dishes, bowls... You know.",
        )
        chatPlayer(happy, "Neat!")
        anythingElseMentioned()
    }

    private suspend fun Dialogue.glassBlowing() {
        chatPlayer(quiz, "Tell me about glass blowing.")
        chatNpc(
            neutral,
            "Glass blowing? Well, to get started you'll need to get " +
                "yourself some molten glass. You can make some for " +
                "yourself by heating sand and soda ash in a furnace.",
        )
        chatNpc(
            neutral,
            "Once you've got a hold of that, you'll want a glass " +
                "blowing pipe to blow it out in to shape, before it cools " +
                "down.",
        )
        chatNpc(
            happy,
            "It's not the easiest thing to get to grips with, but it shouldn't be too difficult!",
        )
        chatPlayer(happy, "Nifty.")
        anythingElseMentioned()
    }

    private suspend fun Dialogue.jewellery() {
        chatPlayer(quiz, "Tell me about making jewellery.")
        chatNpc(
            neutral,
            "Sure. All it boils down to is a bar of gold or silver, some " +
                "kind of cut gem if you're feeling fancy, and a mould to " +
                "help shape the thing.",
        )
        chatNpc(
            neutral,
            "You can use a chisel to make a gem really shine and " +
                "sparkle. You wouldn't want to use an uncut one for any " +
                "jewellery.",
        )
        chatNpc(
            neutral,
            "Once you've got all of your materials together, you'll want " +
                "to use a furnace to actually craft whatever it is you're " +
                "trying to make!",
        )
        chatNpc(
            neutral,
            "Wizards seem to like enchanting their jewellery to do all " +
                "kinds of things, too, but I don't know a lot about that.",
        )
        chatPlayer(happy, "Sounds good!")
        anythingElseMentioned()
    }

    private suspend fun Dialogue.weapons() {
        chatPlayer(quiz, "Tell me about crafting weapons.")
        chatNpc(
            neutral,
            "Well, there aren't too many weapons that people tend to " +
                "craft, but there are a couple worth noting!",
        )
        chatNpc(
            neutral,
            "Battlestaves are probably the most common. It boils down " +
                "to fastening an orb of some kind onto the end of a " +
                "battlestaff.",
        )
        chatNpc(
            neutral,
            "You do need to get the orb in the first place, though... " +
                "You can make one through glass blowing, but it's not " +
                "much use without being charged.",
        )
        chatNpc(
            neutral,
            "I can't help much with crafting it, though! There's a little " +
                "more magic involved there than I'm familiar with.",
        )
        chatNpc(
            neutral,
            "Aside from battlestaves, there are a couple of weapons " +
                "that people like to make from silver... Sickles and bolts, " +
                "usually.",
        )
        chatPlayer(quiz, "Interesting...")
        anythingElseMentioned()
    }

    private suspend fun Dialogue.notRightNow() {
        chatPlayer(neutral, "Not right now.")
        chatNpc(happy, "Is there anything else you want to know?")
        val choice = choice2("How can I train my crafting?", 1, "No, thank you.", 2)
        when (choice) {
            1 -> trainCrafting()
            2 -> noThanks()
        }
    }

    private suspend fun Dialogue.noThanks() {
        chatPlayer(neutral, "No, thank you.")
        chatNpc(happy, "Well, just come back any time you want to know anything!")
    }
}
