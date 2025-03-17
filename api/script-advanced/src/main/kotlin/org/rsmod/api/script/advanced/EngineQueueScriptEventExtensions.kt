package org.rsmod.api.script.advanced

import org.rsmod.api.player.events.PlayerQueueEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onProtectedEvent
import org.rsmod.game.queue.EngineQueueType
import org.rsmod.plugin.scripts.ScriptContext

// Currently, each engine queue type can only be bound **once**. If a need arises, we may
// reconsider this limitation, but supporting multiple bindings would require a new event
// bus implementation that can handle suspend `UnboundEvent`s.
public fun ScriptContext.onEngineQueue(
    type: EngineQueueType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.Engine<Nothing>) -> Unit,
): Unit = onProtectedEvent(type.id, action)

public fun <T> ScriptContext.onEngineQueueWithArgs(
    type: EngineQueueType,
    action: suspend ProtectedAccess.(PlayerQueueEvents.Engine<T>) -> Unit,
): Unit = onProtectedEvent(type.id, action)
