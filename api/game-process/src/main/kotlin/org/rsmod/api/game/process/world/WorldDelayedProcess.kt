package org.rsmod.api.game.process.world

import jakarta.inject.Inject
import org.rsmod.api.repo.EntityDelayedProcess

public class WorldDelayedProcess
@Inject
constructor(private val entityDelayed: EntityDelayedProcess) {
    public fun process() {
        entityDelayed.process()
    }
}
