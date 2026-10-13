package org.runerealm.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.runerealm.api.config.refs.mesanims
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.script.onOpNpc5
import org.runerealm.api.shops.Shops
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.content.areas.city.lumbridge.npcs.FarmingGardener.gardenerDialogue
import org.runerealm.content.areas.city.lumbridge.npcs.FarmingGardener.openFarmingSupplies
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/** Fayeth looks after the tree patch west of Lumbridge Castle. */
class Fayeth @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.fayeth) { talkTo(it.npc) }
        onOpNpc3(lumbridge_npcs.fayeth) { pay(it.npc) }
        onOpNpc5(lumbridge_npcs.fayeth) { player.openFarmingSupplies(shops, it.npc) }
    }

    private suspend fun ProtectedAccess.talkTo(npc: Npc) =
        startDialogue(npc) { gardenerDialogue(shops, npc, NOT_PLANTED) }

    // TODO(content): Farming - payment/protection once tree patches are implemented.
    private suspend fun ProtectedAccess.pay(npc: Npc) {
        chatNpc(npc, mesanims.neutral, NOT_PLANTED)
    }

    private companion object {
        const val NOT_PLANTED =
            "You don't have a sapling planted in that patch. Plant one and I might agree to " +
                "look after it for you."
    }
}
