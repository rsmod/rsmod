package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.varp.UnpackedVarpType
import org.rsmod.game.type.varp.VarpTypeBuilder

@DslMarker private annotation class VarpBuilderDsl

@VarpBuilderDsl
public class VarpPluginBuilder(public var internal: String? = null) {
    private val backing = VarpTypeBuilder()

    // Varps created through this builder are usually server-side only.
    public var transmit: Boolean = false
    public var clientCode: Int? by backing::clientCode
    public var protect: Boolean? by backing::protect

    public fun build(id: Int): UnpackedVarpType {
        backing.internal = internal
        backing.transmit = transmit
        return backing.build(id)
    }
}
