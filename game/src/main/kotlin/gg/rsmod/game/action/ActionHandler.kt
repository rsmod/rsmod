package gg.rsmod.game.action

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.mob.Player
import kotlin.reflect.KClass

data class ActionMessage(
    val action: Action,
    val handler: ActionHandler<Action>
)

interface ActionHandler<T : Action> {
    fun handle(client: Client, player: Player, action: T)
}

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
        logger.debug {
            "Register action handler (action=${T::class.simpleName}, handler=${handler.javaClass.simpleName})"
        }
        handlers[T::class] = handler
    }

    @Suppress("UNCHECKED_CAST")
    inline operator fun <reified T : Action> get(action: T) = handlers[action::class] as? ActionHandler<T>

    companion object {
        val logger = InlineLogger()
    }
}

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class ActionHandlerBuilder<T : Action> {
    var handler: ActionHandler<T>? = null
}
