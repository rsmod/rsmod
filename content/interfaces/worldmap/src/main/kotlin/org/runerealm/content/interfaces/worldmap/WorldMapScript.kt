package org.runerealm.content.interfaces.worldmap

import jakarta.inject.Inject
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.ui.ifCloseOverlay
import org.runerealm.api.player.ui.ifOpenOverlay
import org.runerealm.api.player.ui.ifOpenTop
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.interf.InterfaceTypeList
import org.runerealm.game.ui.UserInterface
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The world map, opened from the minimap's world map orb.
 *
 * The orb's ops are built by the `orbs_worldmap_setup` clientscript:
 * - Op2 "Floating World Map": opens the `worldmap` overlay into the toplevel `floater` target (the
 *   same target used by other floating panels such as All Settings). While the floater holds any
 *   interface the client relabels op2 as "Close Floating panel".
 * - Op3 "Fullscreen World Map": switches the toplevel to `toplevel_display` and opens the map in
 *   its `displaymodal` target. Closing the map restores the player's gameframe toplevel; the client
 *   keeps the gameframe's sub-interfaces attached in the meantime.
 *
 * Panning, zooming (scroll wheel and +/- buttons) and the key/legend are handled entirely by the
 * client. In floating mode, `worldmap_init` creates 8 resize/move handles as dynamic children of
 * `worldmap:content`, which are made draggable. The server receives the close button, escape key,
 * the key panel toggle and the five map toggles (`worldmap:toggles` 0..4), and saves their varbits.
 */
class WorldMapScript
@Inject
constructor(private val eventBus: EventBus, private val interfaceTypes: InterfaceTypeList) :
    PluginScript() {
    private var Player.disableKey by boolVarBit(worldmap_varbits.disable_key)
    private var Player.mapToggles by intVarBit(worldmap_varbits.toggles)
    private val Player.gameframeTopLevel by intVarBit(varbits.gameframe_toplevel)

    override fun ScriptContext.startup() {
        onIfOverlayButton(worldmap_components.orb) {
            when (op) {
                IfButtonOp.Op2 -> player.toggleFloatingMap()
                IfButtonOp.Op3 -> player.openFullscreenMap()
                else -> {}
            }
        }
        onIfOverlayButton(worldmap_components.close) { player.closeWorldMap() }
        onIfOverlayButton(worldmap_components.esckey) { player.closeWorldMap() }
        onIfOverlayButton(worldmap_components.key_toggle) { player.disableKey = !player.disableKey }
        onIfOverlayButton(worldmap_components.toggles) { player.toggleMapOption(comsub) }
    }

    private fun Player.toggleFloatingMap() {
        if (isFullscreenMap()) {
            closeWorldMap()
            return
        }
        // The client shows "Close Floating panel" instead of "Floating World Map" whenever the
        // floater has an interface open, whichever one it is.
        val floating = ui.getOverlayOrNull(components.toplevel_target_floater)
        if (floating != null) {
            ifCloseOverlay(interfaceTypes.getValue(floating.packed), eventBus)
            return
        }
        ifOpenOverlay(worldmap_interfaces.worldmap, components.toplevel_target_floater, eventBus)
        ifSetEvents(worldmap_components.content, 0..<RESIZE_HANDLES, IfEvent.Depth1)
        setToggleEvents()
    }

    private fun Player.openFullscreenMap() {
        if (isFullscreenMap()) {
            return
        }
        ifCloseOverlay(worldmap_interfaces.worldmap, eventBus)
        ifOpenTop(worldmap_interfaces.toplevel_display)
        ifOpenOverlay(worldmap_interfaces.worldmap, worldmap_components.displaymodal, eventBus)
        setToggleEvents()
    }

    private fun Player.closeWorldMap() {
        val fullscreen = isFullscreenMap()
        ifCloseOverlay(worldmap_interfaces.worldmap, eventBus)
        if (fullscreen) {
            restoreGameframe()
        }
    }

    private fun Player.restoreGameframe() {
        val topLevel = interfaceTypes[gameframeTopLevel] ?: return
        ifOpenTop(topLevel)
    }

    private fun Player.isFullscreenMap(): Boolean =
        ui.topLevel == UserInterface(worldmap_interfaces.toplevel_display.id)

    private fun Player.setToggleEvents() {
        ifSetEvents(worldmap_components.toggles, 0..<MAP_TOGGLES, IfEvent.Op1)
    }

    private fun Player.toggleMapOption(comsub: Int) {
        if (comsub !in 0..<MAP_TOGGLES) {
            return
        }
        mapToggles = mapToggles xor (1 shl comsub)
    }

    private companion object {
        const val RESIZE_HANDLES = 8
        const val MAP_TOGGLES = 5
    }
}

typealias worldmap_interfaces = WorldMapInterfaces

typealias worldmap_components = WorldMapComponents

typealias worldmap_varbits = WorldMapVarBits

object WorldMapInterfaces : InterfaceReferences() {
    val worldmap = find("worldmap")
    val toplevel_display = find("toplevel_display")
}

object WorldMapComponents : ComponentReferences() {
    val orb = find("orbs:worldmap")
    val displaymodal = find("toplevel_display:displaymodal")
    val close = find("worldmap:close")
    val esckey = find("worldmap:esckey")
    val content = find("worldmap:content")
    val key_toggle = find("worldmap:key_toggle")
    val toggles = find("worldmap:toggles")
}

object WorldMapVarBits : VarBitReferences() {
    val toggles = find("worldmap_toggles")
    val disable_key = find("worldmap_disable_key")
}
