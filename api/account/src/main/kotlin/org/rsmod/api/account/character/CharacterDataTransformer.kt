package org.rsmod.api.account.character

import org.rsmod.game.entity.Player

public data class CharacterDataTransformer<T : CharacterDataStage.Segment>(
    val applier: CharacterDataStage.Applier<T>,
    val data: T,
) {
    public fun apply(player: Player) {
        applier.apply(player, data)
    }
}
