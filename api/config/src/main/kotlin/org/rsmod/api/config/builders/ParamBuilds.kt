@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.builders

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.headbars
import org.rsmod.api.config.refs.hitmarks
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.headbar.HeadbarType
import org.rsmod.game.type.hitmark.HitmarkType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.proj.ProjAnimType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.map.CoordGrid

internal object ParamBuilds : ParamBuilder() {
    init {
        build<Int>("graceful_restore_rate")

        build<ObjType>("charged_variant")
        build<ObjType>("uncharged_variant")

        build<CoordGrid>("spell_telecoord")
        build<Int>("spell_castxp")

        build<Int>("amascutnpc") { default = 0 }
        build<Int>("xerician") { default = 0 }

        build<Int>("poison_immunity") { default = 0 }
        build<Int>("cannon_immunity") { default = 0 }
        build<Int>("thrall_immunity") { default = 0 }
        build<Int>("venom_immunity") { default = 0 }
        build<Int>("burn_immunity") { default = 0 }

        build<Int>("freeze_resistance") { default = 0 }

        build<Int>("slayer_experience") { default = 0 }
        build<Int>("slayer_levelrequire") { default = 0 }

        build<Boolean>("magic_defence_uses_defence_level") { default = false }
        build<String>("spell_worn_req_message")

        build<Int>("elemental_weakness_type")
        build<Int>("elemental_weakness_percent") { default = 0 }

        // This value represents a decimal xp multiplier, scaled by 1000. For example, a
        // value of 1000 equals a 1.0x multiplier, and 1075 would equal 1.075x (+7.5%).
        build<Int>("npc_com_xp_multiplier")

        build<CategoryType>("npc_attack_type")
        build<ProjAnimType>("proj_type")
        build<SpotanimType>("proj_travel")
        build<SpotanimType>("proj_launch")
        build<SpotanimType>("proj_launch_double")

        build<Int>("defence_light") { default = 0 }
        build<Int>("defence_standard") { default = 0 }
        build<Int>("defence_heavy") { default = 0 }

        build<Boolean>("metallic_interference") { default = false }
        build<Int>("ammo_recovery_rate")
        build<CategoryType>("required_ammo")
        build<Int>("bone_weapon") { default = 0 }

        build<SynthType>("item_block_sound1")
        build<SynthType>("item_block_sound2")
        build<SynthType>("item_block_sound3")
        build<SynthType>("item_block_sound4")
        build<SynthType>("item_block_sound5")

        build<Int>("retreat") { default = -1 }

        build<Boolean>("td_shield_active") { default = true }
        build<Int>("tormented_demon") { default = 0 }

        build<Int>("demonbane_resistant") { default = 0 }

        build<Int>("slayer_helm") { default = 0 }
        build<Int>("slayer_helm_imbued") { default = 0 }

        build<Int>("blackmask") { default = 0 }
        build<Int>("blackmask_imbued") { default = 0 }

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
        build<SeqType>("climb_anim") { default = seqs.human_reachforladder }

        build<Boolean>("shop_sale_restricted") { default = false }

        build<SeqType>("skill_anim")
        build<Int>("skill_xp")
        build<ObjType>("skill_productitem")
        build<SynthType>("skill_sound")

        build<Int>("deplete_chance")
        build<Int>("despawn_time")
        build<Int>("respawn_time")
        build<Int>("respawn_time_low")
        build<Int>("respawn_time_high")

        build<Int>("food_heal_value") { default = 0 }
        build<Int>("food_secondary_heal") { default = 0 }
        build<Boolean>("food_is_combo") { default = false }
        build<Boolean>("food_overheal") { default = false }
        build<Boolean>("food_requires_replacement") { default = false }
        build<ObjType>("food_replacement") { default = null }

        build<StatType>("boosted_skill1")
        build<StatType>("boosted_skill2")
        build<StatType>("boosted_skill3")
        build<Int>("boosted_skill1_value")
        build<Int>("boosted_skill2_value")
        build<Int>("boosted_skill3_value")


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
        build<Int>("attack_melee") { default = 0 }
        build<Int>("attackrate") { default = constants.combat_default_attackrate }
    }
}
