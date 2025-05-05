package org.rsmod.api.script

import org.rsmod.api.player.events.EngineQueueEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.EventBus
import org.rsmod.game.queue.EngineQueueType
import org.rsmod.game.type.area.AreaType
import org.rsmod.plugin.scripts.ScriptContext

private fun ScriptContext.onEngineQueue(
    type: EngineQueueType,
    label: Int,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
) {
    onProtectedEvent(EventBus.composeLongKey(label, type.id), action)
    engineQueueCache.addLabelled(type, label)
}

public fun ScriptContext.onArea(
    area: AreaType,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.Area, area.id, action)

public fun ScriptContext.onAreaExit(
    area: AreaType,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.AreaExit, area.id, action)
