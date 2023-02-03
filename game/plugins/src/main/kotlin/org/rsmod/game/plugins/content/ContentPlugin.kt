package org.rsmod.game.plugins.content

import com.google.inject.Injector
import kotlin.properties.ObservableProperty

public open class ContentPlugin(public val injector: Injector) {

    public inline fun <reified T> inject(): InjectedProperty<T> {
        return InjectedProperty(injector.getInstance(T::class.java))
    }

    public companion object {

        public class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)
    }
}
