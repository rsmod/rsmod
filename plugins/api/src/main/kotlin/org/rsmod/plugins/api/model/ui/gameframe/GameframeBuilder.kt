package org.rsmod.plugins.api.model.ui.gameframe

import org.rsmod.plugins.api.cache.name.ui.ComponentNameMap
import org.rsmod.plugins.api.cache.name.ui.UserInterfaceNameMap

@DslMarker
private annotation class BuilderDsl

@BuilderDsl
class GameframeBuilder(
    var type: GameframeType? = null,
    var topLevel: String = "",
    private val components: MutableMap<String, GameframeNameComponent> = mutableMapOf()
) {

    fun component(init: GameframeComponentBuilder.() -> Unit) {
        val builder = GameframeComponentBuilder().apply(init)
        val component = builder.build()
        components[component.target] = component
    }

    fun build(interfaceMap: UserInterfaceNameMap, componentMap: ComponentNameMap): Gameframe {
        val type = type ?: error("Gameframe type must be set")
        val topLevel = interfaceMap.getValue(topLevel)
        val components = components.toComponentMap(interfaceMap, componentMap)
        return Gameframe(type, topLevel, components)
    }

    private fun Map<String, GameframeNameComponent>.toComponentMap(
        interfaces: UserInterfaceNameMap,
        components: ComponentNameMap
    ): GameframeComponentMap {
        val map = entries.associate { entry ->
            val component = entry.value
            val name = component.name
            val inter = interfaces.getValue(component.inter)
            val target = components.getValue(component.target)
            name to GameframeComponent(component.name, inter, target)
        }
        return GameframeComponentMap(LinkedHashMap(map))
    }
}

@BuilderDsl
class GameframeComponentBuilder(
    var name: String = "",
    var inter: String = "",
    var target: String = ""
) {

    fun build(): GameframeNameComponent {
        check(name.isNotBlank()) { "Component name must be set" }
        return GameframeNameComponent(
            name = name,
            inter = inter,
            target = target
        )
    }
}
