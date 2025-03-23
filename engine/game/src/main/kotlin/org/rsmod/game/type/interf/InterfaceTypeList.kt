package org.rsmod.game.type.interf

import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.comp.UnpackedComponentType
import org.rsmod.game.ui.Component

public data class InterfaceTypeList(public val types: MutableMap<Int, UnpackedInterfaceType>) :
    Map<Int, UnpackedInterfaceType> by types {
    public operator fun get(type: InterfaceType): UnpackedInterfaceType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")

    public operator fun get(component: Component): UnpackedInterfaceType =
        types[component.parent]
            ?: throw NoSuchElementException("Type is missing in the map: $component.")

    public companion object {
        public fun from(components: Iterable<UnpackedComponentType>): InterfaceTypeList {
            val grouped = components.sortedBy { it.layer }.groupBy { it.interfaceId }
            val mapped =
                grouped.mapValues { (interfaceId, components) ->
                    UnpackedInterfaceType(components, interfaceId, "")
                }
            return InterfaceTypeList(mapped.toMutableMap())
        }
    }
}
