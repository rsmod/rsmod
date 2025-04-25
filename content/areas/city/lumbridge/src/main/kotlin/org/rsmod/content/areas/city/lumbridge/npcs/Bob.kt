package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.dialogue.Dialogues
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.advanced.onUnimplementedOpNpc4
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_invs
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Bob @Inject constructor(private val shops: Shops, private val dialogues: Dialogues) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.bob) { startDialogue(it.npc) }
        onOpNpc3(lumbridge_npcs.bob) { player.openShop(it.npc) }
        onUnimplementedOpNpc4(lumbridge_npcs.bob) { repairOp(it.npc) }
    }

    private fun Player.openShop(npc: Npc) {
        shops.open(this, npc, "Bob's Brilliant Axes", lumbridge_invs.axeshop)
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        dialogues.start(this, npc) { bobDialogue(npc) }
    }

    private suspend fun Dialogue.bobDialogue(npc: Npc) {
        val choice =
            choice3(
                "Give me a quest!",
                1,
                "Have you anything to sell?",
                2,
                "Can you repair my items for me?",
                3,
            )
        when (choice) {
            1 -> requestQuest()
            2 -> openShop(npc)
            3 -> repairItems()
        }
    }

    private suspend fun Dialogue.requestQuest() {
        chatPlayer(happy, "Give me a quest!")
        chatNpc(angry, "Get yer own!")
    }

    private suspend fun Dialogue.openShop(npc: Npc) {
        chatPlayer(quiz, "Have you anything to sell?")
        chatNpc(happy, "Yes! I buy and sell axes! Take your pick (or axe)!")
        player.openShop(npc)
    }

    private suspend fun Dialogue.repairItems() {
        chatPlayer(sad, "Can you repair my items for me?")
        chatNpc(
            shocked,
            "Of course I'll repair it, though the materials may cost " +
                "you. Just hand me the item and I'll have a look.",
        )
    }

    private suspend fun ProtectedAccess.repairOp(npc: Npc) {
        dialogues.start(this, npc) { chatNpc(confused, "You don't have anything I can repair.") }
    }
}
