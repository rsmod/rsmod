package org.rsmod.game.type.varobjbit

@DslMarker private annotation class VarObjBitBuilderDsl

@VarObjBitBuilderDsl
public class VarObjBitTypeBuilder(public var internal: String? = null) {
    public var startBit: Int? = null
    public var endBit: Int? = null

    public fun build(id: Int): UnpackedVarObjBitType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val startBit = checkNotNull(startBit) { "`startBit` must be set." }
        val endBit = checkNotNull(endBit) { "`endBit` must be set." }
        check(startBit >= 0) { "`startBit` must be positive." }
        check(endBit >= 0) { "`endBit` must be positive." }
        check(endBit >= startBit) { "`endBit` must be greater or equal to `startBit`." }
        check(endBit < Int.SIZE_BITS) { "`endBit` cannot be greater or equal to 32." }
        return UnpackedVarObjBitType(
            startBit = startBit,
            endBit = endBit,
            internalId = id,
            internalName = internal,
        )
    }
}
