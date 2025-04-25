package org.rsmod.api.specials.scripts

import kotlin.math.min
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.timers
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onPlayerLogIn
import org.rsmod.api.script.onPlayerSoftTimer
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class SpecialEnergyScript : PluginScript() {
    private val Player.newAccount by boolVarBit(varbits.new_player_account)
    private var Player.specialAttackEnergy by intVarp(varps.sa_energy)

    override fun ScriptContext.startUp() {
        onPlayerLogIn { player.initRegenTimer() }
        onPlayerSoftTimer(timers.spec_regen) { player.specRegen() }
    }

    private fun Player.initRegenTimer() {
        if (newAccount) {
            specialAttackEnergy = constants.sa_max_energy
        }
        softTimer(timers.spec_regen, constants.spec_regen_interval)
    }

    private fun Player.specRegen() {
        val increased = min(constants.sa_max_energy, specialAttackEnergy + 100)
        if (increased > specialAttackEnergy) {
            specialAttackEnergy = increased
        }
    }
}
