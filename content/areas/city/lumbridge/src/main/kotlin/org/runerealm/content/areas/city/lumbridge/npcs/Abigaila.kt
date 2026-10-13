package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.inv.Inventory
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Dialogue follows https://oldschool.runescape.wiki/w/Transcript:Abigaila and
 * https://oldschool.runescape.wiki/w/Abigaila.
 *
 * Scrying (spectating Theatre of Blood parties) is not implemented: the item/poison/disease checks
 * and party menu are in place, but observing a party ends with a "not available" message.
 */
class Abigaila : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.abigaila) { startDialogue(it.npc) { abigailaDialogue() } }
        onOpNpc3(lumbridge_npcs.abigaila) { startDialogue(it.npc) { scry() } }
    }
}

private suspend fun Dialogue.abigailaDialogue() {
    chatNpc(happy, "What can I help you with, dear?")
    while (true) {
        val option =
            choice3(
                "Who are you and what are you doing?",
                1,
                "I'd like to look in your scrying ball.",
                2,
                "I'll leave you be.",
                3,
            )
        when (option) {
            1 -> whoAreYou()
            2 -> {
                chatPlayer(quiz, "I'd like to look in your scrying ball.")
                scry()
                return
            }
            else -> {
                chatPlayer(neutral, "I'll leave you be.")
                return
            }
        }
    }
}

private suspend fun Dialogue.whoAreYou() {
    chatPlayer(quiz, "Who are you and what are you doing?")
    chatNpc(
        neutral,
        "I'm Abigaila, from Morytania. It's a terrible place, run by the vampyres. They " +
            "visit the towns and collect everyone's blood for food!",
    )
    chatNpc(
        neutral,
        "I tried for years to escape, with my son, Suro, to make a better life for " +
            "ourselves in Misthalin. I scraped together enough money to bribe a boatman to " +
            "get us out.",
    )
    chatNpc(
        sad,
        "The journey was tough. Some men in pink hoods attacked our boat, shouting at us " +
            "to go back where we belonged, and to keep out of their country. In the " +
            "struggle, the boat overturned.",
    )
    chatNpc(
        sad,
        "It was dark, and I couldn't find Suro in the water. I swam as deep as I could, " +
            "until my lungs screamed, but I couldn't find him. He wasn't good at swimming.",
    )
    chatNpc(
        sad,
        "Eventually I got myself to the shore. The sun was rising, and I found Suro there. " +
            "On the shore, where the water had dumped his... body.",
    )
    chatNpc(sad, "<col=0000ff>*sob*</col>")
    chatNpc(
        neutral,
        "I've done alright in Misthalin, I guess. I know a bit of magic, and the local " +
            "gentry pay me well for my scrying skills. I'm good at turning a profit on Grand " +
            "Exchange trades too.",
    )
    chatNpc(
        neutral,
        "Anyway, I'm able to rent a room in Lumbridge, and to buy food. I'm not being a " +
            "burden on anyone, and there are no vampyres coming for my blood.",
    )
    chatNpc(happy, "I am free!")
    chatNpc(
        neutral,
        "But I haven't forgotten my people in the east. There's one vampyre there who runs " +
            "a place she calls the Theatre of Blood, challenging humans to face monsters in " +
            "her arena.",
    )
    chatNpc(
        sad,
        "My husband went there, trying to win freedom from the blood-gatherers by " +
            "surviving her arena, but he never returned. No-one does.",
    )
    chatNpc(
        neutral,
        "So now I pass my spare time scrying on the Theatre of Blood, watching other " +
            "adventurers there. You can have a look, if you like.",
    )
}

private suspend fun Dialogue.scry() {
    if (!canScry()) {
        chatNpc(
            neutral,
            "I'm afraid I can't let you use my scrying ball now, dear. Clear out everything " +
                "you're carrying, and make sure you're not sick or poisoned.",
        )
        return
    }
    val option =
        choice3(
            "Observe a specific party",
            1,
            "Observe recent party",
            2,
            "Cancel",
            3,
            title = "Select an Option",
        )
    if (option != 3) {
        // TODO(content): Theatre of Blood spectating.
        mesbox("Theatre of Blood spectating is not available yet.")
    }
}

private fun Dialogue.canScry(): Boolean {
    if (vars[abigaila_varps.poison] > 0 || vars[abigaila_varps.disease] > 0) {
        return false
    }
    return player.inv.isEmptyExceptOculus() && player.worn.isEmptyExceptOculus()
}

/** The oculus orb is the only item the wiki says may be carried while scrying. */
private fun Inventory.isEmptyExceptOculus(): Boolean =
    objs.all { it == null || it.id == abigaila_objs.oculus_orb.id }

internal object AbigailaVarps : VarpReferences() {
    val poison = find("poison")
    val disease = find("disease")
}

internal object AbigailaObjs : ObjReferences() {
    val oculus_orb = find("oculus")
}

private typealias abigaila_varps = AbigailaVarps

private typealias abigaila_objs = AbigailaObjs
