package org.rsmod.game.type.varnbit

import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.MergeableCacheBuilder
import org.rsmod.game.type.varn.VarnType

@DslMarker private annotation class VarnBitBuilderDsl

@VarnBitBuilderDsl
public class VarnBitTypeBuilder(public var internal: String? = null) {
    public var baseVar: Int? = null
    public var lsb: Int? = null
    public var msb: Int? = null
    public var varn: VarnType? = null

    public fun build(id: Int): UnpackedVarnBitType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val baseVar = checkNotNull(baseVar) { "`baseVar` must be set." }
        val lsb = checkNotNull(lsb) { "`lsb` must be set." }
        val msb = checkNotNull(msb) { "`msb` must be set." }
        return UnpackedVarnBitType(
            varnId = baseVar,
            internalVarn = varn,
            internalLsb = lsb,
            internalMsb = msb,
            internalId = id,
            internalName = internal,
        )
    }

    public fun buildDefault(id: Int): UnpackedVarnBitType {
        baseVar = -1
        lsb = -1
        msb = -1
        return build(id)
    }

    public companion object : MergeableCacheBuilder<UnpackedVarnBitType> {
        override fun merge(
            edit: UnpackedVarnBitType,
            base: UnpackedVarnBitType,
        ): UnpackedVarnBitType {
            val varnId = select(edit, base, default = -1) { varnId }
            val lsb = select(edit, base, default = -1) { internalLsb }
            val msb = select(edit, base, default = -1) { internalMsb }
            val varn = select(edit, base, default = null) { internalVarn }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedVarnBitType(
                varnId = varnId,
                internalVarn = varn,
                internalLsb = lsb,
                internalMsb = msb,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
