package org.runerealm.api.script

import org.runerealm.api.player.events.EngineQueueEvents
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.EventBus
import org.runerealm.game.queue.EngineQueueType
import org.runerealm.game.type.area.AreaType
import org.runerealm.plugin.scripts.ScriptContext

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
