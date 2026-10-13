package org.runerealm.api.specials.scripts

import kotlin.math.min
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.timers
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerSoftTimer
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class SpecialEnergyScript : PluginScript() {
    private val Player.newAccount by boolVarBit(varbits.new_player_account)
    private var Player.specialAttackEnergy by intVarp(varps.sa_energy)

    override fun ScriptContext.startup() {
        onPlayerLogin { player.initRegenTimer() }
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
