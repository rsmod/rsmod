package org.runerealm.content.interfaces.gameframe

import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.ui.ifMoveSub
import org.runerealm.api.player.ui.ifOpenSub
import org.runerealm.api.player.ui.ifOpenTop
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfSubType
import org.runerealm.game.ui.Component
import org.runerealm.game.ui.UserInterfaceMap

internal fun Player.openGameframe(gameframe: Gameframe, eventBus: EventBus) {
    ui.setGameframe(gameframe.mappings)
    for (overlay in gameframe.overlays) {
        // The xp drops overlay is opened and closed by the xp drops orb; on login it is only
        // opened when xp drops were left enabled.
        if (overlay.interf.id == interfaces.xp_drops.id && !xpDropsEnabled) {
            continue
        }
        ifOpenSub(overlay.interf, overlay.target, IfSubType.Overlay, eventBus)
    }
}

internal fun Player.moveGameframe(from: Gameframe, dest: Gameframe, eventBus: EventBus) {
    ifOpenTop(dest.topLevel)
    ui.setGameframe(dest.mappings)
    val moveComponents = StandardOverlays.move
    for (moveComponent in moveComponents) {
        val target = Component(moveComponent.packed)
        val sourceComponent =
            from.mappings[target]
                ?: error("Expected move target in source mapping: '${moveComponent.internalName}'")
        val destComponent =
            dest.mappings[target]
                ?: error("Expected move target in dest mapping: '${moveComponent.internalName}'")
        ifMoveSub(sourceComponent, destComponent, target, eventBus)
    }
}

private val Player.xpDropsEnabled by boolVarBit(varbits.xpdrops_enabled)

private fun UserInterfaceMap.setGameframe(mappings: Map<Component, Component>) {
    gameframe.clear()
    for ((original, translated) in mappings) {
        gameframe[original] = translated
    }
}
