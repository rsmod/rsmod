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
    overlays[Component(target.id)] = UserInterface(overlay.id)
}

public fun UserInterfaceMap.openModal(modal: NamedInterface, target: NamedComponent) {
    modals[Component(target.id)] = UserInterface(modal.id)
}

public fun UserInterfaceMap.setProperties(component: NamedComponent, range: IntRange, events: Int) {
    properties[Component(component.id)] = ComponentProperty(range, events)
}
