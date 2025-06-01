package org.rsmod.api.player

import net.rsprot.protocol.game.outgoing.sound.MidiJingle
import net.rsprot.protocol.game.outgoing.sound.MidiSongV2
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.game.entity.Player
import org.rsmod.game.type.jingle.JingleType
import org.rsmod.game.type.midi.MidiType

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
