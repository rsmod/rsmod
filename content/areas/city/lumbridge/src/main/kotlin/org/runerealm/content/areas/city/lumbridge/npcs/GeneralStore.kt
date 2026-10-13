package org.runerealm.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.runerealm.api.config.refs.BaseInvs
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.shops.Shops
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
