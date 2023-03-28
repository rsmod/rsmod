package org.rsmod.plugins.cache.config.varp

private const val DEFAULT_ID = -1
private const val DEFAULT_TRANSMIT_FLAG = false
private const val DEFAULT_PERSIST_FLAG = true

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
public class VarpTypeBuilder(
    public var id: Int = DEFAULT_ID,
    public var name: String? = null,
    public var clientCode: Int? = null,
    public var transmit: Boolean = DEFAULT_TRANSMIT_FLAG,
    public var persist: Boolean? = null
) {

    public fun build(): VarpType {
        check(id != DEFAULT_ID)
        return VarpType(id, name, clientCode, transmit, persist ?: DEFAULT_PERSIST_FLAG)
    }

    public operator fun plusAssign(other: VarpType) {
        if (id == DEFAULT_ID) id = other.id
        if (name == null) name = other.name
        if (clientCode == null) clientCode = other.clientCode
        if (transmit == DEFAULT_TRANSMIT_FLAG) transmit = other.transmit
        if (persist == null) persist = other.persist
    }
}
