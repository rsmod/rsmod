package org.rsmod.api.script

import org.rsmod.api.player.events.SessionStateEvent
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onPlayerInit(action: SessionStateEvent.Initialize.() -> Unit): Unit =
    onEvent<SessionStateEvent.Initialize>(action)

public fun ScriptContext.onPlayerLogIn(action: SessionStateEvent.LogIn.() -> Unit): Unit =
    onEvent<SessionStateEvent.LogIn>(action)
