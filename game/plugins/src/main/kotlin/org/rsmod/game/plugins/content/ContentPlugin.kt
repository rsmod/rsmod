package org.rsmod.game.plugins.content

import com.google.inject.Injector
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public open class ContentPlugin(public val injector: Injector) {

    public inline fun <reified T> inject(): InjectedProperty<T> {
        return InjectedProperty(injector.getInstance(T::class.java))
    }

    public companion object {

        public class InjectedProperty<T>(private val value: T) : ReadOnlyProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                return value
            }
        }
    }
}
