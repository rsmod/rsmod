package org.rsmod.api.registry.controller

import jakarta.inject.Inject
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Controller
import org.rsmod.game.entity.Controller.Companion.INVALID_SLOT
import org.rsmod.game.entity.ControllerList
import org.rsmod.map.zone.ZoneKey

public class ControllerRegistry
@Inject
constructor(private val mapClock: MapClock, private val controllerList: ControllerList) {
    public val zones: ZoneControllerMap = ZoneControllerMap()

    public fun count(): Int = zones.controllerCount()

    public fun add(controller: Controller): ControllerRegistryResult.Add {
        val slot =
            controllerList.nextFreeSlot() ?: return ControllerRegistryResult.Add.NoAvailableSlot
        controllerList[slot] = controller
        controller.slotId = slot
        controller.creationCycle = mapClock.cycle
        zoneAdd(controller, ZoneKey.from(controller.coords))
        return ControllerRegistryResult.Add.Success
    }

    public fun del(controller: Controller): ControllerRegistryResult.Delete {
        val slot = controller.slotId
        if (slot == INVALID_SLOT) {
            return ControllerRegistryResult.Delete.UnexpectedSlot
        } else if (controllerList[slot] != controller) {
            return ControllerRegistryResult.Delete.ListSlotMismatch(controllerList[slot])
        }
        controllerList.remove(controller.slotId)
        zoneDel(controller, ZoneKey.from(controller.coords))
        controller.slotId = INVALID_SLOT
        controller.destroy()
        return ControllerRegistryResult.Delete.Success
    }

    public fun findAll(zone: ZoneKey): Sequence<Controller> {
        val entries = zones[zone] ?: return emptySequence()
        return entries.entries.asSequence()
    }

    private fun zoneDel(controller: Controller, zone: ZoneKey) {
        require(zone != ZoneKey.NULL) { "Invalid zone for controller: $controller" }
        val oldZone = zones[zone]
        oldZone?.remove(controller)
    }

    private fun zoneAdd(controller: Controller, zone: ZoneKey) {
        require(zone != ZoneKey.NULL) { "Invalid zone for controller: $controller" }
        val newZone = zones.getOrPut(zone)
        check(controller !in newZone) {
            "Controller already registered to zone($zone): $controller"
        }
        newZone.add(controller)
    }
}
