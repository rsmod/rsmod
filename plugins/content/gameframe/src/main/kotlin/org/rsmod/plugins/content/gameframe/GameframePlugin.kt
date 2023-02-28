package org.rsmod.plugins.content.gameframe

import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.api.chatbox_unlocked
import org.rsmod.plugins.api.openGameframe
import org.rsmod.plugins.api.setVarbit
import org.rsmod.plugins.api.varbit
import javax.inject.Inject

public class GameframePlugin @Inject constructor(private val varbits: VarbitTypeList) {

    public fun initialize(player: Player) {
        player.openGameframe(GameframeResizeNormal)
        player.setVarbit(true, varbits[varbit.chatbox_unlocked])
    }
}
