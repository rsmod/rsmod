package org.rsmod.plugins.content.gameframe.build

import org.rsmod.plugins.api.cache.type.enums.EnumTypeList
import org.rsmod.plugins.api.enum
import org.rsmod.plugins.api.gameframe_resize_list
import org.rsmod.plugins.api.interf
import org.rsmod.plugins.api.model.ui.StandardGameframe
import org.rsmod.plugins.content.gameframe.util.GameframeUtil
import org.rsmod.plugins.content.gameframe.gameframe_resize_list_component_map
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class GameframeResizeList @Inject constructor(enums: EnumTypeList) : StandardGameframe {

    override val topLevel: NamedInterface = interf.gameframe_resize_list

    override val overlays: Iterable<NamedComponent> = GameframeUtil.build(
        ref = GameframeResizeNormal,
        enumTypes = enums,
        componentMap = enum.gameframe_resize_list_component_map
    )
}
