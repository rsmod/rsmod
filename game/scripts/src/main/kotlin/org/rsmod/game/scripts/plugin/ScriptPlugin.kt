package org.rsmod.game.scripts.plugin

import com.google.inject.Injector
import kotlin.properties.ObservableProperty

public open class ScriptPlugin(public val injector: Injector) {

    public inline fun <reified T> inject(): InjectedProperty<T> {
        return InjectedProperty(injector.getInstance(T::class.java))
    }

    public companion object {

        public class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)
    }
}
