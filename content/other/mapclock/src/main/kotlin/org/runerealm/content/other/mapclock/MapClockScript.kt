package org.runerealm.content.other.mapclock

import jakarta.inject.Inject
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerSoftTimer
import org.runerealm.game.MapClock
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class MapClockScript @Inject constructor(private val clock: MapClock) : PluginScript() {
    private var Player.playtime by intVarp(varps.playtime)
    private var Player.mapClock by intVarp(varps.map_clock)
    private var Player.msPastMinute by intVarBit(varbits.date_ms_past_minute)
    private var Player.secsPastMinute by intVarBit(varbits.date_secs_past_minute)

    override fun ScriptContext.startup() {
        onPlayerLogin { player.initClockTimer() }
        onPlayerSoftTimer(clock_timers.map_clock) { player.incrementClock() }
    }

    private fun Player.initClockTimer() {
        softTimer(clock_timers.map_clock, 1)
    }

    private fun Player.incrementClock() {
        val now = System.currentTimeMillis()
        msPastMinute = (now % 1000).toInt()
        secsPastMinute = ((now / 1000) % 60).toInt()
        mapClock = clock.cycle
        playtime++
    }
}
