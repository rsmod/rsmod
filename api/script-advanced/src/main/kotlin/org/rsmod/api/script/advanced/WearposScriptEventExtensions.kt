package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.interact.HeldEquipEvents
import org.rsmod.api.script.onEvent
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onWearposChange(action: HeldEquipEvents.WearposChange.() -> Unit): Unit =
    onEvent(action)
