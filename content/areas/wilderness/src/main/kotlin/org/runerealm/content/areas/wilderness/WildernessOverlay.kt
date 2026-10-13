package org.runerealm.content.areas.wilderness

import jakarta.inject.Inject
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.output.MiscOutput
import org.runerealm.api.player.output.runClientScript
import org.runerealm.api.player.ui.ifCloseOverlay
import org.runerealm.api.player.ui.ifOpenOverlay
import org.runerealm.api.player.vars.boolVarp
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerSoftTimer
import org.runerealm.api.type.builders.varp.VarpBuilder
import org.runerealm.api.type.refs.timer.TimerReferences
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Shows the Wilderness level overlay (`pvp_icons`) only while the player is in the Wilderness, and
 * tells the client whether it is inside (`combat_wilderness_transmit`). Players in the Wilderness
 * also get the "Attack" option on other players.
 */
class WildernessOverlay @Inject constructor(private val eventBus: EventBus) : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerLogin { player.softTimer(wilderness_timers.check, 1) }
        onPlayerSoftTimer(wilderness_timers.check) { player.updateWilderness() }
    }

    private fun Player.updateWilderness() {
        val inside = WildernessArea.contains(coords)
        if (inside == insideWilderness) {
            return
        }
        insideWilderness = inside
        if (inside) {
            ifOpenOverlay(interfaces.pvp_icons, components.toplevel_target_pvp_icons, eventBus)
        } else {
            ifCloseOverlay(interfaces.pvp_icons, eventBus)
        }
        runClientScript(COMBAT_WILDERNESS_TRANSMIT, if (inside) 1 else 0)
        MiscOutput.setPlayerOp(this, slot = 2, op = if (inside) "Attack" else null, priority = true)
    }

    private companion object {
        const val COMBAT_WILDERNESS_TRANSMIT = 390
    }
}

private var Player.insideWilderness by boolVarp(wilderness_varps.inside)

typealias wilderness_timers = WildernessTimers

typealias wilderness_varps = WildernessVarps

object WildernessTimers : TimerReferences() {
    val check = find("wilderness_check")
}

object WildernessVarps : VarpReferences() {
    /** Whether the overlay currently shows the player as inside the Wilderness. */
    val inside = find("wilderness_inside")
}

internal object WildernessVarpBuilder : VarpBuilder() {
    init {
        build("wilderness_inside") { temporary = true }
    }
}
