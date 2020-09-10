package gg.rsmod.game.plugin

import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import gg.rsmod.game.action.Action
import gg.rsmod.game.action.ActionExecutor
import gg.rsmod.game.action.ActionMap
import gg.rsmod.game.action.ActionType
import gg.rsmod.game.event.Event
import gg.rsmod.game.event.EventBus
import kotlin.properties.ObservableProperty

open class Plugin(
    val injector: Injector,
    val eventBus: EventBus,
    private val actions: ActionMap
) {

    fun <T : Action> onAction(type: ActionType, id: Int, executor: ActionExecutor<T>) =
        onAction(type, id.toLong(), executor)

    fun <T : Action> onAction(type: ActionType, id: Long, executor: ActionExecutor<T>) {
        val registered = actions.register(type, id, executor)
        if (!registered) {
            error("Action with id has already been set (id=$id, action=${type.javaClass.simpleName})")
        }
    }

    inline fun <reified T : Event> onEvent() = eventBus.subscribe<T>()

    inline fun <reified T> inject(): ObservableProperty<T> =
        InjectedProperty(injector.getInstance())

    class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)
}
