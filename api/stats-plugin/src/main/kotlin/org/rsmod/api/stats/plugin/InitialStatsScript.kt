package org.rsmod.api.stats.plugin

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.PlayerSkillXP
import org.rsmod.api.player.stat.baseHitpointsLvl
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.script.onPlayerInit
import org.rsmod.game.entity.Player
import org.rsmod.game.stat.PlayerSkillXPTable
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
