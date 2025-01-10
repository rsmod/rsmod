package org.rsmod.game.type.varconbit

import org.rsmod.game.type.varcon.VarConType

@DslMarker private annotation class VarConBitBuilderDsl

@VarConBitBuilderDsl
public class VarConBitTypeBuilder(public var internal: String? = null) {
    public var baseVar: Int? = null
    public var lsb: Int? = null
    public var msb: Int? = null
    public var varcon: VarConType? = null

    public fun build(id: Int): UnpackedVarConBitType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val baseVar = checkNotNull(baseVar) { "`baseVar` must be set." }
        val lsb = checkNotNull(lsb) { "`lsb` must be set." }
        val msb = checkNotNull(msb) { "`msb` must be set." }
        return UnpackedVarConBitType(
            varconId = baseVar,
            lsb = lsb,
            msb = msb,
            varcon = varcon,
            internalId = id,
            internalName = internal,
        )
    }
}
