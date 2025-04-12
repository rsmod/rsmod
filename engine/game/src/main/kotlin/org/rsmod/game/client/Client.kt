package org.rsmod.game.client

import org.rsmod.game.entity.Player

public interface Client<S, T> {
    public fun close()

    public fun write(message: T)

    public fun read(player: Player)

    public fun flush()

    public fun unregister(service: S, player: Player)
}

public interface ClientCycle {
    public fun preCycle(player: Player)

    public fun postCycle(player: Player)
}
