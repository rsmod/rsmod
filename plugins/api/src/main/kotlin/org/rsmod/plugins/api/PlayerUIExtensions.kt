@file:Suppress("DuplicatedCode")

package org.rsmod.plugins.api

import org.rsmod.game.model.mob.Player
import org.rsmod.game.ui.Component
import org.rsmod.plugins.api.model.event.DownstreamEvent
import org.rsmod.plugins.api.model.ui.InterfaceType
import org.rsmod.plugins.api.model.ui.StandardGameframe
import org.rsmod.plugins.api.net.downstream.IfOpenSub
import org.rsmod.plugins.api.net.downstream.IfOpenTop
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public fun Player.openGameframe(gameframe: StandardGameframe) {
    val topLevel = gameframe.topLevel
    val overlays = gameframe.overlays
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
    ui.openOverlay(overlay, target)
    publish(overlay.id, DownstreamEvent.IfOpenSub(overlay, target, InterfaceType.Overlay))
    downstream += IfOpenSub(overlay.id, target.id, InterfaceType.Overlay)
}

public fun Player.openModal(modal: NamedInterface, target: NamedComponent) {
    closeModal(target)
    ui.openModal(modal, target)
    publish(modal.id, DownstreamEvent.IfOpenSub(modal, target, InterfaceType.Modal))
    downstream += IfOpenSub(modal.id, target.id, InterfaceType.Modal)
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
    val component = Component(target.id)
    val colliding = ui.overlays[component] ?: return
    val event = DownstreamEvent.IfCloseSub(sub = NamedInterface(colliding.id), target = target)
    publish(colliding.id, event)
    ui.overlays -= component
}

public fun Player.closeModal(target: NamedComponent) {
    val component = Component(target.id)
    val colliding = ui.modals[component] ?: return
    val event = DownstreamEvent.IfCloseSub(sub = NamedInterface(colliding.id), target = target)
    publish(colliding.id, event)
    ui.modals -= component
}
