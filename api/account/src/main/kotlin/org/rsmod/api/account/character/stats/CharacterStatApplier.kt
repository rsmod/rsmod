package org.rsmod.api.account.character.stats

import jakarta.inject.Inject
import org.rsmod.api.account.character.CharacterDataStage
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatTypeList

public class CharacterStatApplier @Inject constructor(private val statTypes: StatTypeList) :
    CharacterDataStage.Applier<CharacterStatData> {
    override fun apply(player: Player, data: CharacterStatData) {
        for (loaded in data.stats) {
            val (type, vis, base, fineXp) = loaded
            val stat = statTypes.getValue(type)
            player.statMap.setCurrentLevel(stat, vis.toByte())
            player.statMap.setBaseLevel(stat, base.toByte())
            player.statMap.setFineXP(stat, fineXp)
        }
    }
}
