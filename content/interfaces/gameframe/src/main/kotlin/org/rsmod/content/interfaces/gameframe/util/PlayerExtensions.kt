package org.rsmod.content.interfaces.gameframe.util

import org.rsmod.api.config.aliases.EnumComp
import org.rsmod.api.player.ifOpenSub
import org.rsmod.api.player.ifOpenTop
import org.rsmod.content.interfaces.gameframe.Gameframe
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterfaceMap

fun Player.openGameframe(gameframe: Gameframe, eventBus: EventBus) {
    val (topLevel, mappings, overlays) = gameframe
    ui.setGameframe(mappings)
    ifOpenTop(topLevel, eventBus)
    for (overlay in overlays) {
        ifOpenSub(overlay.first, overlay.second, IfSubType.Overlay, eventBus)
    }
}

private fun UserInterfaceMap.setGameframe(mappings: Map<EnumComp, EnumComp?>) {
    gameframe.clear()
    for ((original, translated) in mappings) {
        if (translated != null) {
            val key = Component(original.packed)
            val value = Component(translated.packed)
            gameframe[key] = value
        }
    }
}
