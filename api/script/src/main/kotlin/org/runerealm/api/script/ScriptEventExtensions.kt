package org.runerealm.api.script

import org.runerealm.api.cheat.CheatHandlerBuilder
import org.runerealm.api.cheat.register
import org.runerealm.api.controller.access.StandardConAccess
import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.KeyedEvent
import org.runerealm.events.SuspendEvent
import org.runerealm.events.UnboundEvent
import org.runerealm.plugin.scripts.ScriptContext

public fun ScriptContext.onCommand(command: String, build: CheatHandlerBuilder.() -> Unit): Unit =
    cheatCommandMap.register(command, build)

public inline fun <reified T : UnboundEvent> ScriptContext.onEvent(
    noinline action: T.() -> Unit
): Unit = eventBus.subscribeUnbound(T::class.java, action)

public inline fun <reified T : KeyedEvent> ScriptContext.onEvent(
    id: Long,
    noinline action: T.() -> Unit,
): Unit = eventBus.subscribeKeyed(T::class.java, id, action)

public inline fun <reified T : KeyedEvent> ScriptContext.onEvent(
    id: Int,
    noinline action: T.() -> Unit,
): Unit = onEvent(id.toLong(), action)

public inline fun <reified T : SuspendEvent<ProtectedAccess>> ScriptContext.onProtectedEvent(
    id: Long,
    noinline action: suspend ProtectedAccess.(T) -> Unit,
): Unit = eventBus.subscribeSuspend(T::class.java, id, action)

public inline fun <reified T : SuspendEvent<ProtectedAccess>> ScriptContext.onProtectedEvent(
    id: Int,
    noinline action: suspend ProtectedAccess.(T) -> Unit,
): Unit = onProtectedEvent(id.toLong(), action)

public inline fun <reified T : SuspendEvent<StandardNpcAccess>> ScriptContext.onNpcAccessEvent(
    id: Long,
    noinline action: suspend StandardNpcAccess.(T) -> Unit,
): Unit = eventBus.subscribeSuspend(T::class.java, id, action)

public inline fun <reified T : SuspendEvent<StandardNpcAccess>> ScriptContext.onNpcAccessEvent(
    id: Int,
    noinline action: suspend StandardNpcAccess.(T) -> Unit,
): Unit = onNpcAccessEvent(id.toLong(), action)

public inline fun <reified T : SuspendEvent<StandardConAccess>> ScriptContext.onConAccessEvent(
    id: Long,
    noinline action: suspend StandardConAccess.(T) -> Unit,
): Unit = eventBus.subscribeSuspend(T::class.java, id, action)

public inline fun <reified T : SuspendEvent<StandardConAccess>> ScriptContext.onConAccessEvent(
    id: Int,
    noinline action: suspend StandardConAccess.(T) -> Unit,
): Unit = onConAccessEvent(id.toLong(), action)
