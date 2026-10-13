package org.runerealm.api.stats.plugin

import org.runerealm.api.player.stat.PlayerSkillXP
import org.runerealm.api.script.onPlayerInit
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class CombatLevelScript : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerInit { player.setCombatLevel() }
    }

    private fun Player.setCombatLevel() {
        val combatLevel = PlayerSkillXP.calculateCombatLevel(this)
        appearance.combatLevel = combatLevel
    }
}
