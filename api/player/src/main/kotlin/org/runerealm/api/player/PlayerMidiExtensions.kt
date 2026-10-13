package org.runerealm.api.player

import net.rsprot.protocol.game.outgoing.sound.MidiJingle
import net.rsprot.protocol.game.outgoing.sound.MidiSongV2
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.game.entity.Player
import org.runerealm.game.type.jingle.JingleType
import org.runerealm.game.type.midi.MidiType

internal var Player.musicClocks by intVarBit(varbits.music_curr_clocks)

/** @see [MidiJingle] */
public fun Player.midiJingle(jingle: JingleType) {
    musicClocks = 0 // Client restarts music when a jingle is played.
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
