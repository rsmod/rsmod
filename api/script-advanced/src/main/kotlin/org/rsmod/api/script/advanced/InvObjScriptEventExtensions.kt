package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.interact.InvObjDropEvents
import org.rsmod.api.script.onEvent
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onDropObj(action: InvObjDropEvents.Drop.() -> Unit): Unit = onEvent(action)

public fun ScriptContext.onDestroyObj(action: InvObjDropEvents.Destroy.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onReleaseObj(action: InvObjDropEvents.Release.() -> Unit): Unit =
    onEvent(action)
