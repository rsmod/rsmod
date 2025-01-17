@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.builders

import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType

internal object ParamBuilds : ParamBuilder() {
    init {
        build<String>("game_message")
        build<String>("game_message2")

        build<LocType>("next_loc_stage")
        build<SynthType>("opensound") { default = synths.door_open }
        build<SynthType>("closesound") { default = synths.door_close }
        build<SeqType>("climb_anim") { default = seqs.human_reachforladddertop }

        build<Boolean>("shop_sale_restricted") { default = false }

        build<SeqType>("skill_anim")
        build<Int>("skill_levelreq")
        build<Int>("skill_xp")
        build<ObjType>("skill_productitem")
        build<SynthType>("skill_sound")

        build<Int>("deplete_chance")
        build<Int>("despawn_time")
        build<Int>("respawn_time")
        build<Int>("respawn_time_low")
        build<Int>("respawn_time_high")

        build<StatType>("xpmod_stat")
        build<Int>("xpmod_percent")

        // TODO: Integration test to ensure all objs with `Destroy` iop have a destroy note params.
        //  + Remove defaults once all destroyable objs have notes.
        //  Same goes for `Release` iop.
        build<String>("destroy_note_title") {
            default = "Are you sure you want to destroy this item?"
        }
        build<String>("destroy_note_desc") { default = "This action cannot be undone." }

        build<String>("release_note_title") { default = "Drop all of this item?" }
        build<String>("release_note_message") { default = "You release it and it bounds away." }

        build<String>("statreq_failmessage1") {
            default = "You are not a high enough level to use this item."
        }
        build<String>("statreq_failmessage2")

        build<String>("player_op5_text")
        build<SynthType>("equipment_sound") { default = synths.default_equipment }
    }
}
