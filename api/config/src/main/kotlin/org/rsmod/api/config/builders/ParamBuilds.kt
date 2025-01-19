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

        build<String>("destroy_note_title") {
            default = "Are you sure you want to destroy this item?"
        }
        build<String>("destroy_note_desc") { default = "This action cannot be undone." }

        build<String>("release_note_title")
        build<String>("release_note_message")

        build<String>("statreq_failmessage1") {
            default = "You are not a high enough level to use this item."
        }
        build<String>("statreq_failmessage2")

        build<String>("player_op5_text")
        build<SynthType>("equipment_sound") { default = synths.default_equipment }

        build<Int>("attack_range") { default = 1 }
        build<SeqType>("attack_anim_accurate")
        build<SeqType>("attack_anim_aggressive")
        build<SeqType>("attack_anim_controlled")
        build<SeqType>("attack_anim_defensive")
        build<SynthType>("attack_sound_accurate")
        build<SynthType>("attack_sound_aggressive")
        build<SynthType>("attack_sound_controlled")
        build<SynthType>("attack_sound_defensive")
        build<SeqType>("bas_readyanim") { default = seqs.human_ready }
        build<SeqType>("bas_turnonspot") { default = seqs.human_turnonspot }
        build<SeqType>("bas_walk_f") { default = seqs.human_walk_f }
        build<SeqType>("bas_walk_b") { default = seqs.human_walk_b }
        build<SeqType>("bas_walk_l") { default = seqs.human_walk_l }
        build<SeqType>("bas_walk_r") { default = seqs.human_walk_r }
        build<SeqType>("bas_running") { default = seqs.human_running }
        build<SeqType>("attack_anim")
        build<SeqType>("defend_anim")
        build<Int>("attackrate") { default = 4 }
    }
}
