package org.rsmod.plugins.content.gameframe

import jakarta.inject.Inject
import org.rsmod.plugins.content.gameframe.build.GameframeFixed
import org.rsmod.plugins.content.gameframe.build.GameframeResizeList
import org.rsmod.plugins.content.gameframe.build.GameframeResizeNormal

public data class GameframePlugin @Inject constructor(
    public val fixed: GameframeFixed,
    public val resizeList: GameframeResizeList
) {

    public val resizeNormal: GameframeResizeNormal get() = GameframeResizeNormal
}
