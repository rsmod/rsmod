package org.runerealm.content.interfaces.gameframe

import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.interf.InterfaceType
import org.runerealm.game.ui.Component

data class Gameframe(
    val topLevel: InterfaceType,
    val overlays: List<GameframeOverlay>,
    val mappings: Map<Component, Component>,
    val clientMode: Int,
    val resizable: Boolean,
    val isDefault: Boolean,
    val stoneArrangement: Boolean,
)

data class GameframeOverlay(val interf: InterfaceType, val target: ComponentType)

data class GameframeMove(val from: Gameframe, val dest: Gameframe, val intermediate: Gameframe?)
