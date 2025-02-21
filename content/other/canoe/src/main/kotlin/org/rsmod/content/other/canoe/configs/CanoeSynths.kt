package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.refs.synth.SynthReferences

typealias canoe_synths = CanoeSynths

object CanoeSynths : SynthReferences() {
    val canoe_pushed = find("canoe_pushed")
    val canoe_paddle = find("canoe_paddle")
    val canoe_sink = find("canoe_sink")
}
