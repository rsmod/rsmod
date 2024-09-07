@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.builders

import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.synth.SynthType

internal object ParamBuilds : ParamBuilder() {
    init {
        buildTyped<String>("game_message") {}
        buildTyped<String>("game_message2") {}

        buildTyped<LocType>("next_loc_stage") {}
        buildTyped<SynthType>("opensound") { default = synths.door_open }
        buildTyped<SynthType>("closesound") { default = synths.door_close }
        buildTyped<SeqType>("climb_anim") { default = seqs.human_reachforladddertop }
    }
}
