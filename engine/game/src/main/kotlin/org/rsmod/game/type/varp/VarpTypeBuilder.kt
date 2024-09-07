package org.rsmod.game.type.varp

@DslMarker private annotation class VarpBuilderDsl

@VarpBuilderDsl
public class VarpTypeBuilder(public var internal: String? = null) {
    public var clientCode: Int? = null
    public var transmit: Boolean? = null
    public var protect: Boolean? = null

    public fun build(id: Int): UnpackedVarpType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val clientCode = clientCode ?: DEFAULT_CLIENT_CODE
        val transmit = transmit ?: DEFAULT_TRANSMIT
        val protect = protect ?: DEFAULT_PROTECT
        return UnpackedVarpType(
            clientCode = clientCode,
            transmit = transmit,
            protect = protect,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object {
        public const val DEFAULT_CLIENT_CODE: Int = -1
        public const val DEFAULT_PROTECT: Boolean = false // Don't really want this to default true.
        public const val DEFAULT_TRANSMIT: Boolean = true
    }
}
