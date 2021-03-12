package org.rsmod.game.plugin

import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import org.rsmod.game.action.Action
import org.rsmod.game.action.ActionBus
import org.rsmod.game.action.ActionExecutor
import org.rsmod.game.event.Event
import org.rsmod.game.event.EventBus
import org.rsmod.game.cmd.CommandMap
import kotlin.properties.ObservableProperty

open class Plugin(
    val injector: Injector,
    val eventBus: EventBus,
    val actionBus: ActionBus,
    val commands: CommandMap
) {

    inline fun <reified T : Event> onEvent() = eventBus.subscribe<T>()

    inline fun <reified T : Action> onAction(id: Int, noinline executor: ActionExecutor<T>) =
        onAction(id.toLong(), executor)

    inline fun <reified T : Action> onAction(id: Long, noinline executor: ActionExecutor<T>) {
        val registered = actionBus.register(id, executor)
        if (!registered) {
            error("Action with id has already been set (id=$id, type=${T::class.simpleName})")
        }
    }

    inline fun <reified T : Action> onAction(noinline executor: ActionExecutor<T>) {
        actionBus.register(executor)
    }

    inline fun <reified T> inject(): ObservableProperty<T> = InjectedProperty(injector.getInstance())

    class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)
}
