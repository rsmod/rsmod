package org.runerealm.api.net.rsprot.player

import org.runerealm.game.type.comp.UnpackedComponentType
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.ui.UserInterfaceMap

internal object InterfaceEvents {
    fun isEnabled(
        ui: UserInterfaceMap,
        component: UnpackedComponentType,
        comsub: Int,
        event: IfEvent,
    ): Boolean {
        val verifyStaticEvents = comsub == -1
        return if (verifyStaticEvents) {
            // Static components may also have events enabled at runtime with `if_setevents`.
            component.hasEvent(event) || ui.hasEvent(component, 0, event)
        } else {
            ui.hasEvent(component, comsub, event)
        }
    }
}
