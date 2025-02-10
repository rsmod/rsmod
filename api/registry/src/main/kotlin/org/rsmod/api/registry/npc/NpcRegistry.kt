package org.rsmod.api.registry.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.PathingEntity.Companion.INVALID_SLOT
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
        val slot = npcList.nextFreeSlot() ?: return NpcRegistryResult.AddErrorInvalidSlot
        npcList[slot] = npc
        npc.slotId = slot
        npc.addBlockWalkCollision(collision, npc.coords)
        eventBus.publish(NpcEvents.Create(npc))
        eventBus.publish(NpcEvents.Spawn(npc))
        npc.lastProcessedZone = ZoneKey.from(npc.coords)
        zoneAdd(npc, npc.lastProcessedZone)
        return NpcRegistryResult.AddSuccess
    }

    public fun del(npc: Npc): NpcRegistryResult.Delete {
        val slot = npc.slotId
        if (slot == INVALID_SLOT) {
            return NpcRegistryResult.DeleteErrorInvalidSlot
        } else if (npcList[slot] != npc) {
            return NpcRegistryResult.DeleteErrorSlotMismatch(npcList[slot])
        }
        npcList.remove(npc.slotId)
        eventBus.publish(NpcEvents.Delete(npc))
        npc.removeBlockWalkCollision(collision, npc.coords)
        zoneDel(npc, npc.lastProcessedZone)
        npc.slotId = INVALID_SLOT
        npc.destroy()
        npc.disableAvatar()
        return NpcRegistryResult.DeleteSuccess
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

    public fun findAll(zone: ZoneKey): Sequence<Npc> {
        val entries = zones[zone] ?: return emptySequence()
        return entries.entries.asSequence()
    }

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
