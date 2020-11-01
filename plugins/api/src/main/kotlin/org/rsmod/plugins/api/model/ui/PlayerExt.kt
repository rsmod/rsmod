package org.rsmod.plugins.api.model.ui

import org.rsmod.game.event.impl.CloseModal
import org.rsmod.game.event.impl.CloseOverlay
import org.rsmod.game.event.impl.CloseTopLevel
import org.rsmod.game.event.impl.OpenModal
import org.rsmod.game.event.impl.OpenOverlay
import org.rsmod.game.event.impl.OpenTopLevel
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.game.model.ui.ComponentProperty
import org.rsmod.game.model.ui.DynamicComponentEvent
import org.rsmod.game.model.ui.InterfaceList
import org.rsmod.game.model.ui.UserInterface
import org.rsmod.plugins.api.protocol.packet.server.IfCloseSub
import org.rsmod.plugins.api.protocol.packet.server.IfOpenSub
import org.rsmod.plugins.api.protocol.packet.server.IfOpenTop
import org.rsmod.plugins.api.protocol.packet.server.IfSetEvents

fun Player.openTopLevel(userInterface: UserInterface) {
    if (ui.topLevel.contains(userInterface)) {
        warn { "Interface list already contains top-level interface (ui=$userInterface)" }
        return
    }
    val event = OpenTopLevel(this, userInterface)
    ui.topLevel.add(userInterface)
    submitEvent(event)
    write(IfOpenTop(userInterface.id))
}

fun Player.closeTopLevel(userInterface: UserInterface) {
    if (!ui.topLevel.contains(userInterface)) {
        warn { "Interface list does not contain top-level interface (ui=$userInterface)" }
        return
    }
    val event = CloseTopLevel(this, userInterface)
    ui.topLevel.remove(userInterface)
    submitEvent(event)
}

fun Player.openModal(
    modal: UserInterface,
    parent: Component,
    clickMode: InterfaceClickMode = InterfaceClickMode.Enabled
) {
    if (ui.modals.containsKey(parent)) {
        warn { "Interface list already contains modal for parent (modal=$modal, parent=$parent)" }
        return
    }
    val event = OpenModal(this, parent, modal)
    ui.modals[parent] = modal
    submitEvent(event)
    write(IfOpenSub(modal.id, parent.packed, clickMode.id))
}

fun Player.closeModal(modal: UserInterface) {
    val entry = ui.modals.entries.firstOrNull { it.value == modal }
    if (entry == null) {
        warn { "Interface list does not contain modal (modal=$modal)" }
        return
    }
    val parent = entry.key
    val event = CloseModal(this, parent, modal)
    val components = ui.properties.filterKeys { it.interfaceId == modal.id }.keys
    components.forEach(ui.properties::remove)
    ui.modals.remove(parent)
    submitEvent(event)
    write(IfCloseSub(parent.packed))
}

fun Player.openOverlay(
    overlay: UserInterface,
    parent: Component,
    clickMode: InterfaceClickMode = InterfaceClickMode.Enabled
) {
    if (ui.overlays.contains(parent)) {
        warn { "Interface list already contains overlay (modal=$parent)" }
        return
    }
    val event = OpenOverlay(this, parent, overlay)
    ui.overlays[parent] = overlay
    submitEvent(event)
    write(IfOpenSub(overlay.id, parent.packed, clickMode.id))
}

fun Player.closeOverlay(overlay: UserInterface) {
    val entry = ui.modals.entries.firstOrNull { it.value == overlay }
    if (entry == null) {
        warn { "Interface list does not contain overlay (overlay=$overlay)" }
        return
    }
    val parent = entry.key
    val event = CloseOverlay(this, parent, overlay)
    val components = ui.propertyComponents(overlay)
    components.forEach(ui.properties::remove)
    ui.overlays.remove(parent)
    submitEvent(event)
    write(IfCloseSub(parent.packed))
}

fun Player.setComponentEvents(
    component: Component,
    range: IntRange,
    events: Set<InterfaceEvent>
) {
    val packed = events.sumBy { it.flag }
    val event = DynamicComponentEvent(range, packed)
    val property = ui.properties.getOrPut(component) { ComponentProperty() }

    /* if there is a property occupying same bit range, we remove it */
    val occupying = property.firstOrNull { it.range == range }
    occupying?.let { property.remove(it) }

    /* try to add property within bit range */
    val added = property.add(event)
    if (!added) {
        /* find the property that is occupying one or more of the given bits */
        val occupiedBy = property.firstOrNull { it.range.within(range) }
        warn { "Component property bit-range already occupied (occupiedBy=$occupiedBy)" }
        return
    }
    write(IfSetEvents(component.packed, range, packed))
}

fun Player.getComponentEvents(component: Component, range: IntRange): List<InterfaceEvent> {
    val property = ui.properties[component] ?: return emptyList()
    val event = property.firstOrNull { range.within(it.range) } ?: return emptyList()
    val types = InterfaceEvent.values
    return types.filter { (event.packed and it.flag) != 0 }
}

private fun IntRange.within(other: IntRange): Boolean {
    return first >= other.first && last <= other.last
}

/**
 * Creates a set of [Component]s that belong to [userInterface] and have active
 * [ComponentProperty]s. Properties are set with [setComponentEvents].
 */
private fun InterfaceList.propertyComponents(userInterface: UserInterface): Set<Component> {
    return properties.filterKeys { it.interfaceId == userInterface.id }.keys
}

private val InterfaceClickMode.id: Int
    get() = when (this) {
        InterfaceClickMode.Disabled -> 0
        InterfaceClickMode.Enabled -> 1
    }
