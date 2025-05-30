package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.config.refs.BaseVarBits
import org.rsmod.api.config.refs.BaseVarps
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DonieAndGee : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.gee) { startDialogue(it.npc) }
        onOpNpc1(lumbridge_npcs.donie) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello there, can I help you?")
            when (random.of(maxExclusive = 5)) {
                0 -> randomDialogue1()
                1 -> randomDialogue2()
                2 -> randomDialogue3()
                3 -> randomDialogue4()
                4 -> randomDialogue5()
            }
        }

    private suspend fun Dialogue.randomDialogue1() {
        val nextDialogue =
            choice3(
                "What's up?",
                ::whatsUp,
                "Are there any quests I can do here?",
                ::questsToDo,
                "Can I buy your stick?",
                ::buyStick,
            )
        nextDialogue()
    }

    private suspend fun Dialogue.randomDialogue2() {
        val nextDialogue =
            choice3(
                "Do you have anything of value which I can have?",
                ::freeStuff,
                "Are there any quests I can do here?",
                ::questsToDo,
                "Can I buy your stick?",
                ::buyStick,
            )
        nextDialogue()
    }

    private suspend fun Dialogue.randomDialogue3() {
        val nextDialogue =
            choice4(
                "What's up?",
                ::whatsUp,
                "Are there any quests I can do here?",
                ::questsToDo,
                "Can I buy your stick?",
                ::buyStick,
                "Your shoe lace is untied.",
                ::untiedShoeLace,
            )
        nextDialogue()
    }

    private suspend fun Dialogue.randomDialogue4() {
        val nextDialogue =
            choice4(
                "Where am I?",
                ::whereAmI,
                "How are you today?",
                ::howAreYouToday,
                "Are there any quests I can do here?",
                ::questsToDo,
                "Your shoe lace is untied.",
                ::untiedShoeLace,
            )
        nextDialogue()
    }

    private suspend fun Dialogue.randomDialogue5() {
        val nextDialogue =
            choice4(
                "Where am I?",
                ::whereAmI,
                "How are you today?",
                ::howAreYouToday,
                "Are there any quests I can do here?",
                ::questsToDo,
                "Where can I get a haircut like yours?",
                ::haircutRecommendation,
            )
        nextDialogue()
    }
}

/* NOTE: any "extra" whitespaces in dialogues below are "correct." */

private suspend fun Dialogue.whatsUp() {
    chatPlayer(quiz, "What's up?")
    chatNpc(quiz, "I assume the sky is up..")
    chatPlayer(quiz, "You assume?")
    chatNpc(quiz, "Yeah, unfortunately I don't seem to be able to look up.")
}

private suspend fun Dialogue.whereAmI() {
    chatPlayer(quiz, "Where am I?")
    chatNpc(laugh, "This is the town of Lumbridge my friend.")
    val nextDialogue =
        choice3(
            "How are you today?",
            ::howAreYouToday,
            "Do you know of any quests I can do?",
            ::questsToDo,
            "Your shoe lace is untied.",
            ::untiedShoeLace,
        )
    nextDialogue()
}

private suspend fun Dialogue.howAreYouToday() {
    chatPlayer(happy, "How are you today?")
    chatNpc(happy, "Aye, not too bad thank you. Lovely weather in Gielinor this fine day.")
    chatPlayer(laugh, "Weather?")
    chatNpc(laugh, "Yes weather, you know.")
    chatNpc(
        quiz,
        "The state or condition of the atmosphere at a time and " +
            "place, with respect to variables such as temperature, " +
            "moisture, wind velocity, and barometric pressure.",
    )
    chatPlayer(quiz, "...")
    chatNpc(laugh, "Not just a pretty face eh? Ha ha ha.")
}

