package org.rsmod.plugins.api

import org.rsmod.game.events.EventBus
import org.rsmod.game.model.mob.Player
import org.rsmod.game.scripts.plugin.ScriptPlugin
import org.rsmod.plugins.api.lang.APIException
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.api.model.event.TypeGameEvent
import org.rsmod.plugins.api.model.event.TypePlayerEvent
import org.rsmod.plugins.api.model.event.UpstreamEvent
import org.rsmod.plugins.types.NamedComponent

private val ScriptPlugin.eventBus get() = injector.getInstance(EventBus::class.java)

public fun ScriptPlugin.onLogIn(action: Player.(event: PlayerSession.LogIn) -> Unit): Unit =
    eventBus.subscribe(action)

public fun ScriptPlugin.onLogOut(action: Player.(event: PlayerSession.LogOut) -> Unit): Unit =
    eventBus.subscribe(action)

public fun ScriptPlugin.onButton(
    component: NamedComponent,
    action: Player.(event: UpstreamEvent.IfButton) -> Unit
) {
    val id = component.id.toLong()
    if (eventBus.contains(id, UpstreamEvent.IfButton::class.java)) {
        throw APIException.KeyedEventAlreadyMapped("component=$component")
    }
    eventBus.subscribe(id, action)
}

public inline fun <reified T : TypePlayerEvent> ScriptPlugin.subscribe(
    noinline action: Player.(event: T) -> Unit
) {
    val eventBus = injector.getInstance(EventBus::class.java)
    eventBus.add(T::class.java, action)
}

public inline fun <reified T : TypeGameEvent> ScriptPlugin.subscribe(
    noinline action: T.() -> Unit
) {
    val eventBus = injector.getInstance(EventBus::class.java)
    eventBus.add(T::class.java) { action(it) }
}
