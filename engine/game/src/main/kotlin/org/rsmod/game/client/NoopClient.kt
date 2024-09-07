package org.rsmod.game.client

import org.rsmod.game.entity.Player

public object NoopClient : Client<Any, Any> {
    override fun open(service: Any, player: Player) {}

    override fun close(service: Any, player: Player) {}

    override fun write(message: Any) {}

    override fun read(player: Player) {}

    override fun flush() {}

    override fun preparePlayerCycle(player: Player) {}

    override fun playerCycle(player: Player) {}

    override fun completePlayerCycle(player: Player) {}
}
