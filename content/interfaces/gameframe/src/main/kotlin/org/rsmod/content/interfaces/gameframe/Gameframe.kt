package org.rsmod.content.interfaces.gameframe

import org.rsmod.api.config.aliases.EnumComp
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.InterfaceType

interface Gameframe {
    val topLevel: InterfaceType
    val mappings: Map<EnumComp, EnumComp?>
    val overlays: List<Pair<InterfaceType, ComponentType>>

    operator fun component1(): InterfaceType = topLevel

    operator fun component2(): Map<EnumComp, EnumComp?> = mappings

    operator fun component3(): List<Pair<InterfaceType, ComponentType>> = overlays
}
