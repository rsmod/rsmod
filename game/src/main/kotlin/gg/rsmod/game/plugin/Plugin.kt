package gg.rsmod.game.plugin

import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import kotlin.properties.ObservableProperty

open class Plugin(val injector: Injector) {

    inline fun <reified T> inject(): ObservableProperty<T> =
        InjectedProperty(injector.getInstance())

    class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)
}
