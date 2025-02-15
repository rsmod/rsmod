package org.rsmod.game.client

import org.rsmod.game.entity.Player

public interface Client<S, T> {
    public fun open(service: S, player: Player)

    public fun close(service: S, player: Player)

    public fun write(message: T)

    public fun read(player: Player)

    public fun flush()
}

public interface ClientCycle {
    public fun preCycle(player: Player)

    public fun postCycle(player: Player)
}
