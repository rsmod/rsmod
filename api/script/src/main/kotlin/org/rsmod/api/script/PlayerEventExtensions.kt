package org.rsmod.api.script

import org.rsmod.api.player.events.PlayerTimerEvent
import org.rsmod.api.player.events.SessionStateEvent
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.timer.TimerType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onPlayerInit(action: SessionStateEvent.Initialize.() -> Unit): Unit =
    onEvent<SessionStateEvent.Initialize>(action)

public fun ScriptContext.onPlayerLogIn(action: SessionStateEvent.LogIn.() -> Unit): Unit =
    onEvent<SessionStateEvent.LogIn>(action)

public fun ScriptContext.onPlayerTimer(
    timer: TimerType,
    action: suspend ProtectedAccess.(PlayerTimerEvent.Normal) -> Unit,
): Unit = onProtectedEvent(timer.id, action)

public fun ScriptContext.onPlayerSoftTimer(
    timer: TimerType,
    action: PlayerTimerEvent.Soft.() -> Unit,
): Unit = onEvent(timer.id, action)
