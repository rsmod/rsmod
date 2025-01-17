package org.rsmod.game.client

import org.rsmod.game.entity.Player

public interface Client<S, T> {
    public fun open(service: S, player: Player)

    public fun close(service: S, player: Player)

    public fun write(message: T)

    public fun read(player: Player)

    public fun flush()

    public fun prePlayerCycle(player: Player)

    public fun postPlayerCycle(player: Player)
}
