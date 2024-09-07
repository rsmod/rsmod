package org.rsmod.api.type.editors

public abstract class TypeEditor<B, T> {
    internal val cache = mutableListOf<T>()

    public abstract fun edit(internal: String, init: B.() -> Unit)
}
