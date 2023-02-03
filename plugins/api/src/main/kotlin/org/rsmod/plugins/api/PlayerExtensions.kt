package org.rsmod.plugins.api

import org.rsmod.game.model.mob.Player
import org.rsmod.game.types.NamedComponent
import org.rsmod.game.types.NamedInterface
import org.rsmod.plugins.api.model.ui.StandardGameframe
import org.rsmod.plugins.api.net.downstream.IfOpenSub
import org.rsmod.plugins.api.net.downstream.IfOpenTop

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
    downstream += IfOpenTop(topLevel.id)
}

public fun Player.openOverlay(overlay: NamedInterface, target: NamedComponent) {
    downstream += IfOpenSub(overlay.id, target.id, 1)
}
