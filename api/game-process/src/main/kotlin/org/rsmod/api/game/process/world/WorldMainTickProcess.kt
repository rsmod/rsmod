package org.rsmod.api.game.process.world

import jakarta.inject.Inject

public class WorldMainTickProcess
@Inject
constructor(
    private val dbSync: WorldDbSyncProcess,
    private val queues: WorldQueueListProcess,
    private val delayed: WorldDelayedProcess,
) {
    public fun process() {
        dbSync.process()
        queues.process()
        delayed.process()
    }
}
