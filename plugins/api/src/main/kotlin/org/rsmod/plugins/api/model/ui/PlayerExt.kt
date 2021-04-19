package org.rsmod.plugins.api.model.ui

import org.rsmod.game.event.impl.CloseModal
import org.rsmod.game.event.impl.CloseOverlay
import org.rsmod.game.event.impl.CloseTopLevel
import org.rsmod.game.event.impl.OpenModal
import org.rsmod.game.event.impl.OpenOverlay
import org.rsmod.game.event.impl.OpenTopLevel
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.game.model.ui.Component
import org.rsmod.game.model.ui.ComponentProperty
import org.rsmod.game.model.ui.DynamicComponentEvent
import org.rsmod.game.model.ui.InterfaceList
import org.rsmod.game.model.ui.UserInterface
import org.rsmod.game.model.ui.type.ComponentType
import org.rsmod.game.model.ui.type.InterfaceType
import org.rsmod.plugins.api.model.ui.gameframe.Gameframe
import org.rsmod.plugins.api.protocol.packet.server.IfCloseSub
import org.rsmod.plugins.api.protocol.packet.server.IfOpenSub
import org.rsmod.plugins.api.protocol.packet.server.IfOpenTop
import org.rsmod.plugins.api.protocol.packet.server.IfSetAnim
import org.rsmod.plugins.api.protocol.packet.server.IfSetEvents
import org.rsmod.plugins.api.protocol.packet.server.IfSetNpcHead
import org.rsmod.plugins.api.protocol.packet.server.IfSetObject
import org.rsmod.plugins.api.protocol.packet.server.IfSetPlayerHead
import org.rsmod.plugins.api.protocol.packet.server.IfSetText

fun Player.openGameframe(frame: Gameframe) {
    openTopLevel(frame.topLevel)
    frame.components.values.forEach { component ->
        openOverlay(component.inter, component.target)
    }
}

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
    target: Component,
    clickMode: InterfaceClickMode = InterfaceClickMode.Disabled
) {
    if (ui.modals.containsKey(target)) {
        warn { "Interface list already contains modal for target component (modal=$modal, target=$target)" }
        return
    }
    val event = OpenModal(this, target, modal)
    ui.modals[target] = modal
    submitEvent(event)
    write(IfOpenSub(modal.id, target.packed, clickMode.id))
}

fun Player.closeModal(modal: UserInterface) {
    val entry = ui.modals.entries.firstOrNull { it.value == modal }
    if (entry == null) {
        warn { "Interface list does not contain modal (modal=$modal)" }
        return
    }
    closeModal(entry.key)
}

/**
 * Close any modal opened in [target] component.
 */
fun Player.closeModal(target: Component) {
    val modal = ui.modals[target]
    if (modal == null) {
        warn { "Interface list does not contain a modal in target component (target=$target)" }
        return
    }
    val event = CloseModal(this, target, modal)
    val components = ui.properties.filterKeys { it.interfaceId == modal.id }.keys
    components.forEach(ui.properties::remove)
    ui.modals.remove(target)
    submitEvent(event)
    write(IfCloseSub(target.packed))
}

fun Player.openOverlay(
    overlay: UserInterface,
    target: Component,
    clickMode: InterfaceClickMode = InterfaceClickMode.Enabled
) {
    if (ui.overlays.contains(target)) {
        warn { "Interface list already contains overlay in target component (target=$target)" }
        return
    }
    val event = OpenOverlay(this, target, overlay)
    ui.overlays[target] = overlay
    submitEvent(event)
    write(IfOpenSub(overlay.id, target.packed, clickMode.id))
}

fun Player.closeOverlay(overlay: UserInterface) {
    val entry = ui.modals.entries.firstOrNull { it.value == overlay }
    if (entry == null) {
        warn { "Interface list does not contain overlay (overlay=$overlay)" }
        return
    }
    closeOverlay(entry.key)
}

/**
 * Close any overlay opened in [target] component.
 */
