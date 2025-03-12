@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.builders

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.headbars
import org.rsmod.api.config.refs.hitmarks
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.game.type.headbar.HeadbarType
import org.rsmod.game.type.hitmark.HitmarkType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.VarBitType

internal object ParamBuilds : ParamBuilder() {
    init {
        build<Int>("retreat") { default = -1 }

        build<Boolean>("td_shield_active") { default = true }
        build<Int>("tormented_demon") { default = 0 }

        build<Int>("demonbane_resistant") { default = 0 }
        build<Int>("corpbane") { default = 0 }
        build<Int>("slayer_helm") { default = 0 }
        build<Int>("blackmask") { default = 0 }

        build<Int>("shade") { default = 0 }
        build<Int>("leafy") { default = 0 }
        build<Int>("rat") { default = 0 }
        build<Int>("demon") { default = 0 }
        build<Int>("revenant") { default = 0 }
        build<Int>("undead") { default = 0 }

        build<HitmarkType>("hitmark_lit") { default = hitmarks.regular_damage_lit }
        build<HitmarkType>("hitmark_tint") { default = hitmarks.regular_damage_tint }
        build<HitmarkType>("hitmark_max") { default = hitmarks.regular_damage_max }

        build<HeadbarType>("headbar") { default = headbars.health_30 }

        build<ObjType>("rewarditem")
        build<NpcType>("next_npc_stage")
        build<Boolean>("bankside_extraop_conditional_flip") { default = false }
        build<VarBitType>("bankside_extraop_conditional_varbit")
        build<Int>("bankside_extraop_conditional_bit")
        build<Boolean>("bond_item") { default = false }

        build<Int>("bonus_undead_buff") { default = 0 }
        build<Boolean>("bonus_undead_meleeonly") { default = true }

        build<Int>("bonus_slayer_buff") { default = 0 }
        build<Boolean>("bonus_slayer_meleeonly") { default = true }

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
        build<SeqType>("attack_anim_stance1")
        build<SeqType>("attack_anim_stance2")
        build<SeqType>("attack_anim_stance3")
        build<SeqType>("attack_anim_stance4")
        build<SynthType>("attack_sound_stance1")
        build<SynthType>("attack_sound_stance2")
        build<SynthType>("attack_sound_stance3")
        build<SynthType>("attack_sound_stance4")
        build<SeqType>("bas_readyanim") { default = seqs.human_ready }
        build<SeqType>("bas_turnonspot") { default = seqs.human_turnonspot }
        build<SeqType>("bas_walk_f") { default = seqs.human_walk_f }
        build<SeqType>("bas_walk_b") { default = seqs.human_walk_b }
        build<SeqType>("bas_walk_l") { default = seqs.human_walk_l }
        build<SeqType>("bas_walk_r") { default = seqs.human_walk_r }
        build<SeqType>("bas_running") { default = seqs.human_running }
        build<SeqType>("attack_anim") { default = seqs.human_unarmedpunch }
        build<SeqType>("defend_anim") { default = seqs.human_unarmedblock }
        build<SeqType>("death_anim") { default = seqs.human_death }
        build<SynthType>("attack_sound")
        build<SynthType>("defend_sound")
        build<SynthType>("death_sound")
        build<Int>("attackrate") { default = constants.combat_default_attackrate }
    }
}
