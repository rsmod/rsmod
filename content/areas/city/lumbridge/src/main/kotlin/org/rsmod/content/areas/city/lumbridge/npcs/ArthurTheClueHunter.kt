package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.random.GameRandom
import org.rsmod.api.script.onAiTimer
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeNpcs
import org.rsmod.content.areas.city.lumbridge.configs.clueScrollDisableVessels
import org.rsmod.game.entity.Npc
import org.rsmod.game.map.Direction
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class ArthurTheClueHunter @Inject constructor(private val random: GameRandom) : PluginScript() {
    override fun ScriptContext.startUp() {
        onAiTimer(LumbridgeNpcs.arthur_the_clue_hunter) { npc.shout() }
        onOpNpc1(LumbridgeNpcs.arthur_the_clue_hunter) { startDialogue(it.npc) }
    }

    private fun Npc.shout() {
        faceDirection(Direction.North)
        when (random.of(maxExclusive = 5)) {
            0 -> {
                say("Why did I not think of that before.")
                anim(seqs.emote_slap_head)
            }
            1 -> {
                say("Hmm.... What could this mean?")
                anim(seqs.emote_shrug)
            }
            2 -> {
                say("I'm going to be rich!")
                anim(seqs.emote_jump_with_joy)
            }
            3 -> {
                say("I've got it!")
                anim(seqs.emote_lightbulb)
                // TODO: spotanim(712)
            }
            4 -> {
                say("Why is this so hard...")
                anim(seqs.emote_stampfeet)
            }
        }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(neutral, "What can I do for you?")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice5(
                "Ask about beginner clues.",
                1,
                "Ask about more advanced clues.",
                2,
                "Ask about the types of clues.",
                3,
                "Toggle gaining vesseled clue scrolls.",
                4,
                "Goodbye.",
                5,
            )
        when (choice) {
            1 -> beginnerCluesInquiry()
            2 -> advancedCluesInquiry()
            3 -> clueTypesInquiry()
            4 -> toggleVesselClueScrolls()
            5 -> goodbye()
        }
    }

    private suspend fun Dialogue.beginnerCluesInquiry() {
        chatPlayer(quiz, "What are beginner clues?")
        mesbox("Arthur's eyes light up.")
        chatNpc(
            happy,
            "Beginner clues are the lowest tier of clue scroll and as " +
                "such only require a couple of steps to solve.",
        )
        chatNpc(
            happy,
            "Beginner clues are gained through various activities. " +
                "Those activities are as follows.",
        )
        chatNpc(
            happy,
            "Slaying monsters for a chance at a clue being dropped. " +
                "I hear goblins are sneaky, maybe they have some clue " +
                "scrolls on them ripe for the taking.",
        )
        chatNpc(
            happy,
            "Maybe someone left one in the cow field, who knows where you could obtain them.",
        )
        chatNpc(
            happy,
            "Beginner clues have also been known to be left in " +
                "geodes while mining, as well as sometimes in bottles that " +
                "you may fish up from time to time and from nests " +
                "which fall out of trees.",
        )
        chatNpc(sad, "Although this is a much rarer occurrence.")
        chatNpc(
            sad,
            "Unlike more advanced clues, you should not expect to " +
                "get very valuable items from completing a beginner clue.",
        )
        mainMenu()
    }

    private suspend fun Dialogue.advancedCluesInquiry() {
        chatPlayer(quiz, "What are more advanced clues?")
        chatNpc(happy, "Advanced clues have tiers between easy and master.")
        chatNpc(
            happy,
            "You can gain advanced clues the same way you would " +
                "get a beginner clue. But to get higher tier scrolls you " +
                "will need to defeat more powerful foes.",
        )
        chatNpc(
            happy,
            "The chance to get a higher tier clue from geodes, " +
                "bottles and nests is lower the higher the tier. But if you " +
                "chop down higher-level trees you will have a higher " +
                "chance of receiving a nest which contains a clue. Same " +
                "with fishing and mining.",
        )
        chatNpc(sad, "The only exception to this is master clue scrolls.")
        chatNpc(
            neutral,
            "Master clues can only be obtained as a rare chance " +
                "when opening your casket or by handing in one of " +
                "every type of clue, apart from beginner, to Watson who " +
                "resides in Hosidius.",
        )
        chatNpc(happy, "But with more advanced tiers of clue, come better loot.")
        mainMenu()
    }

    private suspend fun Dialogue.clueTypesInquiry() {
        clueTypesPg1()
    }

    private suspend fun Dialogue.clueTypesPg1() {
        val choice =
            choice5(
                "Anagrams.",
                1,
                "Challenge scrolls.",
                2,
                "Cipher clues.",
                3,
                "Coordinate clues.",
                4,
                "More.",
                5,
            )
        when (choice) {
            1 -> {
                anagramExplanation()
                clueTypesPg1()
            }
            2 -> {
                challengeExplanation()
                clueTypesPg1()
            }
            3 -> {
                cipherExplanation()
                clueTypesPg1()
            }
            4 -> {
                coordinateExplanation()
                clueTypesPg1()
            }
            5 -> clueTypesPg2()
        }
    }

    private suspend fun Dialogue.anagramExplanation() {
        chatNpc(
            neutral,
            "This type of clue has you talk to someone around " +
                "Gielinor. Although the clue does not tell you exactly who " +
                "to talk to.",
        )
        chatNpc(
            neutral,
            "You will see a couple of words that may or may not " +
                "make sense together. You will have to rearrange the " +
                "letters to make the name of the person you need to talk " +
                "to.",
        )
    }

    private suspend fun Dialogue.challengeExplanation() {
        chatNpc(
            neutral,
            "This type of clue has you work out a logical problem " +
                "for the person you need to talk to. This type of " +
                "challenge only appears in clue tiers medium, hard and " +
                "elite.",
        )
        chatNpc(
            neutral,
            "There are also challenges that will involve you gathering " +
                "an item or crafting an item for a certain person.",
        )
    }

    private suspend fun Dialogue.cipherExplanation() {
        chatNpc(
            neutral,
            "This type of clues has you talk to someone around " +
                "Gielinor. Although the clue does not tell you exactly who " +
                "to talk to. This type of challenge only appears in clue " +
                "tiers medium, hard and elite.",
        )
        chatNpc(
            neutral,
            "A cipher is a string of letters that have been scrambled " +
                "using the shift cipher method.",
        )
        chatNpc(
            neutral,
            "An example of this would be ZCZL which, shifted forward by one, would be Adam.",
        )
    }

    private suspend fun Dialogue.coordinateExplanation() {
        chatNpc(
            neutral,
            "This type of clue will have you use a sextant, chart and " +
                "a watch to find out the place you will need to dig. This " +
                "type of challenge only appears in clue tiers medium and " +
                "above.",
        )
        chatNpc(
            neutral,
            "After you find the place you need to dig, you may find a casket or another scroll.",
        )
    }

    private suspend fun Dialogue.clueTypesPg2() {
        val choice =
            choice5(
                "Cryptic clues.",
                1,
                "Emote clues.",
                2,
                "Hot Cold clues.",
                3,
                "Puzzle clues.",
                4,
                "More.",
                5,
            )
        when (choice) {
            1 -> crypticExplanation()
            2 -> emoteExplanation()
            3 -> hotColdExplanation()
            4 -> puzzleExplanation()
            5 -> clueTypesPg3()
        }
    }

    private suspend fun Dialogue.crypticExplanation() {
        chatNpc(
            neutral,
            "This type of clue can be as simple as talk to the " +
                "specified person or searching a crate in specified " +
                "location.",
        )
        chatNpc(
            neutral,
            "But the higher the tier of clue, the more steps you will " +
                "have to do beforehand. An example of this would be " +
                "needing to find a key before opening a chest.",
        )
        clueTypesPg2()
    }

    private suspend fun Dialogue.emoteExplanation() {
        chatNpc(
            neutral,
            "This type of clue will give you a location that you will " +
                "need to visit, along with some items you will need.",
        )
        chatNpc(
            neutral,
            "At this location you will need to perform an emote, as " +
                "well as wear the items that you had to bring along.",
        )
        chatNpc(neutral, "Some clues may have you wear nothing at all.")
        chatNpc(worried, "But please don't do that in public!")
        chatNpc(neutral, "This type of challenge only appears in clue tiers medium and above.")
        clueTypesPg2()
    }

    private suspend fun Dialogue.hotColdExplanation() {
        chatNpc(
            neutral,
            "Depending on the tier of clue, you will need to obtain a " +
                "strange device from either Reldo, located in Varrock, or " +
                "Jorral, located south of Tree Gnome Stronghold.",
        )
        chatNpc(
            neutral,
            "You will then need to use this device to find the location you will need to dig.",
        )
        clueTypesPg2()
    }

    private suspend fun Dialogue.puzzleExplanation() {
        chatNpc(
            neutral,
            "There are many different puzzles that you can come " +
                "across while doing clues. These range from light boxes " +
                "to puzzle boxes.",
        )
        chatNpc(
            neutral,
            "Puzzle boxes are puzzles that will have you complete a " +
                "slider puzzle that, when complete, will look like an image " +
                "in game.",
        )
        chatNpc(
            neutral,
            "Light boxes are a puzzle which contains a 5 by 5 grid " +
                "of lights with some lights on and some lights off.",
        )
        chatNpc(
            neutral,
            "The puzzle will have you click the buttons below to turn " +
                "on or off some of the lights with the end goal being, " +
                "having all the lights turned on.",
        )
        clueTypesPg2()
    }

    private suspend fun Dialogue.clueTypesPg3() {
        val choice =
            choice4(
                "Map clues.",
                1,
                "Fairy ring clues.",
                2,
                "More.",
                3,
                "Ask about something else.",
                4,
            )
        when (choice) {
            1 -> mapExplanation()
            2 -> fairyRingExplanation()
            3 -> clueTypesPg1()
            4 -> mainMenu()
        }
    }

    private suspend fun Dialogue.mapExplanation() {
        chatNpc(neutral, "This type of clue will show you a map of an area.")
        chatNpc(
            neutral,
            "The map will show you where you will need to dig, or " +
                "what you will need to search to get the next clue.",
        )
        clueTypesPg3()
    }

    private suspend fun Dialogue.fairyRingExplanation() {
        chatNpc(
            neutral,
            "This type of clue will show you which fairy ring code " +
                "you will need to use to get to the right place to dig.",
        )
        chatNpc(
            neutral,
            "You will then need to use the numbers to figure out " +
                "where you will need to dig after arriving to your " +
                "destination.",
        )
        clueTypesPg3()
    }

    private suspend fun Dialogue.toggleVesselClueScrolls() {
        if (player.clueScrollDisableVessels) {
            enableVesselClueScrolls()
        } else {
            disableVesselClueScrolls()
        }
    }

    private suspend fun Dialogue.enableVesselClueScrolls() {
        chatPlayer(quiz, "Hey Arthur, can I start receiving clues from vessels?")
        chatNpc(
            happy,
            "Vessel containers include mining geodes, fishing bottles " +
                "and clue nests. Are you sure you want to start " +
                "receiving them, again?",
        )
        val confirm =
            choice2(
                "Yes please, I want to start receiving vesseled containers.",
                true,
                "Nevermind.",
                false,
            )
        if (!confirm) {
            mainMenu()
            return
        }
        chatPlayer(happy, "Yes please, I want to start receiving vesseled containers.")
        player.clueScrollDisableVessels = false
        chatNpc(sad, "Very well, you'll now find clues from mining, fishing and woodcutting.")
        chatNpc(neutral, "Is there anything else I can help you with?")
        mainMenu()
    }

    private suspend fun Dialogue.disableVesselClueScrolls() {
        chatPlayer(
            quiz,
            "Hey Arthur, can I stop receiving clues from vessels? " +
                "They're clogging up my backpack!",
        )
        chatNpc(
            happy,
            "Vessel containers include mining geodes, fishing bottles " +
                "and clue nests. Are you sure you want to stop " +
                "receiving them?",
        )
        val confirm =
            choice2(
                "Yes please, I want to stop receiving vesseled containers.",
                true,
                "Nevermind.",
                false,
            )
        if (!confirm) {
            mainMenu()
            return
        }
        chatPlayer(happy, "Yes please, I want to stop receiving vesseled containers.")
        player.clueScrollDisableVessels = true
        chatNpc(sad, "Very well, you'll no longer find clues from mining, fishing and woodcutting.")
        chatNpc(neutral, "Is there anything else I can help you with?")
        mainMenu()
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(neutral, "So long and happy clue hunting!")
    }
}