fun Player.closeOverlay(target: Component) {
    val overlay = ui.overlays[target]
    if (overlay == null) {
        warn { "Interface list does not contain an overlay in target component (target=$target)" }
        return
    }
    val event = CloseOverlay(this, target, overlay)
    val components = ui.propertyComponents(overlay)
    components.forEach(ui.properties::remove)
    ui.overlays.remove(target)
    submitEvent(event)
    write(IfCloseSub(target.packed))
}

fun Player.setComponentText(component: Component, text: String) {
    write(IfSetText(component.packed, text))
}

fun Player.setComponentNpc(component: Component, npc: NpcType) {
    write(IfSetNpcHead(component.packed, npc.id))
}

fun Player.setComponentPlayer(component: Component) {
    write(IfSetPlayerHead(component.packed))
}

fun Player.setComponentItem(component: Component, item: ItemType, amountOrZoom: Int = 1) {
    /* pay no attention to packet name */
    write(IfSetObject(component.packed, item.id, amountOrZoom))
}

fun Player.setComponentAnim(component: Component, anim: Int) {
    write(IfSetAnim(component.packed, anim))
}

fun Player.setComponentEvents(
    component: Component,
    range: IntRange,
    vararg events: InterfaceEvent
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

fun Player.setComponentEventsAll(component: Component, vararg events: InterfaceEvent) {
    setComponentEvents(component, -1..-1, *events)
}

fun Player.getComponentEvents(component: Component, range: IntRange): List<InterfaceEvent> {
    val property = ui.properties[component] ?: return emptyList()
    val event = property.firstOrNull { range.within(it.range) } ?: return emptyList()
    val types = InterfaceEvent.values
    return types.filter { (event.packed and it.flag) != 0 }
}

fun Player.openTopLevel(type: InterfaceType) {
    return openTopLevel(type.toUserInterface())
}

fun Player.closeTopLevel(type: InterfaceType) {
    return closeTopLevel(type.toUserInterface())
}

fun Player.openModal(
    modalType: InterfaceType,
    targetType: ComponentType,
    clickMode: InterfaceClickMode = InterfaceClickMode.Disabled
) {
    return openModal(modalType.toUserInterface(), targetType.toComponent(), clickMode)
}

fun Player.closeModal(modalType: InterfaceType) {
    return closeModal(modalType.toUserInterface())
}

fun Player.closeModal(targetType: ComponentType) {
    return closeModal(targetType.toComponent())
}

fun Player.openOverlay(
    overlayType: InterfaceType,
    targetType: ComponentType,
    clickMode: InterfaceClickMode = InterfaceClickMode.Enabled
) {
    return openOverlay(overlayType.toUserInterface(), targetType.toComponent(), clickMode)
}

fun Player.closeOverlay(overlayType: InterfaceType) {
    return closeOverlay(overlayType.toUserInterface())
}

fun Player.closeOverlay(targetType: ComponentType) {
    return closeOverlay(targetType.toComponent())
}

fun Player.setComponentText(type: ComponentType, text: String) {
    return setComponentText(type.toComponent(), text)
}

fun Player.setComponentNpc(type: ComponentType, npc: NpcType) {
    return setComponentNpc(type.toComponent(), npc)
}

fun Player.setComponentPlayer(type: ComponentType) {
    return setComponentPlayer(type.toComponent())
}

fun Player.setComponentItem(type: ComponentType, item: ItemType, amountOrZoom: Int = 1) {
    return setComponentItem(type.toComponent(), item, amountOrZoom)
}

fun Player.setComponentAnim(type: ComponentType, anim: Int) {
    return setComponentAnim(type.toComponent(), anim)
}

fun Player.setComponentEvents(type: ComponentType, range: IntRange, vararg events: InterfaceEvent) {
    return setComponentEvents(type.toComponent(), range, *events)
}

fun Player.setComponentEventsAll(type: ComponentType, vararg events: InterfaceEvent) {
    return setComponentEventsAll(type.toComponent(), *events)
}

fun Player.getComponentEvents(type: ComponentType, range: IntRange): List<InterfaceEvent> {
    return getComponentEvents(type.toComponent(), range)
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
