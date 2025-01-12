package org.rsmod.api.stats.xpmod

import jakarta.inject.Inject
import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.stat.StatType

class WornXpModifiers @Inject constructor(private val objTypes: ObjTypeList) : XpMod {
    override fun Player.modifier(stat: StatType): Double {
        val percent = worn.sumOf { it?.modPercent(stat) ?: 0 }
        return percent / 100.0
    }

    private fun InvObj.modPercent(stat: StatType): Int {
        val objType = objTypes[this]
        if (objType.paramOrNull(params.xpmod_stat) != stat) {
            return 0
        }
        return objType.param(params.xpmod_percent)
    }
}
