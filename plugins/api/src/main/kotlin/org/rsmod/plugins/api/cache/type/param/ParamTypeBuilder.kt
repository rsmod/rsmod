package org.rsmod.plugins.api.cache.type.param

import org.rsmod.plugins.api.cache.type.literal.CacheTypeIdentifier

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
public class ParamTypeBuilder {

    public var id: Int? = null
    public var name: String? = null
    public var transmit: Boolean = DEFAULT_TRANSMIT_FLAG
    public var type: Char? = null
    public var autoDisable: Boolean = DEFAULT_AUTO_DISABLE
    public var default: Any? = null

    public fun build(): ParamType {
        val id = id ?: error("`id` must be set.")
        val type = CacheTypeIdentifier.mapped[type]
        return ParamType(
            id = id,
            name = name,
            transmit = transmit,
            type = type,
            autoDisable = autoDisable,
            default = default
        )
    }

    private companion object {

        private const val DEFAULT_TRANSMIT_FLAG: Boolean = false
        private const val DEFAULT_AUTO_DISABLE: Boolean = true
    }
}
