package org.rsmod.plugins.playground.login

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.subscribe
import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.api.chatbox_unlocked
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.openGameframe
import org.rsmod.plugins.api.setVarbit
import org.rsmod.plugins.api.varbit
import org.rsmod.plugins.content.gameframe.GameframePlugin

private val gameframe: GameframePlugin by inject()
private val varbits: VarbitTypeList by inject()
private val events: GameEventBus by inject()

events.subscribe<PlayerSession.Initialize> {
    player.openGameframe(gameframe.fixed)
    player.setVarbit(true, varbits[varbit.chatbox_unlocked])
}
