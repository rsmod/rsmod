package org.runerealm.api.game.process.world

import jakarta.inject.Inject
import org.runerealm.api.db.gateway.GameDbSynchronizer

public class WorldDbSyncProcess @Inject constructor(private val synchronizer: GameDbSynchronizer) {
    public fun process() {
        synchronizer.invokeCallbacks(CALLBACKS_PER_CYCLE)
    }

    private companion object {
        private const val CALLBACKS_PER_CYCLE: Int = 50
    }
}
