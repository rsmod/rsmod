package org.rsmod.game.type.varbit

import org.rsmod.game.type.varp.VarpType

@DslMarker private annotation class VarBitBuilderDsl

@VarBitBuilderDsl
public class VarBitTypeBuilder(public var internal: String? = null) {
    public var baseVar: Int? = null
    public var lsb: Int? = null
    public var msb: Int? = null
    public var varp: VarpType? = null

    public fun build(id: Int): UnpackedVarBitType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val baseVar = checkNotNull(baseVar) { "`baseVar` must be set." }
        val lsb = checkNotNull(lsb) { "`lsb` must be set." }
        val msb = checkNotNull(msb) { "`msb` must be set." }
        return UnpackedVarBitType(
            varpId = baseVar,
            lsb = lsb,
            msb = msb,
            varp = varp,
            internalId = id,
            internalName = internal,
        )
    }
}
