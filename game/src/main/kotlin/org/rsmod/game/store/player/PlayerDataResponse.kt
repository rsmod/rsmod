package org.rsmod.game.store.player

import org.rsmod.game.model.mob.Player

public sealed class PlayerDataResponse {

    public sealed class Success(public val player: Player) : PlayerDataResponse() {

        public class NewPlayer(player: Player) : Success(player)
        public class ExistingPlayer(player: Player) : Success(player)
    }

    public data class Exception(val t: Throwable) : PlayerDataResponse()

    public object InvalidCredentials : PlayerDataResponse()
}
