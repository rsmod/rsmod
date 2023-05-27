package org.rsmod.plugins.content.gameframe.build

import org.rsmod.plugins.api.enum
import org.rsmod.plugins.api.gameframe_resize_list
import org.rsmod.plugins.api.interf
import org.rsmod.plugins.api.model.ui.Gameframe
import org.rsmod.plugins.cache.config.enums.EnumTypeList
import org.rsmod.plugins.content.gameframe.gameframe_resize_list_component_map
import org.rsmod.plugins.content.gameframe.util.GameframeUtil.buildMappings
import org.rsmod.plugins.content.gameframe.util.GameframeUtil.standardOverlays
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class GameframeResizeList @Inject constructor(enums: EnumTypeList) : Gameframe {

    override val topLevel: NamedInterface = interf.gameframe_resize_list

    override val mappings: Map<NamedComponent, NamedComponent> = buildMappings(
        enums[enum.gameframe_resize_list_component_map]
    )

    override val overlays: List<Pair<NamedInterface, NamedComponent>> = standardOverlays()
}
