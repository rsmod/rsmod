package org.rsmod.game.client

import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList

public object NoopClient : Client<Any, Any> {
    override fun open(service: Any, player: Player) {}

    override fun close(service: Any, player: Player) {}

    override fun write(message: Any) {}

    override fun read(player: Player) {}

    override fun flush() {}

    override fun prePlayerCycle(player: Player, objTypes: ObjTypeList) {}

    override fun postPlayerCycle(player: Player) {}
}
