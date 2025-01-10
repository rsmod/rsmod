package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.varcon.VarConType
import org.rsmod.game.type.varconbit.UnpackedVarConBitType
import org.rsmod.game.type.varconbit.VarConBitTypeBuilder

@DslMarker private annotation class VarConBitBuilderDsl

@VarConBitBuilderDsl
public class VarConBitPluginBuilder(public var internal: String? = null) {
    private val backing = VarConBitTypeBuilder()

    public var baseVar: VarConType? by backing::varcon
    public var startBit: Int? by backing::lsb
    public var endBit: Int? by backing::msb

    public var bits: IntRange?
        get() = backing.bits()
        set(value) {
            backing.lsb = value?.first
            backing.msb = value?.last
        }

    public fun build(id: Int): UnpackedVarConBitType {
        backing.internal = internal
        return backing.build(id)
    }

    private fun VarConBitTypeBuilder.bits(): IntRange? {
        val startBit = lsb
        val endBit = msb
        return if (startBit == null || endBit == null) {
            null
        } else {
            startBit..endBit
        }
    }
}
