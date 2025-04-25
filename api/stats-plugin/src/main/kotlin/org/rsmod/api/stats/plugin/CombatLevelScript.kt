package org.rsmod.api.stats.plugin

import org.rsmod.api.player.stat.PlayerSkillXP
import org.rsmod.api.script.onPlayerInit
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class CombatLevelScript : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerInit { player.setCombatLevel() }
    }

    private fun Player.setCombatLevel() {
        val combatLevel = PlayerSkillXP.calculateCombatLevel(this)
        appearance.combatLevel = combatLevel
    }
}
