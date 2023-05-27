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
    val (topLevel, mappings, overlays) = gameframe
    ui.setGameframe(mappings)
    openTopLevel(topLevel)
    overlays.forEach { (overlay, target) ->
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
    val mapped = ui.gameframeTransform(target) ?: target
    ui.openOverlay(overlay, mapped)
    publish(overlay.id, DownstreamEvent.IfOpenSub(overlay, mapped, InterfaceType.Overlay))
    downstream += IfOpenSub(overlay.id, mapped.packed, InterfaceType.Overlay)
}

public fun Player.openModal(modal: NamedInterface, target: NamedComponent) {
    closeModal(target)
    val mapped = ui.gameframeTransform(target) ?: target
    ui.openModal(modal, mapped)
    publish(modal.id, DownstreamEvent.IfOpenSub(modal, mapped, InterfaceType.Modal))
    downstream += IfOpenSub(modal.id, mapped.packed, InterfaceType.Modal)
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
    val mapped = ui.gameframeTransform(target) ?: target
    val colliding = ui.overlays[Component(mapped.packed)] ?: return
    val event = DownstreamEvent.IfCloseSub(sub = NamedInterface(colliding.id), target = mapped)
    publish(colliding.id, event)
    ui.overlays -= Component(mapped.packed)
}

public fun Player.closeModal(target: NamedComponent) {
    val mapped = ui.gameframeTransform(target) ?: target
    val colliding = ui.modals[Component(mapped.packed)] ?: return
    val event = DownstreamEvent.IfCloseSub(sub = NamedInterface(colliding.id), target = mapped)
    publish(colliding.id, event)
    ui.modals -= Component(mapped.packed)
}
