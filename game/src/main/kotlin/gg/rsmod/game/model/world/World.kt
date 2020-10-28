package gg.rsmod.game.model.world

import com.google.inject.Inject
import gg.rsmod.game.queue.GameQueue
import gg.rsmod.game.queue.GameQueueList

class World(
    internal val queueList: GameQueueList
) {

    @Inject
    constructor() : this(GameQueueList())

    fun queue(block: suspend () -> Unit) = queueList.queue(block)
}
