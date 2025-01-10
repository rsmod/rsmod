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

    public fun add(controller: Controller, duration: Int): Boolean {
        val added = registry.add(controller)
        if (!added) {
            return false
        }
        controller.duration(duration)
        return true
    }

    public fun del(controller: Controller) {
        require(controller.duration > -1) { "Duration must be greater than -1: $controller." }
        registry.del(controller)
    }

    public fun findAll(key: ZoneKey): Sequence<Controller> = registry.findAll(key)

    public fun findAll(coords: CoordGrid): Sequence<Controller> = registry.findAll(coords)

    public fun findExact(coords: CoordGrid): Controller? =
        registry.findAll(coords).firstOrNull { it.coords == coords }

    public fun findExact(coords: CoordGrid, type: ControllerType): Controller? =
        registry.findAll(coords).firstOrNull { it.coords == coords && it.id == type.id }

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
