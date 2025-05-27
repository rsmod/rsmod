package org.rsmod.content.interfaces.gameframe

import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.cinematic.Cinematic
import org.rsmod.api.player.ui.ifMoveSub
import org.rsmod.api.player.ui.ifOpenSub
import org.rsmod.api.player.ui.ifOpenTop
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterfaceMap

private var Player.stoneArrangements by boolVarBit(varbits.resizable_stone_arrangement)

internal fun Player.openGameframe(gameframe: Gameframe, eventBus: EventBus) {
    ui.setGameframe(gameframe.mappings)
    for (overlay in gameframe.overlays) {
        ifOpenSub(overlay.interf, overlay.target, IfSubType.Overlay, eventBus)
    }
}

internal fun Player.changeGameframe(gameframe: Gameframe, eventBus: EventBus) {
    if (stoneArrangements != gameframe.stoneArrangement) {
        stoneArrangements = gameframe.stoneArrangement
    }
    ifOpenTop(gameframe.topLevel)
    val moveTargets = StandardOverlays.move
    for (moveTarget in moveTargets) {
        val target = Component(moveTarget.packed)
        val source = ui.gameframe[target]
        val dest = gameframe.mappings[target]
        if (dest == null) {
            val message = "Expected move target in gameframe mapping: '${moveTarget.internalName}'"
            throw IllegalStateException(message)
        }
        ifMoveSub(source, dest, target, eventBus)
    }
    ui.setGameframe(gameframe.mappings)
    Cinematic.syncMinimapState(this)
}

private fun UserInterfaceMap.setGameframe(mappings: Map<Component, Component>) {
    gameframe.clear()
    for ((original, translated) in mappings) {
        gameframe[original] = translated
    }
}
