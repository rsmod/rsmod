package org.rsmod.game.type.midi

@DslMarker private annotation class MidiBuilderDsl

@MidiBuilderDsl
public class MidiTypeBuilder(public var internalName: String? = null) {
    public fun build(id: Int): MidiType {
        val internalName = checkNotNull(internalName) { "`internalName` must be set." }
        return MidiType(internalId = id, internalName = internalName)
    }
}
