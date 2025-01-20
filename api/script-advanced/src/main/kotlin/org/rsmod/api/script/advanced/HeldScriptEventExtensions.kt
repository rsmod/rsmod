package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.interact.HeldDropEvents
import org.rsmod.api.script.onEvent
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onDropHeld(action: HeldDropEvents.Drop.() -> Unit): Unit = onEvent(action)

public fun ScriptContext.onDestroyHeld(action: HeldDropEvents.Destroy.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onReleaseHeld(action: HeldDropEvents.Release.() -> Unit): Unit =
    onEvent(action)
