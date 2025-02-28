package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.varn.VarnType
import org.rsmod.game.type.varnbit.UnpackedVarnBitType
import org.rsmod.game.type.varnbit.VarnBitTypeBuilder

@DslMarker private annotation class VarnBitBuilderDsl

@VarnBitBuilderDsl
public class VarnBitPluginBuilder(public var internal: String? = null) {
    private val backing = VarnBitTypeBuilder()

    public var baseVar: VarnType? by backing::varn
    public var startBit: Int? by backing::lsb
    public var endBit: Int? by backing::msb

    public var bits: IntRange?
        get() = backing.bits()
        set(value) {
            backing.lsb = value?.first
            backing.msb = value?.last
        }

    public fun build(id: Int): UnpackedVarnBitType {
        backing.internal = internal
        backing.baseVar = baseVar?.id
        return backing.build(id)
    }

    private fun VarnBitTypeBuilder.bits(): IntRange? {
        val startBit = lsb
        val endBit = msb
        return if (startBit == null || endBit == null) {
            null
        } else {
            startBit..endBit
        }
    }
}
