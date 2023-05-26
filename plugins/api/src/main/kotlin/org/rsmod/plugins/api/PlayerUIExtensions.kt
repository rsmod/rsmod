@file:Suppress("DuplicatedCode")

package org.rsmod.plugins.api

import org.rsmod.game.model.mob.Player
import org.rsmod.game.ui.Component
import org.rsmod.plugins.api.model.event.DownstreamEvent
import org.rsmod.plugins.api.model.ui.Gameframe
import org.rsmod.plugins.api.model.ui.InterfaceType
import org.rsmod.plugins.api.net.downstream.IfOpenSub
import org.rsmod.plugins.api.net.downstream.IfOpenTop
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public fun Player.openGameframe(gameframe: Gameframe) {
    val topLevel = gameframe.topLevel
    val references = gameframe.references
    val overlays = gameframe.overlays
    ui.setGameframe(references)
    openTopLevel(topLevel)
    overlays.forEach {
        val overlay = NamedInterface(it.interfaceId)
        val target = NamedComponent(topLevel.id, it.child)
        openOverlay(overlay, target)
    }
}

public fun Player.openTopLevel(topLevel: NamedInterface) {
    closeTopLevels()
    ui.openTopLevel(topLevel)
    publish(topLevel.id, DownstreamEvent.IfOpenTop(topLevel))
    downstream += IfOpenTop(topLevel.id)
}

public fun Player.openOverlay(overlay: NamedInterface, target: NamedComponent) {
    closeOverlay(target)
    val converted = ui.gameframe[Component(target.id)]?.let { NamedComponent(it.packed) } ?: target
    ui.openOverlay(overlay, converted)
    publish(overlay.id, DownstreamEvent.IfOpenSub(overlay, converted, InterfaceType.Overlay))
    downstream += IfOpenSub(overlay.id, converted.id, InterfaceType.Overlay)
}

public fun Player.openModal(modal: NamedInterface, target: NamedComponent) {
    closeModal(target)
    val converted = ui.gameframe[Component(target.id)]?.let { NamedComponent(it.packed) } ?: target
    ui.openModal(modal, converted)
    publish(modal.id, DownstreamEvent.IfOpenSub(modal, converted, InterfaceType.Modal))
    downstream += IfOpenSub(modal.id, converted.id, InterfaceType.Modal)
}

public fun Player.closeTopLevels() {
    ui.topLevel.forEach { prev ->
        val event = DownstreamEvent.IfCloseTop(NamedInterface(prev.id))
        publish(prev.id, event)
    }
    ui.topLevel.clear()
}

public fun Player.closeInterface(interf: NamedInterface) {
    ui.overlays.entries.firstOrNull { it.value.id == interf.id }?.let { overlay ->
        closeOverlay(NamedComponent(overlay.key.packed))
    }
    ui.modals.entries.firstOrNull { it.value.id == interf.id }?.let { modal ->
        closeModal(NamedComponent(modal.key.packed))
    }
}

public fun Player.closeSub(target: NamedComponent) {
    closeOverlay(target)
    closeModal(target)
}

public fun Player.closeOverlay(target: NamedComponent) {
    val converted = ui.gameframe[Component(target.id)]?.let { NamedComponent(it.packed) } ?: target
    val colliding = ui.overlays[Component(converted.id)] ?: return
    val event = DownstreamEvent.IfCloseSub(sub = NamedInterface(colliding.id), target = converted)
    publish(colliding.id, event)
    ui.overlays -= Component(converted.id)
}

public fun Player.closeModal(target: NamedComponent) {
    val converted = ui.gameframe[Component(target.id)]?.let { NamedComponent(it.packed) } ?: target
    val colliding = ui.modals[Component(converted.id)] ?: return
    val event = DownstreamEvent.IfCloseSub(sub = NamedInterface(colliding.id), target = converted)
    publish(colliding.id, event)
    ui.modals -= Component(converted.id)
}
