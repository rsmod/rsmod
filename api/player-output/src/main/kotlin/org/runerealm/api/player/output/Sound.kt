package org.runerealm.api.player.output

import net.rsprot.protocol.game.outgoing.sound.SynthSound
import org.runerealm.game.entity.Player
import org.runerealm.game.type.synth.SynthType

/** @see [SynthSound] */
public fun Player.soundSynth(synth: SynthType, loops: Int = 1, delay: Int = 0) {
    client.write(SynthSound(synth.id, loops, delay))
}
