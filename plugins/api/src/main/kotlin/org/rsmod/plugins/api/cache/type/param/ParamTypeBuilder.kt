package org.rsmod.plugins.api.cache.type.param

import org.rsmod.plugins.api.cache.type.literal.CacheTypeIdentifier

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
public class ParamTypeBuilder {

    public var id: Int? = null
    public var name: String? = null
    public var transmit: Boolean = DEFAULT_TRANSMIT_FLAG
    public var typeChar: Char? = null
    public var autoDisable: Boolean = DEFAULT_AUTO_DISABLE
    public var defaultStr: String? = null
    public var defaultInt: Int? = null

    public fun build(): ParamType {
        if (defaultInt != null && defaultStr != null) {
            error("Cannot set both default int and default string values.")
        }
        val id = id ?: error("`id` must be set.")
        val type = CacheTypeIdentifier.mapped[typeChar]
        val default = if (type == null) {
            (defaultInt ?: defaultStr)
        } else {
            defaultInt?.convert(type) ?: defaultStr?.convert(type)
        }
        return ParamType(
            id = id,
            name = name,
            transmit = transmit,
            type = type,
            autoDisable = autoDisable,
            default = default
        )
    }

    private fun Int.convert(id: CacheTypeIdentifier): Any? {
        if (id.isInt) return id.decodeInt(this)
        error("Cache identifier mismatch for `$id` with value `$this`.")
    }

    private fun String.convert(id: CacheTypeIdentifier): Any? {
        if (id.isString) return id.decodeString(this)
        error("Cache identifier mismatch for `$id` with value `$this`.")
    }

    public operator fun plusAssign(other: ParamType) {
        if (id == null) id = other.id
        if (name == null) name = other.name
        if (transmit == DEFAULT_TRANSMIT_FLAG) transmit = other.transmit
        if (typeChar == null) typeChar = other.type?.char
        if (autoDisable == DEFAULT_AUTO_DISABLE) autoDisable = other.autoDisable
    }

    private companion object {

        private const val DEFAULT_TRANSMIT_FLAG: Boolean = false
        private const val DEFAULT_AUTO_DISABLE: Boolean = true
    }
}
