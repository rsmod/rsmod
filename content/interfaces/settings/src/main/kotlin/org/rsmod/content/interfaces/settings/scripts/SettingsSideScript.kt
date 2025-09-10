package org.rsmod.content.interfaces.settings.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.vars.enumVarBit
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.utils.vars.VarEnumDelegate
import org.rsmod.content.interfaces.settings.configs.setting_components
import org.rsmod.content.interfaces.settings.configs.setting_varbits
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SettingsSideScript @Inject constructor(private val protectedAccess: ProtectedAccessLauncher) :
    PluginScript() {
    private var Player.panel by enumVarBit<Panel>(setting_varbits.panel_tab)

    override fun ScriptContext.startup() {
        onIfOpen(interfaces.settings_side) { player.updateIfEvents() }

        onIfOverlayButton(setting_components.settings_tab) { player.panel = Panel.Control }
        onIfOverlayButton(setting_components.audio_tab) { player.panel = Panel.Audio }
        onIfOverlayButton(setting_components.display_tab) { player.panel = Panel.Display }

        onIfOverlayButton(setting_components.settings_open) { player.selectAllSettings() }
    }

    private fun Player.updateIfEvents() {
        ifSetEvents(setting_components.music_bobble_container, 0..21, IfEvent.Op1)
        ifSetEvents(setting_components.sound_bobble_container, 0..21, IfEvent.Op1)
        ifSetEvents(setting_components.areasounds_bobble_container, 0..21, IfEvent.Op1)
        ifSetEvents(setting_components.master_bobble_container, 0..21, IfEvent.Op1)
        ifSetEvents(setting_components.attack_priority_player_buttons, 1..5, IfEvent.Op1)
        ifSetEvents(setting_components.attack_priority_npc_buttons, 1..4, IfEvent.Op1)
        ifSetEvents(setting_components.client_type_buttons, 1..3, IfEvent.Op1)
        ifSetEvents(setting_components.brightness_bobble_container, 0..21, IfEvent.Op1)
    }

    private fun Player.selectAllSettings() {
        val opened = protectedAccess.launch(this) { openAllSettings() }
        if (!opened) {
            mes("Please finish what you are doing before opening the settings menu.")
        }
    }

    private fun ProtectedAccess.openAllSettings() {
        // TODO(content): varp `settings_tracking` is spam synced here for some reason.
        ifOpenOverlay(interfaces.settings)
    }
}

private enum class Panel(override val varValue: Int) : VarEnumDelegate {
    Control(0),
    Audio(1),
    Display(2),
}
