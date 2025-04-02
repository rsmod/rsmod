package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.sound.MidiJingle
import net.rsprot.protocol.game.outgoing.sound.MidiSongV2
import net.rsprot.protocol.game.outgoing.sound.SynthSound
import org.rsmod.game.entity.Player
import org.rsmod.game.type.jingle.JingleType
import org.rsmod.game.type.midi.MidiType
import org.rsmod.game.type.synth.SynthType

/** @see [SynthSound] */
public fun Player.soundSynth(synth: SynthType, loops: Int = 1, delay: Int = 0) {
    client.write(SynthSound(synth.id, loops, delay))
}

/** @see [MidiJingle] */
public fun Player.jingle(jingle: JingleType) {
    client.write(MidiJingle(jingle.id))
}

/** @see [MidiSongV2] */
public fun Player.midiSong(
    midi: MidiType,
    fadeOutDelay: Int = 0,
    fadeOutSpeed: Int = 0,
    fadeInDelay: Int = 0,
    fadeInSpeed: Int = 0,
) {
    client.write(MidiSongV2(midi.id, fadeOutDelay, fadeOutSpeed, fadeInDelay, fadeInSpeed))
}