private suspend fun Dialogue.freeStuff() {
    chatPlayer(quiz, "Do you have anything of value which I can have?")
    chatNpc(quiz, "Are you asking for free stuff?")
    chatPlayer(quiz, "Well... er... yes.")
    chatNpc(
        angry,
        "No I do not have anything I can give you. If I did " +
            "have anything of value I wouldn't want to give it away.",
    )
}

private suspend fun Dialogue.buyStick() {
    chatPlayer(quiz, "Can I buy your stick?")
    chatNpc(angry, "It's not a stick! I'll have you know it's a very powerful staff!")
    chatPlayer(quiz, "Really? Show me what it can do!")
    chatNpc(sad, "Um..It's a bit low on power at the moment..")
    chatPlayer(laugh, "It's a stick isn't it?")
    chatNpc(
        sad,
        "...Ok it's a stick.. But only while I save up for a staff. " +
            "Zaff in Varrock square sells them in his shop.",
    )
    chatPlayer(laugh, "Well good luck with that.")
}

private suspend fun Dialogue.questsToDo() {
    chatPlayer(quiz, "Do you know of any quests I can do?")
    chatNpc(quiz, "What kind of quest are you looking for?")
    val nextDialogue =
        choice5(
            "I fancy a bit of a fight, anything dangerous?",
            ::dangerousQuests,
            "Something easy please, I'm new here.",
            ::easyQuests,
            "I'm a thinker rather than fighter; anything skill oriented?",
            ::thinkingQuests,
            "I want to do all kinds of things, do you know anything like that?",
            ::allQuests,
            "Maybe another time.",
            ::maybeAnotherTime,
        )
    nextDialogue()
}

private suspend fun Dialogue.dangerousQuests() {
    chatPlayer(happy, "I fancy a bit of a fight, anything dangerous?")
    chatNpc(quiz, "Hmm.. dangerous you say? What sort of creatures are you looking to fight?")
    val nextDialogue =
        choice4(
            "Big scary demons!",
            ::bigScaryDemonQuests,
            "Vampyres!",
            ::vampyreQuests,
            "Small.. something small would be good.",
            ::smallMonsterQuests,
            "Maybe another time.",
            ::maybeAnotherTime,
        )
    nextDialogue()
}

private suspend fun Dialogue.bigScaryDemonQuests() {
    chatPlayer(happy, "Big scary demons!")
    chatNpc(laugh, "You are a brave soul indeed.")
    chatNpc(
        quiz,
        "Now that you mention it, I heard a rumour about a " +
            "fortune-teller in Varrock who is rambling about some " +
            "kind of greater evil.. sounds demon-like if you ask me.",
    )
    chatNpc(quiz, "Perhaps you could check it out if you are as brave as you say?")
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarBits.demonslayer_main]
    if (progress == 0) {
        chatPlayer(happy, "Thanks for the tip, perhaps I will.")
    } else {
        chatPlayer(
            laugh,
            "I've already killed the demon Delrith. He was merely a " +
                "stain on my sword when I was finished with him!",
        )
        chatNpc(
            happy,
            "Well done! However I'm sure if you search around the " +
                "world you will find more challenging foes to slay.",
        )
    }
}

private suspend fun Dialogue.vampyreQuests() {
    // Seems like a null anim by mistake - they may fix in the future.
    chatPlayerNoAnim("Vampyres!")
    chatNpc(
        laugh,
        "Ha ha. I personally don't believe in such things. " +
            "However, there is a man in Draynor Village who has " +
            "been scaring the village folk with stories of vampyres.",
    )
    chatNpc(
        happy,
        "He's named Morgan and can be found in one of the " +
            "village houses. Perhaps you could see what the matter " +
            "is?",
    )
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.vampire]
    if (progress == 0) {
        chatPlayer(happy, "Thanks for the tip.")
    } else {
        chatPlayer(
            laugh,
            "Oh I have already killed that nasty blood-sucking " +
                "vampyre. Draynor will be safe now.",
        )
        chatNpc(laugh, "Yeah, yeah of course you did. Everyone knows vampyres are not real....")
        chatPlayer(angry, "What! I did slay the beast..I really did.")
        chatNpc(laugh, "You're not fooling anyone you know.")
        chatPlayer(angry, "..Huh.. But... Hey! I did... believe what you like.")
    }
}

