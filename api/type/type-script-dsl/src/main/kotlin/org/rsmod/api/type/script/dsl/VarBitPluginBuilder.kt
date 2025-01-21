package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.varbit.UnpackedVarBitType
import org.rsmod.game.type.varbit.VarBitTypeBuilder
import org.rsmod.game.type.varp.VarpType

@DslMarker private annotation class VarBitBuilderDsl

@VarBitBuilderDsl
public class VarBitPluginBuilder(public var internal: String? = null) {
    private val backing = VarBitTypeBuilder()

    public var baseVar: VarpType? by backing::varp
    public var startBit: Int? by backing::lsb
    public var endBit: Int? by backing::msb

    public var bits: IntRange?
        get() = backing.bits()
        set(value) {
            backing.lsb = value?.first
            backing.msb = value?.last
        }

    public fun build(id: Int): UnpackedVarBitType {
        backing.internal = internal
        backing.baseVar = baseVar?.id
        return backing.build(id)
    }

    private fun VarBitTypeBuilder.bits(): IntRange? {
        val startBit = lsb
        val endBit = msb
        return if (startBit == null || endBit == null) {
            null
        } else {
            startBit..endBit
        }
    }
}
