package org.rsmod.api.type.builders.map

public abstract class MapTypeBuilder<T> {
    @PublishedApi internal val cache: MutableList<T> = mutableListOf()

    public abstract fun onPackMapTask()
}