private suspend fun Dialogue.smallMonsterQuests() {
    chatPlayer(happy, "Small.. something small would be good.")
    chatNpc(quiz, "Small? Small isn't really that dangerous though is it?")
    chatPlayer(
        angry,
        "Yes it can be! There could be anything from an evil " +
            "chicken to a poisonous spider. They attack in numbers you know!",
    )
    chatNpc(
        happy,
        "Yes ok, point taken. Speaking of small monsters, I hear " +
            "old Wizard Mizgog in the wizards' tower has just had " +
            "all his beads taken by a gang of mischievous imps.",
    )
    chatNpc(happy, " Sounds like it could be a quest for you?")
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.imp]
    if (progress == 0) {
        chatPlayer(happy, "Thanks for the help.")
    } else {
        chatPlayer(
            happy,
            "Yes I know of Mizgog and have already helped him with " +
                "his imp problem. It took me ages to find those beads!",
        )
        chatNpc(laugh, "Imps will be imps!")
    }
}

private suspend fun Dialogue.easyQuests() {
    chatPlayer(happy, "Something easy please, I'm new here.")
    chatNpc(happy, "I can tell you about plenty of small easy tasks.")
    chatNpc(
        quiz,
        "The Lumbridge cook has been having problems, the " +
            "Duke is confused over some strange talisman and on " +
            "top of all that, poor lad Romeo in Varrock has girlfriend " +
            "problems.",
    )
    val nextDialogue =
        choice4(
            "The Lumbridge cook.",
            ::lumbridgeCookQuest,
            "The Duke's strange talisman.",
            ::dukeStrangeTalismanQuest,
            "Romeo and his girlfriend.",
            ::romeoQuest,
            "Maybe another time.",
            ::maybeAnotherTime,
        )
    nextDialogue()
}

private suspend fun Dialogue.lumbridgeCookQuest() {
    chatPlayer(quiz, "Tell me about the Lumbridge cook.")
    chatNpc(
        laugh,
        "It's funny really, the cook would forget his head if it " +
            "wasn't screwed on. This time he forgot to get " +
            "ingredients for the Duke's birthday cake. ",
    )
    chatNpc(
        quiz,
        "Perhaps you could help him? You will probably find him " +
            "in the Lumbridge Castle kitchen.",
    )
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.cookquest]
    if (progress == 0) {
        chatPlayer(happy, "Thank you. I shall go speak with him.")
    } else {
        chatPlayer(happy, "I have already helped the cook in Lumbridge.")
        chatNpc(happy, "Oh yes, so you have. I am sure the Duke will be pleased.")
    }
}

private suspend fun Dialogue.dukeStrangeTalismanQuest() {
    chatPlayer(happy, "Tell me about the Duke's strange talisman.")
    chatNpc(
        happy,
        "Well the Duke of Lumbridge has found a strange " +
            "talisman that no one seems to understand. Perhaps you " +
            "could help him? You can probably find him upstairs in " +
            "Lumbridge Castle.",
    )
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.runemysteries]
    if (progress == 0) {
        chatPlayer(quiz, "Sounds mysterious. I may just do that. Thanks.")
    } else {
        chatPlayer(happy, "Yes, I have already solved the rune mysteries.")
        chatNpc(happy, "Ah excellent. Thank you very much adventurer.")
    }
}

