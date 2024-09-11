package org.rsmod.api.game.process

import jakarta.inject.Inject
import org.rsmod.api.game.process.controller.ControllerMainProcessor
import org.rsmod.api.game.process.npc.NpcHuntProcessor
import org.rsmod.api.game.process.npc.NpcMainProcessor
import org.rsmod.api.game.process.player.PlayerInputProcessor
import org.rsmod.api.game.process.player.PlayerMainProcessor
import org.rsmod.api.game.process.player.PlayerOutputProcessor
import org.rsmod.api.game.process.player.PlayerRouteRequestProcess
import org.rsmod.api.game.process.world.WorldLateProcessor
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock

public data class GameCycle
@Inject
constructor(
    public val eventBus: EventBus,
    public val mapClock: MapClock,
    public val npcHuntProcess: NpcHuntProcessor,
    public val playerInputProcessor: PlayerInputProcessor,
    public val playerRouteRequestProcessor: PlayerRouteRequestProcess,
    public val npcMainProcessor: NpcMainProcessor,
    public val controllerMainProcessor: ControllerMainProcessor,
    public val playerMainProcessor: PlayerMainProcessor,
    public val worldLateProcessor: WorldLateProcessor,
    public val playerOutputProcessor: PlayerOutputProcessor,
) {
    public fun tick() {
        eventBus.publish(GameLifecycle.StartCycle)
        preTick()
        mapClock.tick()
        postTick()
        eventBus.publish(GameLifecycle.EndCycle)
    }

    private fun preTick() {
        npcHuntProcess.process()
        playerInputProcessor.process()
        playerRouteRequestProcessor.process()
        npcMainProcessor.process()
        controllerMainProcessor.process()
        playerMainProcessor.process()
    }

    /*
     * Making an educated guess that these processes occur _after_ the map clock cycles. Drew this
     * conclusion due to some clues, like `loc_del`. i.e., `loc_del(100)` will actually trigger
     * after 99 cycles, not 100.
     */
    private fun postTick() {
        worldLateProcessor.process()
        playerOutputProcessor.process()
    }
}
