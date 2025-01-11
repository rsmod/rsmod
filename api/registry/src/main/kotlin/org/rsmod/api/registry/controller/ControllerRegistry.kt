package org.rsmod.api.registry.controller

import jakarta.inject.Inject
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Controller
import org.rsmod.game.entity.Controller.Companion.INVALID_SLOT
import org.rsmod.game.entity.ControllerList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class ControllerRegistry
@Inject
constructor(private val mapClock: MapClock, private val controllerList: ControllerList) {
    public val zones: ZoneControllerMap = ZoneControllerMap()

    public fun count(): Int = zones.controllerCount()

    public fun add(controller: Controller) {
        val slot = controllerList.nextFreeSlot()
        checkNotNull(slot) { "Could not find free slot for controller: $controller" }
        controllerList[slot] = controller
        controller.slotId = slot
        controller.creationCycle = mapClock.cycle
        zoneAdd(controller, ZoneKey.from(controller.coords))
    }

    public fun del(controller: Controller) {
        check(controller.slotId != INVALID_SLOT) {
            "Controller does not have a valid slotId. (controller=$controller)"
        }
        check(controllerList[controller.slotId] == controller) {
            "Controller is not registered in `ControllerList.` (controller=$controller)"
        }
        controllerList.remove(controller.slotId)
        zoneDel(controller, ZoneKey.from(controller.coords))
        controller.slotId = INVALID_SLOT
        controller.destroy()
    }

    public fun findAll(key: ZoneKey): Sequence<Controller> {
        val entries = zones[key] ?: return emptySequence()
        return entries.entries.asSequence()
    }

    public fun findAll(coords: CoordGrid): Sequence<Controller> =
        findAll(ZoneKey.from(coords)).filter { it.coords == coords }

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