private suspend fun Dialogue.romeoQuest() {
    chatPlayer(happy, "Tell me about Romeo and his girlfriend please.")
    chatNpc(
        happy,
        "Romeo in Varrock needs help with finding his beloved " +
            "Juliet, you may be able to help him out. ",
    )
    chatNpc(
        laugh,
        " Unless of course you manage to find Juliet first in " +
            "which case she has probably lost Romeo.",
    )
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.rjquest]
    if (progress == 0) {
        chatPlayer(quiz, "Right, ok. Romeo is in Varrock?")
        chatNpc(happy, "Yes you can't miss him, he's wandering aimlessly in the square.")
    } else {
        chatPlayer(happy, "Oh yes, I've already helped Romeo in the best possible way I can...")
        chatNpc(quiz, "Really?")
        chatPlayer(happy, "Yup.")
        chatNpc(quiz, "...How?")
        chatPlayer(laugh, "He thinks Juliet is dead...")
        chatNpc(quiz, "Well.. ok.. well done... I think...")
    }
}

private suspend fun Dialogue.thinkingQuests() {
    chatPlayer(happy, "I'm a thinker rather than fighter, anything skill orientated?")
    chatNpc(
        quiz,
        "Skills play a big part when you want to progress in " +
            "knowledge throughout Gielinor. I know of a few skill- " +
            "related quests that can get you started.",
    )
    chatNpc(
        happy,
        "You may be able to help out Fred the farmer who is in " +
            "need of someones crafting expertise.",
    )
    chatNpc(happy, "Or, there's always Doric the dwarf who needs an errand running for him?")
    val nextDialogue =
        choice3(
            "Fred the farmer.",
            ::fredFarmerQuest,
            "Doric the dwarf.",
            ::doricDwarfQuest,
            "Maybe another time.",
            ::maybeAnotherTime,
            title = "Tell me about..",
        )
    nextDialogue()
}

private suspend fun Dialogue.fredFarmerQuest() {
    chatPlayer(happy, "Tell me about Fred the farmer please.")
    chatNpc(
        quiz,
        "You can find Fred next to the field of sheep in " +
            "Lumbridge. Perhaps you should go and speak with him.",
    )
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.sheep]
    if (progress == 0) {
        chatPlayer(happy, "Thanks, maybe I will.")
    } else {
        chatPlayer(
            happy,
            "I have already helped Fred the farmer. I sheared his " +
                "sheep and made 20 balls of wool for him.",
        )
        chatPlayer(sad, " He wouldn't let me kill his chickens though.")
        chatNpc(laugh, "Lumbridge chickens do make good target practice.")
        chatNpc(happy, " You will have to wait until he isn't looking.")
    }
}

private suspend fun Dialogue.doricDwarfQuest() {
    chatPlayer(happy, "Tell me about Doric the dwarf.")
    chatNpc(
        happy,
        "Doric the dwarf is located north of Falador. He might " +
            "be able to help you with smithing. You should speak to " +
            "him. He may let you use his anvils.",
    )
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.doricquest]
    if (progress == 0) {
        chatPlayer(happy, "Thanks for the tip.")
    } else {
        chatPlayer(
            happy,
            "Yes, I've been to see Doric already. He was happy to " +
                "let me use his anvils after I ran a small errand for him.",
        )
        chatNpc(happy, "Oh good, Thank you ${player.displayName}!")
    }
}

private suspend fun Dialogue.allQuests() {
    chatPlayer(happy, "I want to do all kinds of things, do you know of anything like that?")
    chatNpc(happy, "Of course I do. Gielinor is a huge place you know, now let me think...")
    chatNpc(
        happy,
        "Hetty the witch in Rimmington might be able to offer " +
            "help in the ways of magical abilities..",
    )
    chatNpc(
        happy,
        "Also, pirates are currently docked in Port Sarim, " +
            "Where pirates are, treasure is never far away...",
    )
    chatNpc(
        happy,
        "Or you could go help out Ernest who got lost in Draynor Manor, spooky place that.",
    )
    val nextDialogue =
        choice4(
            "Hetty the Witch.",
            ::hettyWitchQuest,
            "Pirate's treasure.",
            ::pirateTreasureQuest,
            "Ernest and Draynor Manor.",
            ::ernestChickenQuest,
            "Maybe another time.",
            ::maybeAnotherTime,
            title = "Tell me about..",
        )
    nextDialogue()
}

