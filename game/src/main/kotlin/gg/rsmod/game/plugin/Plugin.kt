package gg.rsmod.game.plugin

import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import gg.rsmod.game.action.Action
import gg.rsmod.game.action.ActionBus
import gg.rsmod.game.action.ActionExecutor
import gg.rsmod.game.event.Event
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.cmd.CommandMap
import kotlin.properties.ObservableProperty

open class Plugin(
    val injector: Injector,
    val eventBus: EventBus,
    val actions: ActionBus,
    val commands: CommandMap
) {

    inline fun <reified T : Event> onEvent() = eventBus.subscribe<T>()

    inline fun <reified T : Action> onAction(id: Int, noinline executor: ActionExecutor<T>) =
        onAction(id.toLong(), executor)

    inline fun <reified T : Action> onAction(id: Long, noinline executor: ActionExecutor<T>) {
        val registered = actions.register(id, executor)
        if (!registered) {
            error("Action with id has already been set (id=$id, type=${T::class.simpleName})")
        }
    }

    inline fun <reified T : Action> onAction(noinline executor: ActionExecutor<T>) {
        val registered = actions.register(executor)
        if (!registered) {
            error("Action type has already been registered (type=${T::class.simpleName})")
        }
    }

    inline fun <reified T> inject(): ObservableProperty<T> =
        InjectedProperty(injector.getInstance())

    class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)
}
