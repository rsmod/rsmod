package org.runerealm.content.other.social

import jakarta.inject.Inject
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.ui.ifOpenOverlay
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.script.onIfOpen
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.content.other.social.clan.ClanRegistry
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.InterfaceType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The chat-channels side tab: its four tabs (Chat-channel, Your Clan, View another clan and
 * Grouping) each show their own panel inside the tab's contents layer. While a player is forming a
 * clan, "Your Clan" shows the clan creation panel instead.
 */
class ChannelsTabScript
@Inject
constructor(private val eventBus: EventBus, private val clans: ClanRegistry) : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOpen(interfaces.side_channels) { player.showChannelsTab(eventBus, clans) }
        onIfOverlayButton(channels_components.tab_0) { player.selectTab(0) }
        onIfOverlayButton(channels_components.tab_1) { player.selectTab(1) }
        onIfOverlayButton(channels_components.tab_2) { player.selectTab(2) }
        onIfOverlayButton(channels_components.tab_3) { player.selectTab(3) }
    }

    private fun Player.selectTab(tab: Int) {
        selectedChannelsTab = tab
        showChannelsTab(eventBus, clans)
    }
}

/** Shows the panel of the chat-channels tab the player has selected. */
internal fun Player.showChannelsTab(eventBus: EventBus, clans: ClanRegistry) {
    val panel = channelsPanelFor(selectedChannelsTab, clans) ?: return
    ifOpenOverlay(panel, channels_components.contents, eventBus)
}

/** Switches the chat-channels tab to "Your Clan" (used when a clan is being formed or joined). */
internal fun Player.showYourClanTab(eventBus: EventBus, clans: ClanRegistry) {
    selectedChannelsTab = 1
    if (ui.containsOverlay(channels_interfaces.side_channels)) {
        showChannelsTab(eventBus, clans)
    }
}

private fun Player.channelsPanelFor(tab: Int, clans: ClanRegistry): InterfaceType? =
    when (tab) {
        0 -> channels_interfaces.chatchannel_current
        1 ->
            if (clans.formationOf(this) != null) {
                channels_interfaces.clans_creation_sidepanel
            } else {
                channels_interfaces.clans_sidepanel
            }
        2 -> channels_interfaces.clans_guest_sidepanel
        3 -> channels_interfaces.grouping
        else -> null
    }

private var Player.selectedChannelsTab by intVarBit(channels_varbits.tab_selected)

typealias channels_interfaces = ChannelsInterfaces

typealias channels_components = ChannelsComponents

typealias channels_varbits = ChannelsVarBits

object ChannelsInterfaces : InterfaceReferences() {
    val side_channels = find("side_channels")
    val chatchannel_current = find("chatchannel_current")
    val clans_sidepanel = find("clans_sidepanel")
    val clans_guest_sidepanel = find("clans_guest_sidepanel")
    val clans_creation_sidepanel = find("clans_creation_sidepanel")
    val grouping = find("grouping")
}

object ChannelsComponents : ComponentReferences() {
    val contents = find("side_channels:contents")
    val tab_0 = find("side_channels:tab_0")
    val tab_1 = find("side_channels:tab_1")
    val tab_2 = find("side_channels:tab_2")
    val tab_3 = find("side_channels:tab_3")
}

object ChannelsVarBits : VarBitReferences() {
    val tab_selected = find("side_channels_tab_selected")
}
