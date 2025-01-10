package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.varobjbit.UnpackedVarObjBitType
import org.rsmod.game.type.varobjbit.VarObjBitTypeBuilder

@DslMarker private annotation class VarObjBitBuilderDsl

@VarObjBitBuilderDsl
public class VarObjBitPluginBuilder(public var internal: String? = null) {
    private val backing: VarObjBitTypeBuilder = VarObjBitTypeBuilder()

    public var startBit: Int? by backing::startBit
    public var endBit: Int? by backing::endBit

    public var bits: IntRange?
        get() = backing.bits()
        set(value) {
            backing.startBit = value?.first
            backing.endBit = value?.last
        }

    public fun build(id: Int): UnpackedVarObjBitType {
        backing.internal = internal
        return backing.build(id)
    }

    private fun VarObjBitTypeBuilder.bits(): IntRange? {
        val startBit = startBit
        val endBit = endBit
        return if (startBit == null || endBit == null) {
            null
        } else {
            startBit..endBit
        }
    }
}
