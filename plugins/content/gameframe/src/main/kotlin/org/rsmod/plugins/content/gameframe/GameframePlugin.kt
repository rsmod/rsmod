package org.rsmod.plugins.content.gameframe

import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.api.chatbox_unlocked
import org.rsmod.plugins.api.model.ui.StandardGameframe
import org.rsmod.plugins.api.openGameframe
import org.rsmod.plugins.api.setVarbit
import org.rsmod.plugins.api.varbit
import org.rsmod.plugins.content.gameframe.build.GameframeFixed
import org.rsmod.plugins.content.gameframe.build.GameframeResizeList
import org.rsmod.plugins.content.gameframe.build.GameframeResizeNormal
import javax.inject.Inject

public class GameframePlugin @Inject constructor(
    private val varbits: VarbitTypeList,
    public val fixed: GameframeFixed,
    public val resizeList: GameframeResizeList
) {

    public val resizeNormal: StandardGameframe get() = GameframeResizeNormal

    public fun initialize(player: Player) {
        player.openGameframe(fixed)
        player.setVarbit(true, varbits[varbit.chatbox_unlocked])
    }
}
