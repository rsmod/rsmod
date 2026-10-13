package org.runerealm.api.game.process.world

import jakarta.inject.Inject
import org.runerealm.api.repo.EntityDelayedProcess

public class WorldDelayedProcess
@Inject
constructor(private val entityDelayed: EntityDelayedProcess) {
    public fun process() {
        entityDelayed.process()
    }
}
