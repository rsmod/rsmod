package org.rsmod.content.interfaces.equipment

import org.rsmod.api.player.output.mes
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.content.interfaces.equipment.configs.equip_components
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EquipmentTabScript : PluginScript() {
    override fun ScriptContext.startUp() {
        onIfOverlayButton(equip_components.call_follower) { player.callFollower() }
    }

    private fun Player.callFollower() {
        // TODO(content): Apply logic once followers are available.
        mes("You do not have a follower.")
    }
}
