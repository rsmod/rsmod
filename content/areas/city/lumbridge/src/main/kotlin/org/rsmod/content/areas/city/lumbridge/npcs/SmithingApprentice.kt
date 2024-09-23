package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.dialogue.Dialogue
import org.rsmod.api.dialogue.Dialogues
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseSmithingLvl
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeNpcs
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeObjs.bank_icon
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeObjs.furnace_icon
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeObjs.mining_icon
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeObjs.smithing_icon
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SmithingApprentice @Inject constructor(private val dialogues: Dialogues) : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpNpc1(LumbridgeNpcs.smithing_apprentice) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        dialogues.start(this, npc) { smithingApprenticeDialogue() }
    }

    private suspend fun Dialogue.smithingApprenticeDialogue() {
        when {
            player.baseSmithingLvl >= 29 -> highLevelDialogue()
            // NOTE: Might be lower than 14, did not check from level 10-13.
            player.baseSmithingLvl >= 14 -> intermediateLevelDialogue()
            else -> lowLevelDialogue()
        }
    }

    private suspend fun Dialogue.lowLevelDialogue() {
        chatPlayer(quiz, "Can you teach me the basics of smelting please?")
        objbox(
            furnace_icon,
            "Look for this icon on your minimap to find a furnace to smelt ores into metal.",
        )
        if (content.ore in player.inv) {
            invOre()
        } else {
            noInvOre()
        }
    }

    private suspend fun Dialogue.noInvOre() {
        chatNpc(
            sad,
            "You'll need to have mined some ore to smelt first. Go " +
                "see the mining tutor to the south if you're not sure " +
                "how to do this.",
        )
        objbox(mining_icon, "Look for this icon to the south of here, in the swamp.")
    }

    private suspend fun Dialogue.invOre() {
        chatNpc(happy, "I see you have some ore with you to smelt, so let's get started.")
        chatNpc(
            neutral,
            "Click on the furnace to bring up a menu of metal bars " +
                "you can try to make from your ore.",
        )
        chatNpc(
            happy,
            "When you have a full inventory, take it to the bank, " +
                "you can find it on the roof of the castle in Lumbridge.",
        )
        objbox(
            bank_icon,
            "To find a bank, look for this symbol on your minimap " +
                "after climbing the stairs of the Lumbridge Castle to the " +
                "top. There are banks all over the world with this symbol.",
        )
        chatNpc(
            happy,
            "If you have a hammer with you, you can smith the " +
                "bronze bars into equipment on the anvil outside.",
        )
        chatNpc(
            sad,
            "I'm afraid the weather over the years has rusted it " +
                "down, so it can only be used to work bronze.",
        )
        chatNpc(
            happy,
            "Alternatively you can run up to Varrock. Look for my " +
                "Master, the Smithing Tutor, in the west of the city, he " +
                "can help you smith better gear.",
        )
    }

    private suspend fun Dialogue.intermediateLevelDialogue() {
        chatPlayer(quiz, "I already know about the basics of smelting, got any tips")
        chatNpc(
            happy,
            "I find it useful to stockpile my ore before smelting it in " +
                "one go, or even do it on the way to the bank in such " +
                "places as Al Kharid or Falador. But you can do it " +
                "anyway you want, one load at a time is fine too!",
        )
        chatNpc(neutral, "Not too much can go wrong with smelting.")
        chatNpc(
            neutral,
            "You will lose some iron when smelting that ore, but a " +
                "Ring of Forging or the Superheat Item spell will soon " +
                "solve that",
        )
    }

    private suspend fun Dialogue.highLevelDialogue() {
        chatPlayer(quiz, "What do I do after smelting the ore?")
        chatNpc(
            laugh,
            "Ahh you'll need to learn to smith. You can use bronze " +
                "bars on the anvil outside with a hammer.",
        )
        chatNpc(
            happy,
            "But for the best advice, go to Varrock. That's where " +
                "my master, the Smithing Tutor, plies his trade. Ask him " +
                "to teach you how to smith.",
        )
        objbox(smithing_icon, "Look for this icon in the west of Varrock.")
        chatNpc(
            happy,
            "Smelt some ore and store it in the bank. Grab a " +
                "hammer from the general store before you go too!",
        )
    }
}
