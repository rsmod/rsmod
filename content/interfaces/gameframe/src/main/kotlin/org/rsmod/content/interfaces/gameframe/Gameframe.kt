package org.rsmod.content.interfaces.gameframe

import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.ui.Component

data class Gameframe(
    val topLevel: InterfaceType,
    val overlays: List<GameframeOverlay>,
    val mappings: Map<Component, Component>,
    val windowMode: Int,
    val clientMode: Int,
    val isDefault: Boolean,
    val stoneArrangement: Boolean,
)

data class GameframeOverlay(val interf: InterfaceType, val target: ComponentType)
