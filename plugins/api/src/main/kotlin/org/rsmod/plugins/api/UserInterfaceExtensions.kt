package org.rsmod.plugins.api

import org.rsmod.game.ui.Component
import org.rsmod.game.ui.ComponentProperty
import org.rsmod.game.ui.UserInterface
import org.rsmod.game.ui.UserInterfaceMap
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public fun UserInterfaceMap.openTopLevel(topLevel: NamedInterface) {
    this.topLevel += UserInterface(topLevel.id)
}

public fun UserInterfaceMap.openOverlay(overlay: NamedInterface, target: NamedComponent) {
    overlays[Component(target.packed)] = UserInterface(overlay.id)
}

public fun UserInterfaceMap.openModal(modal: NamedInterface, target: NamedComponent) {
    modals[Component(target.packed)] = UserInterface(modal.id)
}

public fun UserInterfaceMap.setProperties(component: NamedComponent, range: IntRange, events: Int) {
    properties[Component(component.packed)] = ComponentProperty(range, events)
}

public fun UserInterfaceMap.setGameframe(references: Map<NamedComponent, NamedComponent>) {
    gameframe.clear()
    references.forEach { (original, current) ->
        gameframe[Component(original.packed)] = Component(current.packed)
    }
}
