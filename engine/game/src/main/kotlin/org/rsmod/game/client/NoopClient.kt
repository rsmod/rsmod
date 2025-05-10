package org.rsmod.game.client

import org.rsmod.game.entity.Player

public object NoopClient : Client<Any, Any> {
    override fun close() {}

    override fun write(message: Any) {}

    override fun read(player: Player) {}

    override fun flush() {}

    override fun flushHighPriority() {}

    override fun unregister(service: Any, player: Player) {}
}

public object NoopClientCycle : ClientCycle {
    override fun update(player: Player) {}

    override fun flush(player: Player) {}
}
