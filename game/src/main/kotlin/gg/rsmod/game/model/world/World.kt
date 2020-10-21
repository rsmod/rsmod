package gg.rsmod.game.model.world

import com.google.inject.Inject
import gg.rsmod.game.model.queue.GameQueue
import gg.rsmod.game.model.queue.GameQueueList

class World(
    internal val queueList: GameQueueList
) {

    @Inject
    constructor() : this(GameQueueList())

    fun queue(block: suspend GameQueue.() -> Unit) = queueList.queue(block)
}
