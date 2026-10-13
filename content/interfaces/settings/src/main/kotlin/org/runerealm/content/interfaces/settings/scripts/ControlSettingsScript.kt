package org.runerealm.content.interfaces.settings.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.enumVarp
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.utils.vars.VarEnumDelegate
import org.runerealm.content.interfaces.settings.configs.setting_components
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class ControlSettingsScript
@Inject
constructor(private val protectedAccess: ProtectedAccessLauncher) : PluginScript() {
    private var Player.acceptAid by boolVarBit(varbits.option_acceptaid)
    private var Player.skullPrevention by boolVarBit(varbits.skull_prevent)
    private var Player.priorityPlayer by enumVarp<PlayerPriority>(varps.option_attackpriority)
    private var Player.priorityNpc by enumVarp<NpcPriority>(varps.option_attackpriority_npc)

    override fun ScriptContext.startup() {
        onIfOverlayButton(setting_components.skull_prevention) { player.toggleSkullPrevention() }

        onIfOverlayButton(setting_components.attack_priority_player_buttons) {
            player.selectPlayerPriority(comsub)
        }

        onIfOverlayButton(setting_components.attack_priority_npc_buttons) {
            player.selectNpcPriority(comsub)
        }

        onIfOverlayButton(setting_components.acceptaid) { player.toggleAcceptAid() }
        onIfOverlayButton(setting_components.houseoptions) { player.selectHouseOptions() }
        onIfOverlayButton(setting_components.bondoptions) { player.selectBondPouch() }
    }

    private fun Player.toggleSkullPrevention() {
        skullPrevention = !skullPrevention
    }

    private fun Player.selectPlayerPriority(comsub: Int) {
        val priority =
            when (comsub) {
                1 -> PlayerPriority.CombatLevel
                2 -> PlayerPriority.RightClickAlways
                3 -> PlayerPriority.LeftClick
                4 -> PlayerPriority.Hidden
                5 -> PlayerPriority.RightClickClan
                else -> error("Invalid comsub: $comsub")
            }
        priorityPlayer = priority
    }

    private fun Player.selectNpcPriority(comsub: Int) {
        val priority =
            when (comsub) {
                1 -> NpcPriority.CombatLevel
                2 -> NpcPriority.RightClickAlways
                3 -> NpcPriority.LeftClick
                4 -> NpcPriority.Hidden
                else -> error("Invalid comsub: $comsub")
            }
        priorityNpc = priority
    }

    private fun Player.toggleAcceptAid() {
        acceptAid = !acceptAid
    }

    private fun Player.selectHouseOptions() {
        protectedAccess.launch(this) { ifOpenSide(interfaces.poh_options) }
    }

    private fun Player.selectBondPouch() {
        val opened = protectedAccess.launch(this) { ifOpenMainModal(interfaces.bond_main, -1, -2) }
        if (!opened) {
            mes(constants.dm_busy)
        }
    }
}

private enum class PlayerPriority(override val varValue: Int) : VarEnumDelegate {
    CombatLevel(0),
    RightClickAlways(1),
    LeftClick(2),
    Hidden(3),
    RightClickClan(4),
}

private enum class NpcPriority(override val varValue: Int) : VarEnumDelegate {
    CombatLevel(0),
    RightClickAlways(1),
    LeftClick(2),
    Hidden(3),
}
