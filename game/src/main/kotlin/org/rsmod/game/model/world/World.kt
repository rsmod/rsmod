package org.rsmod.game.model.world

import com.google.inject.Inject
import org.rsmod.game.queue.GameQueueList

class World(
    internal val queueList: GameQueueList
) {

    @Inject
    constructor() : this(GameQueueList())

    fun queue(block: suspend () -> Unit) = queueList.queue(block)
}
