package org.rsmod.plugins.api.model.ui.gameframe

import org.rsmod.game.model.ui.type.InterfaceType

data class Gameframe(
    val type: GameframeType,
    val topLevel: InterfaceType,
    val components: GameframeComponentMap
) : Map<String, GameframeComponent> by components
