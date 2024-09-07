@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.synth.SynthReferences
import org.rsmod.game.type.synth.SynthType

public typealias synths = BaseSynths

public object BaseSynths : SynthReferences() {
    public val door_close: SynthType = find("door_close")
    public val door_open: SynthType = find("door_open")
    public val nicedoor_close: SynthType = find("nicedoor_close")
    public val nicedoor_open: SynthType = find("nicedoor_open")
    public val pick2: SynthType = find("pick2")
}
