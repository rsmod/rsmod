package org.rsmod.api.type.refs

public abstract class TypeReferences<T, I>(internal val type: Class<T>) {
    internal val cache = mutableListOf<T>()
}

public abstract class HashTypeReferences<T>(type: Class<T>) : TypeReferences<T, Long>(type) {
    public abstract fun find(hash: Long): T
}

public abstract class NameTypeReferences<T>(type: Class<T>) : TypeReferences<T, Long>(type) {
    public abstract fun find(internal: String): T
}
