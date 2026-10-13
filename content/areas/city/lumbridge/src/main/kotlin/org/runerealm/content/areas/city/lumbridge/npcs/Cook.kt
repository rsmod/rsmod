package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/** Dialogue for a player who has not yet started Cook's Assistant. */
class Cook : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.cook) { cookDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.cookDialogue(npc: Npc) = startDialogue(npc) { start() }
}

private suspend fun Dialogue.start() {
    chatNpc(sad, "What am I to do?")
    val next =
        choice4(
            "What's wrong?",
            ::whatsWrong,
            "You're a cook, why don't you bake me a cake?",
            ::bakeMeACake,
            "You don't look very happy.",
            ::notVeryHappy,
            "Nice hat!",
            ::niceHat,
        )
    next()
}

private suspend fun Dialogue.whatsWrong() {
    chatPlayer(quiz, "What's wrong?")
    chatNpc(
        worried,
        "Oh dear, oh dear, oh dear, I'm in a terrible terrible mess! It's the Duke's " +
            "birthday today, and I should be making him a lovely big birthday cake.",
    )
    chatNpc(
        worried,
        "I've forgotten to buy the ingredients. I'll never get them in time now. He'll " +
            "sack me! What will I do? I have four children and a goat to look after. " +
            "Would you help me? Please?",
    )
    val accept = choice2("Yes.", true, "No.", false, title = "Start the Cook's Assistant quest?")
    if (accept) {
        chatPlayer(happy, "Yes, I'll help you.")
        mesbox("This quest is not available yet.")
    } else {
        chatPlayer(neutral, "No, I don't feel like it. Maybe later.")
        chatNpc(
            angry,
            "Fine. I always knew you Adventurer types were callous beasts. Go on your " +
                "merry way!",
        )
    }
}

private suspend fun Dialogue.bakeMeACake() {
    chatPlayer(quiz, "You're a cook, why don't you bake me a cake?")
    chatNpc(sad, "*sniff* Don't talk to me about cakes...")
    whatsWrong()
}

private suspend fun Dialogue.notVeryHappy() {
    chatPlayer(neutral, "You don't look very happy.")
    chatNpc(
        sad,
        "No, I'm not. The world is caving in around me - I am overcome by dark feelings " +
            "of impending doom.",
    )
    val next =
        choice2(
            "What's wrong?",
            ::whatsWrong,
            "I'd take the rest of the day off if I were you.",
            ::dayOff,
        )
    next()
}

private suspend fun Dialogue.dayOff() {
    chatPlayer(neutral, "I'd take the rest of the day off if I were you.")
    chatNpc(worried, "No, that's the worst thing I could do. I'd get in terrible trouble.")
    chatPlayer(neutral, "Well maybe you need to take a holiday...")
    chatNpc(sad, "That would be nice, but the Duke doesn't allow holidays for core staff.")
    chatPlayer(quiz, "Hmm, why not run away to the sea and start a new life as a Pirate?")
    chatNpc(
        sad,
        "My wife gets sea sick, and I have an irrational fear of eyepatches. I don't see " +
            "it working myself.",
    )
    chatPlayer(sad, "I'm afraid I've run out of ideas.")
    chatNpc(sad, "I know I'm doomed.")
    whatsWrong()
}

private suspend fun Dialogue.niceHat() {
    chatPlayer(happy, "Nice hat!")
    chatNpc(neutral, "Err thank you. It's a pretty ordinary cooks hat really.")
    chatPlayer(happy, "Still, suits you. The trousers are pretty special too.")
    chatNpc(neutral, "Its all standard cook's issue uniform...")
    chatPlayer(
        happy,
        "The whole hat, apron, stripey trousers ensemble - it works. It make you looks " +
            "like a real cook.",
    )
    chatNpc(
        angry,
        "I am a real cook! I haven't got time to be chatting about Culinary Fashion. I am " +
            "in desperate need of help!",
    )
    whatsWrong()
}
