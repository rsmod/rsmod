package org.rsmod.plugins.api.model.ui.gameframe

import org.rsmod.game.model.ui.type.ComponentType
import org.rsmod.game.model.ui.type.InterfaceType

data class GameframeComponent(val name: String, val inter: InterfaceType, val target: ComponentType)

data class GameframeNameComponent(val name: String, val inter: String, val target: String)

class GameframeComponentMap(
    private val components: LinkedHashMap<String, GameframeComponent>
) : Map<String, GameframeComponent> by components {

    fun add(component: GameframeComponent) {
        components[component.name] = component
    }
}
