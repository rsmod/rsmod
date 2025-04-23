package org.rsmod.api.registry.npc

import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.PathingEntity.Companion.INVALID_SLOT
import org.rsmod.game.entity.npc.NpcStateEvents
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap

public class NpcRegistry
@Inject
constructor(
    private val npcList: NpcList,
    private val collision: CollisionFlagMap,
    private val eventBus: EventBus,
) {
    public val zones: ZoneNpcMap = ZoneNpcMap()

    public fun count(): Int = zones.npcCount()

    public fun add(npc: Npc): NpcRegistryResult.Add {
        val slot = npcList.nextFreeSlot() ?: return NpcRegistryResult.Add.NoAvailableSlot
        npcList[slot] = npc
        npc.slotId = slot
        npc.assignUid()
        npc.addBlockWalkCollision(collision, npc.coords)
        eventBus.publish(NpcStateEvents.Create(npc))
        eventBus.publish(NpcStateEvents.Spawn(npc))
        npc.lastProcessedZone = ZoneKey.from(npc.coords)
        zoneAdd(npc, npc.lastProcessedZone)
        return NpcRegistryResult.Add.Success
    }

    public fun del(npc: Npc): NpcRegistryResult.Delete {
        val slot = npc.slotId
        if (slot == INVALID_SLOT) {
            return NpcRegistryResult.Delete.UnexpectedSlot
        } else if (npcList[slot] != npc) {
            return NpcRegistryResult.Delete.ListSlotMismatch(npcList[slot])
        }
        npcList.remove(npc.slotId)
        eventBus.publish(NpcStateEvents.Delete(npc))
        npc.removeBlockWalkCollision(collision, npc.coords)
        zoneDel(npc, npc.lastProcessedZone)
        npc.slotId = INVALID_SLOT
        npc.clearUid()
        npc.destroy()
        npc.disableAvatar()
        return NpcRegistryResult.Delete.Success
    }

    public fun hide(npc: Npc) {
        eventBus.publish(NpcStateEvents.Hide(npc))
        npc.removeBlockWalkCollision(collision, npc.coords)
        npc.hidden = true
        npc.hideAvatar()
    }

    public fun reveal(npc: Npc) {
        eventBus.publish(NpcStateEvents.Reveal(npc))
        npc.addBlockWalkCollision(collision, npc.coords)
        npc.hidden = false
        npc.revealAvatar()
    }

    public fun despawn(npc: Npc) {
        npc.removeBlockWalkCollision(collision, npc.coords)
        npc.hidden = true
        npc.hideAvatar()
    }

    public fun respawn(npc: Npc) {
        val deathZone = npc.lastProcessedZone
        val respawnCoords = npc.spawnCoords
        val respawnZone = ZoneKey.from(respawnCoords)

        change(npc, from = deathZone, to = respawnZone)
        npc.coords = respawnCoords
        npc.lastProcessedZone = respawnZone

        npc.addBlockWalkCollision(collision, npc.coords)
        eventBus.publish(NpcStateEvents.Respawn(npc))
        npc.hidden = false
        npc.revealAvatar()
    }

    public fun change(npc: Npc, from: ZoneKey, to: ZoneKey) {
        zoneDel(npc, from)
        zoneAdd(npc, to)
    }

    /**
     * Returns a sequence of all [Npc]s in the given [zone].
     *
     * _Note: This function does **not** filter out "hidden" npcs (e.g., those that are respawning).
     * If you want to exclude these, filter the result using `Npc.isValidTarget`._
     */
    public fun findAll(zone: ZoneKey): Sequence<Npc> {
        val entries = zones[zone] ?: return emptySequence()
        return entries.entries.asSequence()
    }

    /**
     * Returns a sequence of all [Npc]s in the given [coords].
     *
     * _Note: This function does **not** filter out "hidden" npcs (e.g., those that are respawning).
     * If you want to exclude these, filter the result using `Npc.isValidTarget`._
     */
    public fun findAll(coords: CoordGrid): Sequence<Npc> =
        findAll(ZoneKey.from(coords)).filter { it.coords == coords }

    private fun zoneDel(npc: Npc, zone: ZoneKey) {
        if (zone == ZoneKey.NULL) {
            return
        }
        val oldZone = zones[zone]
        oldZone?.remove(npc)
    }

    private fun zoneAdd(npc: Npc, zone: ZoneKey) {
        if (zone == ZoneKey.NULL) {
            return
        }
        val newZone = zones.getOrPut(zone)
        check(npc !in newZone) { "Npc already registered to zone($zone): $npc" }
        newZone.add(npc)
    }

    private fun Npc.disableAvatar() {
        infoProtocol.disable()
    }

    private fun Npc.hideAvatar() {
        hidden = true
        infoProtocol.hide()
    }

    private fun Npc.revealAvatar() {
        hidden = false
        infoProtocol.reveal()
    }
}
