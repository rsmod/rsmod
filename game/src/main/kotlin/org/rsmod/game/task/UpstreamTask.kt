package org.rsmod.game.task

import org.rsmod.game.model.UpstreamList
import org.rsmod.game.model.mob.Player

public interface UpstreamTask {

    public fun readAll(player: Player, upstream: UpstreamList)
}
