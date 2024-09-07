package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.BaseInvs
import org.rsmod.api.dialogue.Dialogue
import org.rsmod.api.dialogue.Dialogues
import org.rsmod.api.dialogue.startDialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.api.shops.openShop
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class GeneralStore @Inject constructor(private val dialogues: Dialogues, private val shops: Shops) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onOpNpc1(LumbridgeNpcs.shop_keeper) { shopDialogue(it.npc) }
        onOpNpc3(LumbridgeNpcs.shop_keeper) { player.openGeneralStore() }
        onOpNpc1(LumbridgeNpcs.shop_assistant) { shopDialogue(it.npc) }
        onOpNpc3(LumbridgeNpcs.shop_assistant) { player.openGeneralStore() }
    }

    private fun Player.openGeneralStore() {
        openShop(shops, "Lumbridge General Store", BaseInvs.generalshop1)
    }

    private suspend fun ProtectedAccess.shopDialogue(npc: Npc) =
        startDialogue(dialogues, npc) { shopKeeper() }

    private suspend fun Dialogue.shopKeeper() {
        chatNpc(happy, "Can I help you at all?")
        val choice = choice2("Yes please. What are you selling?", 1, "No thanks.", 2)
        if (choice == 1) {
            player.openGeneralStore()
        } else if (choice == 2) {
            chatPlayer(neutral, "No thanks.")
        }
    }
}
