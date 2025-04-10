package org.rsmod.api.account.character.stats

import org.rsmod.api.account.character.CharacterDataStage

public data class CharacterStatData(val stats: List<Stat>) : CharacterDataStage.Segment {
    public data class Stat(val type: Int, val vis: Int, val base: Int, val fineXp: Int)
}
