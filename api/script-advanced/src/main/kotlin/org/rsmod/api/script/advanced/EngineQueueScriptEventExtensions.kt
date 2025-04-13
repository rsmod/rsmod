package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.PlayerQueueEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onProtectedEvent
import org.rsmod.game.queue.EngineQueueType
import org.rsmod.game.type.stat.StatType
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneKey
import org.rsmod.plugin.scripts.ScriptContext

private fun ScriptContext.onEngineQueue(
    type: EngineQueueType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineDefault) -> Unit,
): Unit = onProtectedEvent(type.id, action)

private fun ScriptContext.onEngineQueue(
    type: EngineQueueType,
    label: Int,
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineLabelled) -> Unit,
): Unit = onProtectedEvent((label shl 32) or type.id, action)

public fun ScriptContext.onChangeStat(
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineDefault) -> Unit
): Unit = onEngineQueue(EngineQueueType.ChangeStat, action)

public fun ScriptContext.onChangeStat(
    stat: StatType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineLabelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.ChangeStat, stat.id, action)

public fun ScriptContext.onAdvanceStat(
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineDefault) -> Unit
): Unit = onEngineQueue(EngineQueueType.AdvanceStat, action)

public fun ScriptContext.onAdvanceStat(
    stat: StatType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineLabelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.AdvanceStat, stat.id, action)

public fun ScriptContext.onMapzone(
    square: MapSquareKey,
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineLabelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.Mapzone, square.id, action)

public fun ScriptContext.onMapzoneExit(
    square: MapSquareKey,
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineLabelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.MapzoneExit, square.id, action)

public fun ScriptContext.onZone(
    zone: ZoneKey,
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineLabelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.Zone, zone.packed, action)

public fun ScriptContext.onZoneExit(
    zone: ZoneKey,
    action: suspend ProtectedAccess.(PlayerQueueEvents.EngineLabelled) -> Unit,
): Unit = onEngineQueue(EngineQueueType.ZoneExit, zone.packed, action)
