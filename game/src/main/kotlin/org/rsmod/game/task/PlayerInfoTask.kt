package org.rsmod.game.task

import org.rsmod.game.model.mob.Player

public interface PlayerInfoTask {

    public fun init(player: Player)

    public fun execute()
}
