package org.rsmod.api.game.process

import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.game.GameProcess

public class MainGameProcess
@Inject
constructor(private val gameCycle: GameCycle, private val eventBus: EventBus) : GameProcess {
    override fun startUp() {
        eventBus.publish(GameLifecycle.BootUp)
    }

    override fun shutDown() {
        eventBus.publish(GameLifecycle.ShutDown)
    }

    override fun cycle() {
        gameCycle.tick()
    }
}
