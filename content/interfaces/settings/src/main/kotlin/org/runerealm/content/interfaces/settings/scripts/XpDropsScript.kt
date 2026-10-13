package org.runerealm.content.interfaces.settings.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.ui.ifCloseOverlay
import org.runerealm.api.player.ui.ifOpenMainModal
import org.runerealm.api.player.ui.ifOpenOverlay
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.onIfModalButton
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.varbit.VarBitType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The XP drops orb by the compass: "Show"/"Hide" toggles XP drops and "Setup" opens the XP drops
 * configuration interface, whose dropdown choices are saved to their varbits.
 */
class XpDropsScript @Inject constructor(private val eventBus: EventBus) : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButton(xpdrops_components.orb) {
            when (op) {
                IfButtonOp.Op1 -> player.toggleXpDrops()
                IfButtonOp.Op2 -> player.openSetup()
                else -> {}
            }
        }
        for ((component, varbit) in DROPDOWNS) {
            onIfModalButton(component) { selectOption(varbit, it.comsub) }
        }
        onIfModalButton(xpdrops_components.save) { ifClose() }
        onIfModalButton(xpdrops_components.cancel) { ifClose() }
    }

    /**
     * The client only uses `xpdrops_enabled` to draw the orb ("Show"/"Hide"). The drops are drawn
     * by the `xp_drops` overlay, which the server opens into (or closes from) the toplevel xp drops
     * target. The target is translated to the active gameframe, so this works in both fixed and
     * resizable modes.
     */
    private fun Player.toggleXpDrops() {
        val enable = !xpDropsEnabled
        xpDropsEnabled = enable
        if (enable) {
            ifOpenOverlay(interfaces.xp_drops, components.toplevel_target_xp_drops, eventBus)
        } else {
            ifCloseOverlay(interfaces.xp_drops, eventBus)
        }
    }

    private fun Player.openSetup() {
        ifOpenMainModal(xpdrops_interfaces.setup, eventBus)
        for ((component, _) in DROPDOWNS) {
            ifSetEvents(component, 0..MAX_OPTION, IfEvent.Op1)
        }
    }

    private fun ProtectedAccess.selectOption(varbit: VarBitType, comsub: Int) {
        // Dropdown entries are 1-based in the client.
        val value = comsub - 1
        val maxValue = (1 shl (varbit.msb - varbit.lsb + 1)) - 1
        if (value !in 0..maxValue) {
            return
        }
        vars[varbit] = value
    }

    private companion object {
        const val MAX_OPTION = 32

        val DROPDOWNS: List<Pair<ComponentType, VarBitType>> =
            listOf(
                xpdrops_components.position_options to xpdrops_varbits.position,
                xpdrops_components.size_options to xpdrops_varbits.size,
                xpdrops_components.duration_options to xpdrops_varbits.duration,
                xpdrops_components.counter_options to xpdrops_varbits.counter_type,
                xpdrops_components.progress_options to xpdrops_varbits.progress_type,
                xpdrops_components.colour_options to xpdrops_varbits.colour,
                xpdrops_components.grouping_options to xpdrops_varbits.groupskills,
                xpdrops_components.speed_options to xpdrops_varbits.speed,
                xpdrops_components.fakes_options to xpdrops_varbits.fakesoff,
            )
    }
}

private var Player.xpDropsEnabled by boolVarBit(xpdrops_varbits.enabled)

typealias xpdrops_interfaces = XpDropsInterfaces

typealias xpdrops_components = XpDropsComponents

typealias xpdrops_varbits = XpDropsVarBits

object XpDropsInterfaces : InterfaceReferences() {
    val setup = find("xpdrops_setup")
}

object XpDropsComponents : ComponentReferences() {
    val orb = find("orbs:xp_drops")
    val save = find("xpdrops_setup:save")
    val cancel = find("xpdrops_setup:cancel")
    val position_options = find("xpdrops_setup:position_options")
    val size_options = find("xpdrops_setup:size_options")
    val duration_options = find("xpdrops_setup:duration_options")
    val counter_options = find("xpdrops_setup:counter_options")
    val progress_options = find("xpdrops_setup:progress_options")
    val colour_options = find("xpdrops_setup:colour_options")
    val grouping_options = find("xpdrops_setup:grouping_options")
    val speed_options = find("xpdrops_setup:speed_options")
    val fakes_options = find("xpdrops_setup:fakes_options")
}

object XpDropsVarBits : VarBitReferences() {
    val enabled = find("xpdrops_enabled")
    val position = find("xpdrops_position")
    val size = find("xpdrops_size")
    val duration = find("xpdrops_duration")
    val counter_type = find("xpdrops_counter_type")
    val progress_type = find("xpdrops_progress_type")
    val colour = find("xpdrops_colour")
    val groupskills = find("xpdrops_groupskills")
    val speed = find("xpdrops_speed")
    val fakesoff = find("xpdrops_fakesoff")
}
