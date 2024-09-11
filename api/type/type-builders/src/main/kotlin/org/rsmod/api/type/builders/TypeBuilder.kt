package org.rsmod.api.type.builders

public abstract class TypeBuilder<B, T> {
    @PublishedApi internal val cache: MutableList<T> = mutableListOf<T>()
}

public abstract class HashTypeBuilder<B, T> : TypeBuilder<B, T>() {
    public abstract fun build(internal: String, init: B.() -> Unit)
}

public abstract class NameTypeBuilder<B, T> : TypeBuilder<B, T>() {
    public abstract fun build(internal: String, init: B.() -> Unit): T
}
