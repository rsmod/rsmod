package org.rsmod.content.interfaces.settings.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.ui.ifMoveTop
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.resyncVar
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.content.interfaces.settings.configs.setting_components
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
