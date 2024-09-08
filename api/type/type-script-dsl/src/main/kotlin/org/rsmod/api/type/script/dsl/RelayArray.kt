package org.rsmod.api.type.script.dsl

public class RelayArray<T>(private val setter: (index: Int, T) -> Unit) {
    public operator fun set(index: Int, value: T) {
        setter(index, value)
    }
}

internal fun <T> relayIndexed(setter: (index: Int, T) -> Unit): RelayArray<T> = RelayArray(setter)
