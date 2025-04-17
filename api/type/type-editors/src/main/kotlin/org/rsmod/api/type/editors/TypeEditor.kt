package org.rsmod.api.type.editors

public abstract class TypeEditor<T> {
    internal val cache = mutableListOf<T>()
}
