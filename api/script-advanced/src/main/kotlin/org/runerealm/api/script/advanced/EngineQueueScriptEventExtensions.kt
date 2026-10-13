package org.runerealm.api.script.advanced

import org.runerealm.api.player.events.EngineQueueEvents
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onProtectedEvent
import org.runerealm.events.EventBus
import org.runerealm.game.queue.EngineQueueType
import org.runerealm.game.type.stat.StatType
import org.runerealm.map.square.MapSquareKey
import org.runerealm.map.zone.ZoneKey
import org.runerealm.plugin.scripts.ScriptContext

// Important Note: Only a _single_ engine-queue script may be bound per key. This applies to both
// "Default" and "Labelled" scripts. For example, if `onAdvanceStat { ... }` is already bound,
// a second such script cannot be registered. Similarly, if `onAdvanceStat(stats.attack)` is bound,
// another `onAdvanceStat(stats.attack)` cannot be added (though scripts for other stats can).

private fun <T> ScriptContext.onEngineQueue(
    type: EngineQueueType,
    action: suspend ProtectedAccess.(EngineQueueEvents.Default<T>) -> Unit,
) {
    onProtectedEvent(type.id, action)
    engineQueueCache.addDefault(type)
}

private fun ScriptContext.onEngineQueue(
    type: EngineQueueType,
    label: Int,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
) {
    onProtectedEvent(EventBus.composeLongKey(label, type.id), action)
    engineQueueCache.addLabelled(type, label)
}

public fun ScriptContext.onChangeStat(
    action: suspend ProtectedAccess.(EngineQueueEvents.Default<StatType>) -> Unit
): Unit = onEngineQueue(EngineQueueType.ChangeStat, action)

public fun ScriptContext.onChangeStat(
    stat: StatType,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.ChangeStat, stat.id, action)

public fun ScriptContext.onAdvanceStat(
    action: suspend ProtectedAccess.(EngineQueueEvents.Default<StatType>) -> Unit
): Unit = onEngineQueue(EngineQueueType.AdvanceStat, action)

public fun ScriptContext.onAdvanceStat(
    stat: StatType,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.AdvanceStat, stat.id, action)

public fun ScriptContext.onMapzone(
    square: MapSquareKey,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.Mapzone, square.id, action)

public fun ScriptContext.onMapzoneExit(
    square: MapSquareKey,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.MapzoneExit, square.id, action)

public fun ScriptContext.onZone(
    zone: ZoneKey,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.Zone, zone.packed, action)

public fun ScriptContext.onZoneExit(
    zone: ZoneKey,
    action: suspend ProtectedAccess.(EngineQueueEvents.Labelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.ZoneExit, zone.packed, action)
