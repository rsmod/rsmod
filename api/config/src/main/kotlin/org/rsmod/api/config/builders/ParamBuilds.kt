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
        build<String>("game_message") {}
        build<String>("game_message2") {}

        build<LocType>("next_loc_stage") {}
        build<SynthType>("opensound") { default = synths.door_open }
        build<SynthType>("closesound") { default = synths.door_close }
        build<SeqType>("climb_anim") { default = seqs.human_reachforladddertop }

        build<Boolean>("shop_sale_restricted") { default = false }
    }
}
