package org.rsmod.api.type.builders

public abstract class TypeBuilder<B, T> {
    internal val cache = mutableListOf<T>()
}

public abstract class HashTypeBuilder<B, T> : TypeBuilder<B, T>() {
    public abstract fun build(internal: String, init: B.() -> Unit)
}

public abstract class NameTypeBuilder<B, T> : TypeBuilder<B, T>() {
    public abstract fun build(internal: String, init: B.() -> Unit): T
}
