package org.rsmod.plugins.content.gameframe.util

import org.rsmod.plugins.api.cache.type.enums.EnumTypeList
import org.rsmod.plugins.api.model.ui.StandardGameframe
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedEnum

public object GameframeUtil {

    public fun build(
        ref: StandardGameframe,
        enumTypes: EnumTypeList,
        componentMap: NamedEnum<NamedComponent, NamedComponent>
    ): Iterable<NamedComponent> {
        val mappedComponents = enumTypes[componentMap]
        val overlays = mutableListOf<NamedComponent>()
        ref.overlays.forEach { targetRef ->
            val overlayRef = ref.topLevel.child(targetRef.child)
            val mappedComponent = mappedComponents[overlayRef] ?: return@forEach
            overlays += targetRef.parent().child(mappedComponent.child)
        }
        return overlays
    }
}
