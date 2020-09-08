package gg.rsmod.game.plugin

import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import gg.rsmod.game.event.Event
import gg.rsmod.game.event.EventBus
import kotlin.properties.ObservableProperty

open class Plugin(
    val injector: Injector,
    val eventBus: EventBus
) {

    inline fun <reified T : Event> onEvent() = eventBus.subscribe<T>()

    inline fun <reified T> inject(): ObservableProperty<T> =
        InjectedProperty(injector.getInstance())

    class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)
}
