package org.runerealm.api.script.advanced

import org.runerealm.api.player.events.interact.HeldBanksideEvents
import org.runerealm.api.player.events.interact.HeldDropEvents
import org.runerealm.api.script.onEvent
import org.runerealm.game.type.obj.ObjType
import org.runerealm.plugin.scripts.ScriptContext

public fun ScriptContext.onDropHeld(action: HeldDropEvents.Drop.() -> Unit): Unit = onEvent(action)

public fun ScriptContext.onDestroyHeld(action: HeldDropEvents.Destroy.() -> Unit): Unit =
    onEvent(action)

public fun ScriptContext.onReleaseHeld(action: HeldDropEvents.Release.() -> Unit): Unit =
    onEvent(action)

/** Used for inv objs that are given a special `Op9` while in the bankside inv interface. */
public fun ScriptContext.onOpBankside(
    type: ObjType,
    action: HeldBanksideEvents.Type.() -> Unit,
): Unit = onEvent(type.id, action)
