package org.runerealm.api.testing.scope

import org.runerealm.annotations.InternalApi
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessContext
import org.runerealm.api.player.protect.ProtectedAccessContextFactory
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.route.BoundValidator
import org.runerealm.api.route.RayCastFactory
import org.runerealm.api.route.RayCastValidator
import org.runerealm.api.route.RouteFactory
import org.runerealm.api.route.StepFactory
import org.runerealm.api.testing.GameCollisionState
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.factory.collisionFactory
import org.runerealm.events.EventBus
import org.runerealm.game.MapClock
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.NpcList
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.PathingEntityAvatar
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.entity.npc.NpcStateEvents
import org.runerealm.game.entity.player.SessionStateEvent
import org.runerealm.game.movement.MoveSpeed
import org.runerealm.game.movement.RouteRequestCoord
import org.runerealm.game.movement.RouteRequestPathingEntity
import org.runerealm.map.CoordGrid
import org.runerealm.routefinder.collision.CollisionFlagMap

public class BasicGameTestScope(private val eventBus: EventBus) {
    public val mapClock: MapClock = MapClock()
    public val playerList: PlayerList = PlayerList()
    public val npcList: NpcList = NpcList()

    public fun withPlayer(
        coords: CoordGrid = CoordGrid.ZERO,
        player: Player = Player(),
        action: Player.() -> Unit,
    ) {
        val slot = playerList.nextFreeSlot() ?: error("No available slot.")
        player.coords = coords
        player.slotId = slot
        playerList[slot] = player
        eventBus.publish(SessionStateEvent.Initialize(player))
        eventBus.publish(SessionStateEvent.Login(player))
        eventBus.publish(SessionStateEvent.EngineLogin(player))
        action(player)
        player.slotId = -1
        player.destroy()
        playerList.remove(slot)
    }

    /**
     * Differs from [withPlayer] in that this function only publishes the necessary
     * [SessionStateEvent.Initialize] event, and not [SessionStateEvent.Login] or
     * [SessionStateEvent.Delete].
     */
    public fun withPlayerInit(
        coords: CoordGrid = CoordGrid.ZERO,
        player: Player = Player(),
        action: Player.() -> Unit,
    ) {
        val slot = playerList.nextFreeSlot() ?: error("No available slot.")
        player.coords = coords
        player.slotId = slot
        playerList[slot] = player
        eventBus.publish(SessionStateEvent.Initialize(player))
        action(player)
        player.slotId = -1
        player.destroy()
        playerList.remove(slot)
    }

    @OptIn(InternalApi::class)
    public fun withNpc(npc: Npc, action: Npc.() -> Unit) {
        val slot = npcList.nextFreeSlot() ?: error("No available slot.")
        npc.slotId = slot
        npcList[slot] = npc
        eventBus.publish(NpcStateEvents.Spawn(npc))
        action(npc)
        npc.slotId = -1
        npc.clearUid()
        npc.destroy()
        npcList.remove(slot)
    }

    public fun withPathingEntity(entity: PathingEntity, action: PathingEntity.() -> Unit) {
        action(entity)
    }

    public fun withCollisionState(
        collision: CollisionFlagMap = collisionFactory.borrowSharedMap(),
        action: (GameCollisionState) -> Unit,
    ) {
        val rf = RouteFactory(collision)
        val rcf = RayCastFactory(collision)
        val sf = StepFactory(collision)
        val pv = RayCastValidator(collision)
        val bv = BoundValidator(collision)
        action(GameCollisionState(collision, rf, rcf, sf, pv, bv))
    }

    public fun Player.withProtectedAccess(
        context: ProtectedAccessContext = ProtectedAccessContextFactory.empty(),
        block: suspend ProtectedAccess.() -> Unit,
    ): Boolean = ProtectedAccessLauncher.withProtectedAccess(this, context, null, block)

    public fun PathingEntity.move(dest: CoordGrid) {
        val request = RouteRequestCoord(dest)
        routeRequest = request
    }

    public fun PathingEntity.move(target: PathingEntityAvatar, speed: MoveSpeed? = null) {
        val request = RouteRequestPathingEntity(target)
        routeRequest = request
        tempMoveSpeed = speed
    }

    public fun Player.protectedTeleport(collision: CollisionFlagMap, dest: CoordGrid): Boolean =
        withProtectedAccess {
            teleport(dest, collision)
        }

    public fun GameTestState.setMaxLevels(player: Player) {
        val stats = cacheTypes.stats.values
        for (stat in stats) {
            player.statMap.setBaseLevel(stat, stat.maxLevel.toByte())
            player.statMap.setCurrentLevel(stat, stat.maxLevel.toByte())
        }
    }
}
