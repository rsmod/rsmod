package org.runerealm.api.script.advanced

import org.runerealm.api.player.events.interact.HeldEquipEvents
import org.runerealm.api.script.onEvent
import org.runerealm.plugin.scripts.ScriptContext

public fun ScriptContext.onWearposChange(action: HeldEquipEvents.WearposChange.() -> Unit): Unit =
    onEvent(action)
