package org.runerealm.api.stats.plugin

import jakarta.inject.Inject
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.timers
import org.runerealm.api.player.hands
import org.runerealm.api.player.stat.baseHitpointsLvl
import org.runerealm.api.player.stat.hitpoints
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.stat.statAdd
import org.runerealm.api.player.stat.statBase
import org.runerealm.api.player.stat.statHeal
import org.runerealm.api.player.stat.statSub
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerSoftTimer
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.isType
import org.runerealm.game.type.stat.StatType
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class StatRegenScript @Inject constructor(private val statTypes: StatTypeList) :
    PluginScript() {
    private val regenStats by lazy { statTypes.values.toRegenStats() }

    override fun ScriptContext.startup() {
        onPlayerLogin { player.initRegenTimers() }

        onPlayerSoftTimer(timers.stat_regen) { player.statRegen() }
        onPlayerSoftTimer(timers.stat_boost_restore) { player.statBoostRestore() }
        onPlayerSoftTimer(timers.health_regen) { player.healthRegen() }

        onPlayerSoftTimer(timers.rapidrestore_regen) { player.statRegen() }
    }

    private fun Player.initRegenTimers() {
        softTimer(timers.stat_regen, constants.stat_regen_interval)
        softTimer(timers.stat_boost_restore, constants.stat_boost_restore_interval)
        softTimer(timers.health_regen, constants.health_regen_interval)
    }

    private fun Player.statRegen() {
        for (stat in regenStats) {
            val base = statBase(stat)
            val current = stat(stat)
            if (current < base) {
                statAdd(stat, constant = 1, percent = 0)
            }
        }
    }

    private fun Player.statBoostRestore() {
        for (stat in regenStats) {
            val base = statBase(stat)
            val current = stat(stat)
            if (current > base) {
                statSub(stat, constant = 1, percent = 0)
            }
        }
    }

    private fun Player.healthRegen() {
        if (hitpoints >= baseHitpointsLvl) {
            return
        }
        val amount = if (hands.isType(objs.regen_bracelet)) 2 else 1
        statHeal(stats.hitpoints, constant = amount, percent = 0)
    }

    private fun Collection<StatType>.toRegenStats(): List<StatType> {
        return filter { !it.isType(stats.prayer) && !it.isType(stats.hitpoints) }
    }
}
