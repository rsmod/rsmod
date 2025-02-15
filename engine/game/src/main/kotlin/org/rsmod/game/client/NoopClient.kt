package org.rsmod.game.client

import org.rsmod.game.entity.Player

public object NoopClient : Client<Any, Any> {
    override fun open(service: Any, player: Player) {}

    override fun close(service: Any, player: Player) {}

    override fun write(message: Any) {}

    override fun read(player: Player) {}

    override fun flush() {}
}

public object NoopClientCycle : ClientCycle {
    override fun preCycle(player: Player) {}

    override fun postCycle(player: Player) {}
}
