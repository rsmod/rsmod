package org.rsmod.game.client

import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList

public interface Client<S, T> {
    public fun open(service: S, player: Player)

    public fun close(service: S, player: Player)

    public fun write(message: T)

    public fun read(player: Player)

    public fun flush()

    public fun prePlayerCycle(player: Player, objTypes: ObjTypeList)

    public fun postPlayerCycle(player: Player)
}
