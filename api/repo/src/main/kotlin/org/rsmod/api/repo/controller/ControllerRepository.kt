package org.rsmod.api.repo.controller

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import jakarta.inject.Inject
import org.rsmod.api.registry.controller.ControllerRegistry
import org.rsmod.game.entity.Controller
import org.rsmod.game.entity.ControllerList
import org.rsmod.game.type.controller.ControllerType
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class ControllerRepository
@Inject
constructor(private val registry: ControllerRegistry, private val controllerList: ControllerList) {
    private val delControllers = ObjectArrayList<Controller>()

    public fun add(controller: Controller, duration: Int) {
        val add = registry.add(controller)
        check(add.isSuccess) { "Failed to add controller. (result=$add, controller=$controller)" }
        controller.duration(duration)
    }

    public fun del(controller: Controller) {
        check(controller.duration > -1) { "Duration must be greater than -1: $controller." }
        val del = registry.del(controller)
        check(del.isSuccess) {
            "Failed to delete controller. (result=$del, controller=$controller)"
        }
    }

    public fun findAll(zone: ZoneKey): Sequence<Controller> = registry.findAll(zone)

    public fun findAll(coords: CoordGrid): Sequence<Controller> =
        findAll(ZoneKey.from(coords)).filter { it.coords == coords }

    public fun findExact(coords: CoordGrid): Controller? =
        findAll(ZoneKey.from(coords)).firstOrNull { it.coords == coords }

    public fun findExact(coords: CoordGrid, type: ControllerType): Controller? =
        findAll(ZoneKey.from(coords)).firstOrNull { it.coords == coords && it.id == type.id }

    internal fun processDurations() {
        computeDurations()
        processDelDurations()
    }

    private fun computeDurations() {
        for (controller in controllerList) {
            if (controller.duration-- <= 0) {
                delControllers.add(controller)
            }
        }
    }

    private fun processDelDurations() {
        for (controller in delControllers) {
            registry.del(controller)
        }
        delControllers.clear()
    }
}
