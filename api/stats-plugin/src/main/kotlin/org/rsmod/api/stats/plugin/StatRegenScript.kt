package org.rsmod.api.stats.plugin

import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.timers
import org.rsmod.api.player.hands
import org.rsmod.api.player.stat.baseHitpointsLvl
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.stat.statAdd
import org.rsmod.api.player.stat.statBase
import org.rsmod.api.player.stat.statHeal
import org.rsmod.api.player.stat.statSub
import org.rsmod.api.script.onPlayerLogin
import org.rsmod.api.script.onPlayerSoftTimer
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
