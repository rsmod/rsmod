package org.rsmod.api.registry.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.PathingEntity
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.pathfinder.collision.CollisionFlagMap

public class NpcRegistry
@Inject
constructor(
    private val npcList: NpcList,
    private val collision: CollisionFlagMap,
    private val eventBus: EventBus,
) {
    public val zones: ZoneNpcMap = ZoneNpcMap()

    public fun count(): Int = zones.npcCount()

    public fun add(npc: Npc): Boolean {
        val slot = npcList.nextFreeSlot() ?: return false
        npcList[slot] = npc
        npc.slotId = slot
        npc.addBlockWalkCollision(collision, npc.coords)
        eventBus.publish(NpcEvents.Spawn(npc))
        npc.lastProcessedZone = ZoneKey.from(npc.coords)
        zoneAdd(npc, npc.lastProcessedZone)
        return true
    }

    public fun del(npc: Npc): Boolean {
        if (npc.slotId == PathingEntity.INVALID_SLOT) {
            return false
        } else if (npcList[npc.slotId] != npc) {
            return false
        }
        npcList.remove(npc.slotId)
        eventBus.publish(NpcEvents.Delete(npc))
        npc.removeBlockWalkCollision(collision, npc.coords)
        zoneDel(npc, npc.lastProcessedZone)
        npc.slotId = -1
        npc.disableAvatar()
        return true
    }

    public fun hide(npc: Npc) {
        // Note that the event is published _before_ the npc is fully removed from the zone, in case
        // said information is required by the listeners.
        eventBus.publish(NpcEvents.Hide(npc))

        // Remove the npc from their respective zone.
        npc.removeBlockWalkCollision(collision, npc.coords)
        zoneDel(npc, npc.lastProcessedZone)

        // Hide the npc client avatar.
        npc.hidden = true
        npc.hideAvatar()
    }

    public fun reveal(npc: Npc) {
        // Add the npc to the corresponding zone.
        npc.addBlockWalkCollision(collision, npc.coords)
        zoneAdd(npc, npc.lastProcessedZone)

        // Note that the event is published _after_ the npc is registered to the zone, in case said
        // information is required by the listeners.
        eventBus.publish(NpcEvents.Reveal(npc))

        // Reveal the npc client avatar.
        npc.hidden = false
        npc.revealAvatar()
    }

    public fun change(npc: Npc, from: ZoneKey, to: ZoneKey) {
        zoneDel(npc, from)
        zoneAdd(npc, to)
    }

    public fun findAll(coords: CoordGrid): Sequence<Npc> =
        findAll(ZoneKey.from(coords)).filter { it.coords == coords }

    public fun findAll(key: ZoneKey): Sequence<Npc> {
        val entries = zones[key] ?: return emptySequence()
        return entries.entries.asSequence()
    }

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
        rspAvatar.setInaccessible(true)
    }

    private fun Npc.hideAvatar() {
        hidden = true
        rspAvatar.setInaccessible(true)
    }

    private fun Npc.revealAvatar() {
        hidden = false
        rspAvatar.setInaccessible(false)
    }
}
