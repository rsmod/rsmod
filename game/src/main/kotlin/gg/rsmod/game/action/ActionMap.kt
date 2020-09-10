package gg.rsmod.game.action

import com.google.inject.Inject
import kotlin.reflect.KClass

class ActionHandlerMap(
    val handlers: MutableMap<KClass<out Action>, ActionHandler<*>>
) : Map<KClass<out Action>, ActionHandler<*>> by handlers {

    @Inject
    constructor() : this(mutableMapOf())

    inline fun <reified T : Action> register(init: ActionHandlerBuilder<T>.() -> Unit) {
        if (handlers.containsKey(T::class)) {
            error("Action already has a handler (action=${T::class.simpleName}).")
        }
        val builder = ActionHandlerBuilder<T>().apply(init)
        val handler = builder.handler ?: error("Action handler has not been set.")
        handlers[T::class] = handler
    }

    inline operator fun <reified T : Action> get(action: T) = handlers[action::class]
}

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class ActionHandlerBuilder<T : Action> {
    var handler: ActionHandler<T>? = null
}
