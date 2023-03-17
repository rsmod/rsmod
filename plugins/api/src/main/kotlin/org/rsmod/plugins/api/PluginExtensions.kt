package org.rsmod.plugins.api

import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.subscribe
import org.rsmod.game.scripts.plugin.ScriptPlugin
import org.rsmod.plugins.api.model.event.PlayerSession

private val ScriptPlugin.eventBus get() = injector.getInstance(GameEventBus::class.java)

public fun ScriptPlugin.onLogIn(action: PlayerSession.LogIn.() -> Unit): Unit =
    eventBus.subscribe(action)

public fun ScriptPlugin.onLogOut(action: PlayerSession.LogOut.() -> Unit): Unit =
    eventBus.subscribe(action)
