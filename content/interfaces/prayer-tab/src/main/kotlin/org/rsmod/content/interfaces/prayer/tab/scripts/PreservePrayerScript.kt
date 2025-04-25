package org.rsmod.content.interfaces.prayer.tab.scripts

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.timers
import org.rsmod.api.script.onPlayerQueue
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class PreservePrayerScript : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerQueue(queues.preserve_activation) { player.activatePreserveEffect() }
    }

    private fun Player.activatePreserveEffect() {
        val boostedInterval = constants.stat_boost_restore_interval * 1.5
        softTimer(timers.stat_boost_restore, boostedInterval.toInt())
    }
}
