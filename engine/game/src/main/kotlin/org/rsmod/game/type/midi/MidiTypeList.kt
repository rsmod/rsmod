package org.rsmod.game.type.midi

public class MidiTypeList(public val types: Map<Int, MidiType>) : Map<Int, MidiType> by types {
    // TODO(engine): Load actual midi types and remove this function.
    public fun getValue(type: Int): MidiType {
        return types[type] ?: MidiType(type, "unnamed_midi_$type")
    }
}
