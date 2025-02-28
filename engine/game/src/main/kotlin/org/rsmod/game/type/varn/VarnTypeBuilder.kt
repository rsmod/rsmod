package org.rsmod.game.type.varn

import org.rsmod.game.type.util.GenericPropertySelector.select

@DslMarker private annotation class VarnBuilderDsl

@VarnBuilderDsl
public class VarnTypeBuilder(public var internal: String? = null) {
    /** If set to `true` this varn will check all associated varnbits to detect bit collisions. */
    public var bitProtect: Boolean? = null

    public fun build(id: Int): UnpackedVarnType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val bitProtect = bitProtect ?: DEFAULT_BIT_PROTECT
        return UnpackedVarnType(bitProtect = bitProtect, internalId = id, internalName = internal)
    }

    public companion object {
        public const val DEFAULT_BIT_PROTECT: Boolean = true

        public fun merge(edit: UnpackedVarnType, base: UnpackedVarnType): UnpackedVarnType {
            val bitProtect = select(edit, base, DEFAULT_BIT_PROTECT) { bitProtect }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedVarnType(
                bitProtect = bitProtect,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
