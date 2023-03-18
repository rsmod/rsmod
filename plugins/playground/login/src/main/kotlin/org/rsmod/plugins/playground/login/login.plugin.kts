package org.rsmod.plugins.playground.login

import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.api.chatbox_unlocked
import org.rsmod.plugins.api.message
import org.rsmod.plugins.api.onLogIn
import org.rsmod.plugins.api.runClientScript
import org.rsmod.plugins.api.setVarbit
import org.rsmod.plugins.api.varbit

private val varbits: VarbitTypeList by inject()

onLogIn {
    player.setVarbit(true, varbits[varbit.chatbox_unlocked])
    player.runClientScript(2498, 1, 0, 0)
    player.runClientScript(72, 26148868, 26148869, 0)
    player.runClientScript(2358, 0)
    player.runClientScript(385, 1)
    player.runClientScript(828, 1)
    player.runClientScript(876, 28404, 0, player.displayName, "REGULAR")
    player.message("Welcome to RS Mod.")
}
