package org.runerealm.content.interfaces.prayer.tab.scripts

import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.queues
import org.runerealm.api.config.refs.timers
import org.runerealm.api.script.onPlayerQueue
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class PreservePrayerScript : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerQueue(queues.preserve_activation) { player.activatePreserveEffect() }
    }

    private fun Player.activatePreserveEffect() {
        val boostedInterval = constants.stat_boost_restore_interval * 1.5
        softTimer(timers.stat_boost_restore, boostedInterval.toInt())
    }
}
