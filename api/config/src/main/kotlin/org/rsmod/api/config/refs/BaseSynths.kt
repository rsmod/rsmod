@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.synth.SynthReferences

typealias synths = BaseSynths

object BaseSynths : SynthReferences() {
    val door_close = find("door_close")
    val door_open = find("door_open")
    val picketgate_close = find("picketgate_close")
    val picketgate_open = find("picketgate_open")
    val nicedoor_close = find("nicedoor_close")
    val nicedoor_open = find("nicedoor_open")
    val default_equipment = find("default_equipment")
    val pillory_success = find("pillory_success")
    val pillory_locked = find("pillory_locked")
    val pillory_unlock = find("pillory_unlock")
    val pillory_wrong = find("pillory_wrong")
    val put_down = find("put_down")
    val lever = find("lever")
    val pick2 = find("pick2")
    val tree_fall_sound = find("tree_fall_sound")
}
