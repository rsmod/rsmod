@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.synth.SynthReferences
import org.rsmod.game.type.synth.SynthType

public typealias synths = BaseSynths

public object BaseSynths : SynthReferences() {
    public val door_close: SynthType = find("door_close")
    public val door_open: SynthType = find("door_open")
    public val picketgate_close: SynthType = find("picketgate_close")
    public val picketgate_open: SynthType = find("picketgate_open")
    public val nicedoor_close: SynthType = find("nicedoor_close")
    public val nicedoor_open: SynthType = find("nicedoor_open")
    public val pillory_success: SynthType = find("pillory_success")
    public val pillory_locked: SynthType = find("pillory_locked")
    public val pillory_unlock: SynthType = find("pillory_unlock")
    public val pillory_wrong: SynthType = find("pillory_wrong")
    public val pick2: SynthType = find("pick2")
    public val lever: SynthType = find("lever")
    public val tree_fall_sound: SynthType = find("tree_fall_sound")
}
