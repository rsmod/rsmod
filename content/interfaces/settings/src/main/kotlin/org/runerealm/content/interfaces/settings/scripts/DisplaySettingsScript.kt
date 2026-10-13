package org.runerealm.content.interfaces.settings.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.ui.ifMoveTop
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.resyncVar
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.content.interfaces.settings.configs.setting_components
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class DisplaySettingsScript @Inject constructor(private val eventBus: EventBus) : PluginScript() {
    private var Player.zoomDisabled by boolVarBit(varbits.camera_zoom_mouse_disabled)

    override fun ScriptContext.startup() {
        onIfOverlayButton(setting_components.brightness_bobble_container) {
            player.resyncVar(varbits.option_brightness_remember)
        }
        onIfOverlayButton(setting_components.zoom_toggle) {
            player.zoomDisabled = !player.zoomDisabled
        }
        onIfOverlayButton(setting_components.client_type_buttons) {
            player.toggleClientType(comsub)
        }
    }

    private fun Player.toggleClientType(comsub: Int) {
        when (comsub) {
            1 -> ifMoveTop(interfaces.toplevel, eventBus)
            2 -> ifMoveTop(interfaces.toplevel_osrs_stretch, eventBus)
            3 -> ifMoveTop(interfaces.toplevel_pre_eoc, eventBus)
            else -> error("Invalid comsub: $comsub")
        }
    }
}
