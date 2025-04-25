package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.BaseInvs
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class GeneralStore @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.shop_keeper) { shopDialogue(it.npc) }
        onOpNpc3(lumbridge_npcs.shop_keeper) { player.openGeneralStore(it.npc) }
        onOpNpc1(lumbridge_npcs.shop_assistant) { shopDialogue(it.npc) }
        onOpNpc3(lumbridge_npcs.shop_assistant) { player.openGeneralStore(it.npc) }
    }

    private fun Player.openGeneralStore(npc: Npc) {
        shops.open(this, npc, "Lumbridge General Store", BaseInvs.generalshop1)
    }

    private suspend fun ProtectedAccess.shopDialogue(npc: Npc) =
        startDialogue(npc) { shopKeeper(npc) }

    private suspend fun Dialogue.shopKeeper(npc: Npc) {
        chatNpc(happy, "Can I help you at all?")
        val choice = choice2("Yes please. What are you selling?", 1, "No thanks.", 2)
        if (choice == 1) {
            player.openGeneralStore(npc)
        } else if (choice == 2) {
            chatPlayer(neutral, "No thanks.")
        }
    }
}
