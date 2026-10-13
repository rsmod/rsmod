package org.runerealm.content.interfaces.settings.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.ui.ifCloseOverlay
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.vars.enumVarBit
import org.runerealm.api.script.onIfOpen
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.utils.vars.VarEnumDelegate
import org.runerealm.content.interfaces.settings.configs.setting_components
import org.runerealm.content.interfaces.settings.configs.setting_varbits
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class SettingsSideScript
@Inject
constructor(private val protectedAccess: ProtectedAccessLauncher, private val eventBus: EventBus) :
    PluginScript() {
    private var Player.panel by enumVarBit<Panel>(setting_varbits.panel_tab)

    override fun ScriptContext.startup() {
        onIfOpen(interfaces.settings_side) { player.updateIfEvents() }

        onIfOverlayButton(setting_components.settings_tab) { player.panel = Panel.Control }
        onIfOverlayButton(setting_components.audio_tab) { player.panel = Panel.Audio }
        onIfOverlayButton(setting_components.display_tab) { player.panel = Panel.Display }

        onIfOverlayButton(setting_components.settings_open) { player.selectAllSettings() }
        // Op1 is the "Close" button; op2 is bound client-side to the escape key.
        onIfOverlayButton(setting_components.settings_close) {
            player.ifCloseOverlay(interfaces.settings, eventBus)
        }
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
