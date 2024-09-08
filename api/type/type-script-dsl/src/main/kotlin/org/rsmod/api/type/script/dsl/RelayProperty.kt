package org.rsmod.api.type.script.dsl

import kotlin.reflect.KProperty

internal class RelayProperty<T>(private val setter: (T) -> Unit) {
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        setter(value)
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): Nothing =
        throw UnsupportedOperationException("Cannot access value for relay properties.")
}

internal fun <T> relay(setter: (T) -> Unit): RelayProperty<T> = RelayProperty(setter)
