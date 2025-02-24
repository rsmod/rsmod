package org.rsmod.game.type.varp

import org.rsmod.game.type.util.GenericPropertySelector.select

@DslMarker private annotation class VarpBuilderDsl

@VarpBuilderDsl
public class VarpTypeBuilder(public var internal: String? = null) {
    public var clientCode: Int? = null
    public var scope: VarpLifetime? = null
    public var transmit: VarpTransmitLevel? = null
    /** If set to `true` this varp will check all associated varbits to detect bit collisions. */
    public var bitProtect: Boolean? = null

    public fun build(id: Int): UnpackedVarpType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val clientCode = clientCode ?: DEFAULT_CLIENT_CODE
        val scope = scope ?: DEFAULT_SCOPE
        val transmit = transmit ?: DEFAULT_TRANSMIT
        val bitProtect = bitProtect ?: DEFAULT_BIT_PROTECT
        return UnpackedVarpType(
            bitProtect = bitProtect,
            clientCode = clientCode,
            internalScope = scope,
            internalTransmit = transmit,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object {
        public const val DEFAULT_CLIENT_CODE: Int = -1
        public const val DEFAULT_BIT_PROTECT: Boolean = false

        public val DEFAULT_SCOPE: VarpLifetime = VarpLifetime.Perm
        public val DEFAULT_TRANSMIT: VarpTransmitLevel = VarpTransmitLevel.OnSetAlways

        public fun merge(edit: UnpackedVarpType, base: UnpackedVarpType): UnpackedVarpType {
            val clientCode = select(edit, base, DEFAULT_CLIENT_CODE) { clientCode }
            val scope = select(edit, base, DEFAULT_SCOPE) { scope }
            val transmit = select(edit, base, null) { transmit }
            val bitProtect = select(edit, base, DEFAULT_BIT_PROTECT) { bitProtect }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedVarpType(
                bitProtect = bitProtect,
                clientCode = clientCode,
                internalScope = scope,
                internalTransmit = transmit,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
