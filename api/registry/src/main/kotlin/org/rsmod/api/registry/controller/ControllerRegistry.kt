package org.rsmod.api.registry.controller

import jakarta.inject.Inject
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Controller
import org.rsmod.game.entity.ControllerList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class ControllerRegistry
@Inject
constructor(private val mapClock: MapClock, private val controllerList: ControllerList) {
    public val zones: ZoneControllerMap = ZoneControllerMap()

    public fun count(): Int = zones.controllerCount()

    public fun add(controller: Controller): Boolean {
        val slot = controllerList.nextFreeSlot() ?: return false
        controllerList[slot] = controller
        controller.slotId = slot
        controller.creationCycle = mapClock.cycle
        zoneAdd(controller, ZoneKey.from(controller.coords))
        return true
    }

    public fun del(controller: Controller): Boolean {
        if (controller.slotId == Controller.INVALID_SLOT) {
            return false
        } else if (controllerList[controller.slotId] != controller) {
            return false
        }
        controllerList.remove(controller.slotId)
        zoneDel(controller, ZoneKey.from(controller.coords))
        controller.slotId = Controller.INVALID_SLOT
        controller.destroy()
        return true
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
