package org.rsmod.plugins.store.player

import org.rsmod.game.model.mob.Player

public abstract class PlayerDataMapper<T : PlayerCodecData>(public val type: Class<T>) {

    public abstract fun serialize(player: Player): T

    public abstract fun deserialize(req: PlayerDataRequest, data: T): PlayerDataResponse

    public abstract fun createPlayer(req: PlayerDataRequest): Player
}
