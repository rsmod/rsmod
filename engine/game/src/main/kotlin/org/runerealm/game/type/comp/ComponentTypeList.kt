package org.runerealm.game.type.comp

import org.runerealm.game.type.TypeResolver
import org.runerealm.game.ui.Component

public data class ComponentTypeList(public val types: MutableMap<Int, UnpackedComponentType>) :
    Map<Int, UnpackedComponentType> by types {
    public operator fun get(type: ComponentType): UnpackedComponentType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")

    public operator fun get(component: Component): UnpackedComponentType =
        types[component.packed]
            ?: throw NoSuchElementException("Type is missing in the map: $component.")
}
