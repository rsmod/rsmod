package org.rsmod.plugins.content.gameframe.build

import org.rsmod.plugins.api.gameframe_resize_normal
import org.rsmod.plugins.api.interf
import org.rsmod.plugins.api.model.ui.Gameframe
import org.rsmod.plugins.content.gameframe.util.GameframeUtil.standardOverlays
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public object GameframeResizeNormal : Gameframe {

    override val topLevel: NamedInterface = interf.gameframe_resize_normal

    override val mappings: Map<NamedComponent, NamedComponent> = emptyMap()

    override val overlays: List<Pair<NamedInterface, NamedComponent>> = standardOverlays()
}
