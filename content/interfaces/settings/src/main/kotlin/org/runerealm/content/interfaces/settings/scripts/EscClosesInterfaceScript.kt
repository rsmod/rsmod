package org.runerealm.content.interfaces.settings.scripts

import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.boolVarp
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.type.builders.varp.VarpBuilder
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Turns on the "Esc closes the current interface" setting once per account, so the Esc key closes
 * any interface with a close button. Players can still turn it off in their settings.
 */
class EscClosesInterfaceScript : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerLogin { player.applyEscDefault() }
    }

    private fun Player.applyEscDefault() {
        if (escDefaultApplied) {
            return
        }
        escClosesInterface = true
        escDefaultApplied = true
    }
}

private var Player.escClosesInterface by boolVarBit(varbits.keybinding_esc_to_close)
private var Player.escDefaultApplied by boolVarp(EscCloseVarps.default_applied)

object EscCloseVarps : VarpReferences() {
    val default_applied = find("esc_closes_default_applied")
}

internal object EscCloseVarpBuilder : VarpBuilder() {
    init {
        build("esc_closes_default_applied")
    }
}
