package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.interact.InvEquipEvents
import org.rsmod.api.script.onEvent
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onWearposChange(action: InvEquipEvents.WearposChange.() -> Unit): Unit =
    onEvent(action)
