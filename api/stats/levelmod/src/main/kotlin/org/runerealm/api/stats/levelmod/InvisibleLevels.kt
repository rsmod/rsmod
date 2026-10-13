package org.runerealm.api.stats.levelmod

import jakarta.inject.Inject
import org.runerealm.game.entity.Player
import org.runerealm.game.type.stat.StatType

class InvisibleLevels @Inject constructor(mods: Set<InvisibleLevelMod>) {
    private val statMods = mods.groupByTo(HashMap()) { it.stat.id }

    fun get(player: Player, stat: StatType): Int {
        val mods = statMods[stat.id] ?: return 0
        return mods.sumOf { mod -> mod[player] }
    }

    private operator fun InvisibleLevelMod.get(player: Player): Int = player.calculateBoost()
}
