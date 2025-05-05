package org.rsmod.api.type.builders.map

public object MapTypeCollector {
    public fun <T> loadAndCollect(builder: MapTypeBuilder<T>): Collection<T> {
        builder.onPackMapTask()
        return collect(builder)
    }

    public fun <T> collect(builder: MapTypeBuilder<T>): Collection<T> = builder.cache
}
