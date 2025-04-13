package org.rsmod.api.game.process

import jakarta.inject.Inject
import org.rsmod.api.game.process.controller.ControllerMainProcess
import org.rsmod.api.game.process.npc.NpcMainProcess
import org.rsmod.api.game.process.npc.NpcPreTickProcess
import org.rsmod.api.game.process.player.PlayerInputProcess
import org.rsmod.api.game.process.player.PlayerLoginProcess
import org.rsmod.api.game.process.player.PlayerLogoutProcess
import org.rsmod.api.game.process.player.PlayerMainProcess
import org.rsmod.api.game.process.player.PlayerPostTickProcess
import org.rsmod.api.game.process.player.PlayerRouteRequestProcess
import org.rsmod.api.game.process.world.WorldPostTickProcess
import org.rsmod.api.game.process.world.WorldQueueListProcess
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock

public data class GameCycle
@Inject
constructor(
    public val eventBus: EventBus,
    public val mapClock: MapClock,
    public val playerLogin: PlayerLoginProcess,
    public val worldQueue: WorldQueueListProcess,
    public val npcPreTick: NpcPreTickProcess,
    public val playerInput: PlayerInputProcess,
    public val playerRouteRequest: PlayerRouteRequestProcess,
    public val npcMain: NpcMainProcess,
    public val controllerMain: ControllerMainProcess,
    public val playerMain: PlayerMainProcess,
    public val playerLogout: PlayerLogoutProcess,
    public val worldPostTick: WorldPostTickProcess,
    public val playerPostTick: PlayerPostTickProcess,
) {
    public fun tick() {
        eventBus.publish(GameLifecycle.StartCycle)
        preTick()
        mapClock.tick()
        eventBus.publish(GameLifecycle.LateCycle)
        postTick()
        eventBus.publish(GameLifecycle.EndCycle)
    }

    private fun preTick() {
        worldQueue.process()
        npcPreTick.process()
        playerInput.process()
        playerRouteRequest.process()
        npcMain.process()
        controllerMain.process()
        playerMain.process()
        playerLogout.process()
        playerLogin.process()
    }

    /*
     * Making an educated guess that these processes occur _after_ the map clock cycles. Drew this
     * conclusion due to some clues, like `loc_del`. i.e., `loc_del(100)` will actually trigger
     * after 99 cycles, not 100.
     */
    private fun postTick() {
        worldPostTick.process()
        playerPostTick.process()
    }
}
