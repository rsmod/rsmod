package org.rsmod.api.script

import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.events.UnboundEvent
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onBootUp(action: GameLifecycle.BootUp.() -> Unit): Unit =
    eventBus.subscribe<GameLifecycle.BootUp>(action)

public inline fun <reified T : UnboundEvent> ScriptContext.onEvent(
    noinline action: T.() -> Unit
): Unit = eventBus.subscribe<T>(action)

public inline fun <reified T : KeyedEvent> ScriptContext.onEvent(
    id: Number,
    noinline action: T.() -> Unit,
): Unit = eventBus.subscribe<T>(id, action)

public inline fun <reified T : SuspendEvent<ProtectedAccess>> ScriptContext.onProtectedEvent(
    id: Number,
    noinline action: suspend ProtectedAccess.(T) -> Unit,
): Unit = eventBus.subscribe(T::class.java, id, action)
