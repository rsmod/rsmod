package org.rsmod.plugins.api.model.ui

import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public interface Gameframe {

    public val topLevel: NamedInterface

    public val mappings: Map<NamedComponent, NamedComponent>

    public val overlays: List<Pair<NamedInterface, NamedComponent>>

    public operator fun component1(): NamedInterface = topLevel

    public operator fun component2(): Map<NamedComponent, NamedComponent> = mappings

    public operator fun component3(): List<Pair<NamedInterface, NamedComponent>> = overlays
}
