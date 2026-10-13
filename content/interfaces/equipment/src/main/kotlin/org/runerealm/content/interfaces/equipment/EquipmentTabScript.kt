package org.runerealm.content.interfaces.equipment

import org.runerealm.api.player.output.mes
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.content.interfaces.equipment.configs.equip_components
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class EquipmentTabScript : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButton(equip_components.call_follower) { player.callFollower() }
    }

    private fun Player.callFollower() {
        // TODO(content): Apply logic once followers are available.
        mes("You do not have a follower.")
    }
}
