package org.rsmod.game.type.synth

@DslMarker private annotation class SynthBuilderDsl

@SynthBuilderDsl
public class SynthTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): SynthType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return SynthType(internalId = id, internalName = internalName)
    }
}
