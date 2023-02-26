package org.rsmod.plugins.api.cache.type.param

@JvmInline
public value class ParamMap(private val params: Map<Int, Any>) : Map<Int, Any> by params {

    @Suppress("UNCHECKED_CAST")
    public fun <T> getOrDefault(out: Class<T>, type: ParamType): T? {
        val cacheType = type.type
        if (cacheType != null && cacheType.out != out) {
            error("Unexpected `out` type. (received=$out, expected=${cacheType.out})")
        }
        return (params[type.id] ?: type.default) as? T
    }
}
