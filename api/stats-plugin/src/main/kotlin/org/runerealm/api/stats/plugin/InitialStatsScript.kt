package org.runerealm.api.stats.plugin

import jakarta.inject.Inject
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.stat.PlayerSkillXP
import org.runerealm.api.player.stat.baseHitpointsLvl
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.onPlayerInit
import org.runerealm.game.entity.Player
import org.runerealm.game.stat.PlayerSkillXPTable
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class InitialStatsScript @Inject constructor(private val statTypes: StatTypeList) :
    PluginScript() {
    private val hitpointsStartLvl by lazy { statTypes[stats.hitpoints].minLevel }
    private val hitpointsStartFineXp by lazy { getFineXp(hitpointsStartLvl) }

    private val Player.newAccount by boolVarBit(varbits.new_player_account)

    override fun ScriptContext.startup() {
        onPlayerInit { player.setInitialStats() }
    }

    private fun Player.setInitialStats() {
        if (!newAccount) {
            return
        }
        if (baseHitpointsLvl < hitpointsStartLvl) {
            statMap.setFineXP(stats.hitpoints, hitpointsStartFineXp)
            statMap.setCurrentLevel(stats.hitpoints, hitpointsStartLvl.toByte())
            statMap.setBaseLevel(stats.hitpoints, hitpointsStartLvl.toByte())
            appearance.combatLevel = PlayerSkillXP.calculateCombatLevel(this)
        }
    }

    private fun getFineXp(level: Int): Int = PlayerSkillXPTable.getFineXPFromLevel(level)
}
