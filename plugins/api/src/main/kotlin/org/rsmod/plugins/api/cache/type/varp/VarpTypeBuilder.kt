package org.rsmod.plugins.api.cache.type.varp

private const val DEFAULT_ID = -1
private const val DEFAULT_CLIENT_CODE = 0

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
public class VarpTypeBuilder(
    public var id: Int = DEFAULT_ID,
    public var clientCode: Int = DEFAULT_CLIENT_CODE
) {

    public fun build(): VarpType {
        check(id != DEFAULT_ID)
        return VarpType(id, clientCode)
    }

    public operator fun plusAssign(other: VarpType) {
        if (id == DEFAULT_ID) id = other.id
        if (clientCode == DEFAULT_CLIENT_CODE) clientCode = other.clientCode
    }
}
