package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.interact.HeldDropEvents
import org.rsmod.api.script.onEvent
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onDropObj(action: HeldDropEvents.Drop.() -> Unit): Unit = onEvent(action)

public fun ScriptContext.onDestroyObj(action: HeldDropEvents.Destroy.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onReleaseObj(action: HeldDropEvents.Release.() -> Unit): Unit =
    onEvent(action)
