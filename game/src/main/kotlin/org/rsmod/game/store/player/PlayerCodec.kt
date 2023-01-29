package org.rsmod.game.store.player

import org.rsmod.game.model.mob.Player

public interface PlayerCodec {

    public fun serialize(player: Player)

    public fun deserialize(req: PlayerDataRequest): PlayerDataResponse
}
