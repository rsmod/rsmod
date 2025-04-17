package org.rsmod.content.interfaces.gameframe.impl

import jakarta.inject.Inject
import org.rsmod.api.config.aliases.EnumComp
import org.rsmod.api.config.refs.BaseInterfaces
import org.rsmod.content.interfaces.gameframe.Gameframe
import org.rsmod.content.interfaces.gameframe.config.GameframeEnums
import org.rsmod.content.interfaces.gameframe.util.StandardOverlays
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.interf.InterfaceType

class ResizablePane @Inject constructor(private val enums: EnumTypeList) : Gameframe {
    override val topLevel: InterfaceType
        get() = BaseInterfaces.toplevel_osrs_stretch

    override val mappings: Map<EnumComp, EnumComp?>
        get() = enums[GameframeEnums.resizable_basic_pane_redirect]

    override val overlays: List<Pair<InterfaceType, ComponentType>> = StandardOverlays.create()
}
