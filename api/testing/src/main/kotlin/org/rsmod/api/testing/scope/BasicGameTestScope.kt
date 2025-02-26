package org.rsmod.api.testing.scope

import org.rsmod.annotations.InternalApi
import org.rsmod.api.npc.events.NpcEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessContext
import org.rsmod.api.player.protect.ProtectedAccessContextFactory
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.route.BoundValidator
import org.rsmod.api.route.RayCastFactory
import org.rsmod.api.route.RayCastValidator
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.api.testing.GameCollisionState
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.collisionFactory
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.PathingEntityAvatar
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.player.SessionStateEvent
import org.rsmod.game.entity.util.PathingEntityCommon
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionFlagMap

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
        eventBus.publish(SessionStateEvent.LogIn(player))
        action(player)
        player.slotId = -1
        player.destroy()
        playerList.remove(slot)
    }

    /**
     * Differs from [withPlayer] in that this function only publishes the necessary
     * [SessionStateEvent.Initialize] event, and not [SessionStateEvent.LogIn] or
     * [SessionStateEvent.LogOut].
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
        eventBus.publish(NpcEvents.Spawn(npc))
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

    public fun PathingEntity.move(dest: CoordGrid): Unit = PathingEntityCommon.walk(this, dest)

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