private suspend fun Dialogue.hettyWitchQuest() {
    chatPlayer(happy, "Tell me about Hetty the witch.")
    chatNpc(
        happy,
        "Hetty the witch can be found in Rimmington, south of " +
            "Falador. She's currently working on some new potions. " +
            "Perhaps you could give her a hand? She might be able " +
            "to offer help with your magical abilities.",
    )
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.hetty]
    if (progress == 0) {
        chatPlayer(happy, "Ok thanks, let's hope she doesn't turn me into a potato or something..")
    } else {
        chatPlayer(
            happy,
            "Yes, I have already been to see Hetty, she gave me " +
                "super cosmic powers after I helped out with her potion! " +
                "I could probably destroy you with a single thought!",
        )
        chatNpc(worried, "Did she really?")
        chatPlayer(laugh, "No not really...")
        chatNpc(angry, "Right.....")
    }
}

private suspend fun Dialogue.pirateTreasureQuest() {
    chatPlayer(happy, "Tell me about Pirate's Treasure.")
    chatNpc(
        happy,
        "RedBeard Frank in Port Sarim's bar, the Rusty " +
            "Anchor, might be able to tell you about the rumoured " +
            "treasure that is buried somewhere in Gielinor.",
    )
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.hunt]
    if (progress == 0) {
        chatPlayer(happy, "Sounds adventurous, I may have to check that out. Thank you.")
    } else {
        chatPlayer(angry, "Yarr! I already found the booty!")
        chatNpc(laugh, "Yarr indeed my friend. A most excellent find.")
        chatPlayer(angry, "Yarr!")
        chatNpc(laugh, "Yarrr!")
        chatPlayer(angry, "YARRR!")
        chatNpc(angry, "Right, that's enough of that!")
        chatPlayer(sad, "..Sorry.")
    }
}

private suspend fun Dialogue.ernestChickenQuest() {
    chatPlayer(happy, "Tell me about Ernest please.")
    chatNpc(
        happy,
        "The best place to start would be at the gate to " +
            "Draynor Manor. There you will find Veronica who will " +
            "be able to tell you more.",
    )
    chatNpc(happy, "I suggest you tread carefully in that place; it's haunted.")
    // Note: dialogue should be different when the quest has been started but not completed.
    val progress = vars[BaseVarps.haunted]
    if (progress == 0) {
        chatPlayer(quiz, "Sounds like fun. I've never been to a Haunted Manor before.")
    } else {
        chatPlayer(
            happy,
            "Yeah, I found Ernest already. Professor Oddenstein had turned him into a chicken!",
        )
        chatNpc(laugh, "A chicken!?")
        chatPlayer(happy, "Yeah a chicken. It could have been worse though.")
        chatNpc(laugh, "Very true, poor guy.")
    }
}

private suspend fun Dialogue.maybeAnotherTime() {
    chatPlayer(happy, "Maybe another time.")
}

private suspend fun Dialogue.untiedShoeLace() {
    chatPlayer(laugh, "Your shoe lace is untied.")
    chatNpc(angry, "No, it's not!")
    chatPlayer(laugh, "No, you're right. I have nothing to back that up.")
    chatNpc(angry, "Fool! Leave me alone!")
}

private suspend fun Dialogue.haircutRecommendation() {
    chatPlayer(quiz, "Where can I get a haircut like yours?")
    chatNpc(happy, "Yes, it does look like you need a hairdresser.")
    chatPlayer(angry, "Oh thanks!")
    chatNpc(laugh, "No problem. The hairdresser in Falador will probably be able to sort you out.")
    chatNpc(happy, "The Lumbridge general store sells useful maps if you don't know the way.")
}
