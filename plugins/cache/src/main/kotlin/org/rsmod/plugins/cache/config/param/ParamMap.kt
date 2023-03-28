package org.rsmod.plugins.cache.config.param

@JvmInline
public value class ParamMap(private val params: Map<Int, Any>) : Map<Int, Any> by params {

    @Suppress("UNCHECKED_CAST")
    public fun <T> get(type: ParamType<T>): T? {
        val cacheType = type.type
        val element = (params[type.id] ?: type.default)
        if (element != null && cacheType != null && cacheType.out != element.javaClass) {
            error("Unexpected element type. (received=${element.javaClass}, expected=${cacheType.out})")
        }
        return element as? T
    }
}
