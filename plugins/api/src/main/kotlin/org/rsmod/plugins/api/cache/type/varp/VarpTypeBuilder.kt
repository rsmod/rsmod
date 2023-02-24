package org.rsmod.plugins.api.cache.type.varp

private const val DEFAULT_ID = -1
private const val DEFAULT_TRANSMIT_FLAG = false

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
public class VarpTypeBuilder(
    public var id: Int = DEFAULT_ID,
    public var alias: String? = null,
    public var clientCode: Int? = null,
    public var transmit: Boolean = DEFAULT_TRANSMIT_FLAG
) {

    public fun build(): VarpType {
        check(id != DEFAULT_ID)
        return VarpType(id, alias, clientCode, transmit)
    }

    public operator fun plusAssign(other: VarpType) {
        if (id == DEFAULT_ID) id = other.id
        if (alias == null) alias = other.alias
        if (clientCode == null) clientCode = other.clientCode
        if (transmit == DEFAULT_TRANSMIT_FLAG) transmit = other.transmit
    }
}
