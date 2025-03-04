@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.config.aliases.ParamBool
import org.rsmod.api.config.aliases.ParamInt
import org.rsmod.api.config.aliases.ParamLoc
import org.rsmod.api.config.aliases.ParamNpc
import org.rsmod.api.config.aliases.ParamObj
import org.rsmod.api.config.aliases.ParamSeq
import org.rsmod.api.config.aliases.ParamStat
import org.rsmod.api.config.aliases.ParamStr
import org.rsmod.api.config.aliases.ParamSynth
import org.rsmod.api.config.aliases.ParamVarBit
import org.rsmod.api.type.refs.param.ParamReferences

typealias params = BaseParams

object BaseParams : ParamReferences() {
    val attackrate: ParamInt = find("attackrate", 88742575534)
    val wear_op_index: ParamInt = find("wear_op_index", 88714906312)
    val wear_op1: ParamStr = find("wear_op1", 97133155617)
    val wear_op2: ParamStr = find("wear_op2", 97133155618)
    val wear_op3: ParamStr = find("wear_op3", 97133155619)
    val wear_op4: ParamStr = find("wear_op4", 97133155620)
    val wear_op5: ParamStr = find("wear_op5", 97133155621)
    val wear_op6: ParamStr = find("wear_op6", 97133155622)
    val wear_op7: ParamStr = find("wear_op7", 97133155623)
    val wear_op8: ParamStr = find("wear_op8", 97133155624)
    val bankside_extraop: ParamStr = find("bankside_extraop", 97133155761)
    val statreq1_skill: ParamStat = find("statreq1_skill", 70092228127)
    val statreq1_level: ParamInt = find("statreq1_level", 88687192592)
    val statreq2_skill: ParamStat = find("statreq2_skill", 70092228128)
    val statreq2_level: ParamInt = find("statreq2_level", 88687192593)
    val no_bank: ParamInt = find("no_bank", 88687214541)
    val no_alchemy: ParamInt = find("no_alchemy", 88687192451)
    val attack_stab: ParamInt = find("attack_stab", 88687192156)
    val attack_slash: ParamInt = find("attack_slash", 88687192157)
    val attack_crush: ParamInt = find("attack_crush", 88687192158)
    val attack_magic: ParamInt = find("attack_magic", 88687192159)
    val attack_ranged: ParamInt = find("attack_ranged", 88687192160)
    val defence_stab: ParamInt = find("defence_stab", 88687192161)
    val defence_slash: ParamInt = find("defence_slash", 88687192162)
    val defence_crush: ParamInt = find("defence_crush", 88687192163)
    val defence_magic: ParamInt = find("defence_magic", 88687192164)
    val defence_ranged: ParamInt = find("defence_ranged", 88687192165)
    val melee_strength: ParamInt = find("melee_strength", 88687192166)
    val ranged_strength: ParamInt = find("ranged_strength", 88687192168)
    val additive_ranged_strength: ParamInt = find("additive_ranged_strength", 88687192345)
    val magic_damage: ParamInt = find("magic_damage", 88687192455)
    val item_prayer_bonus: ParamInt = find("item_prayer_bonus", 88687192167)

    /* Server-side only types */
    val rewarditem: ParamObj = find("rewarditem")
    val next_npc_stage: ParamNpc = find("next_npc_stage")
    val bankside_extraop_flip: ParamBool = find("bankside_extraop_conditional_flip")
    val bankside_extraop_varbit: ParamVarBit = find("bankside_extraop_conditional_varbit")
    val bankside_extraop_bit: ParamInt = find("bankside_extraop_conditional_bit")
    val bond_item: ParamBool = find("bond_item")
    val bonus_slayer_meleeonly: ParamBool = find("bonus_slayer_meleeonly")
    val bonus_slayer_buff: ParamInt = find("bonus_slayer_buff")
    val bonus_undead_meleeonly: ParamBool = find("bonus_undead_meleeonly")
    val bonus_undead_buff: ParamInt = find("bonus_undead_buff")
    val attackrange: ParamInt = find("attack_range")
    val attack_anim_stance1: ParamSeq = find("attack_anim_stance1")
    val attack_anim_stance2: ParamSeq = find("attack_anim_stance2")
    val attack_anim_stance3: ParamSeq = find("attack_anim_stance3")
    val attack_anim_stance4: ParamSeq = find("attack_anim_stance4")
    val attack_sound_stance1: ParamSynth = find("attack_sound_stance1")
    val attack_sound_stance2: ParamSynth = find("attack_sound_stance2")
    val attack_sound_stance3: ParamSynth = find("attack_sound_stance3")
    val attack_sound_stance4: ParamSynth = find("attack_sound_stance4")
    val bas_readyanim: ParamSeq = find("bas_readyanim")
    val bas_turnonspot: ParamSeq = find("bas_turnonspot")
    val bas_walk_f: ParamSeq = find("bas_walk_f")
    val bas_walk_b: ParamSeq = find("bas_walk_b")
    val bas_walk_l: ParamSeq = find("bas_walk_l")
    val bas_walk_r: ParamSeq = find("bas_walk_r")
    val bas_running: ParamSeq = find("bas_running")
    val attack_anim: ParamSeq = find("attack_anim")
    val defend_anim: ParamSeq = find("defend_anim")
    val equipment_sound: ParamSynth = find("equipment_sound")
    val player_op5_text: ParamStr = find("player_op5_text")
    val release_note_title: ParamStr = find("release_note_title")
    val release_note_message: ParamStr = find("release_note_message")
    val statreq_failmessage1: ParamStr = find("statreq_failmessage1")
    val statreq_failmessage2: ParamStr = find("statreq_failmessage2")
    val destroy_note_title: ParamStr = find("destroy_note_title")
    val destroy_note_desc: ParamStr = find("destroy_note_desc")
    val xpmod_percent: ParamInt = find("xpmod_percent")
    val xpmod_stat: ParamStat = find("xpmod_stat")
    val respawn_time_low: ParamInt = find("respawn_time_low")
    val respawn_time_high: ParamInt = find("respawn_time_high")
    val respawn_time: ParamInt = find("respawn_time")
    val despawn_time: ParamInt = find("despawn_time")
    val deplete_chance: ParamInt = find("deplete_chance")
    val skill_sound: ParamSynth = find("skill_sound")
    val skill_productitem: ParamObj = find("skill_productitem")
    val skill_xp: ParamInt = find("skill_xp")
    val skill_anim: ParamSeq = find("skill_anim")
    val skill_levelreq: ParamInt = find("skill_levelreq")
    val game_message: ParamStr = find("game_message")
    val game_message2: ParamStr = find("game_message2")
    val climb_anim: ParamSeq = find("climb_anim")
    val closesound: ParamSynth = find("closesound")
    val opensound: ParamSynth = find("opensound")
    val next_loc_stage: ParamLoc = find("next_loc_stage")
    val shop_sale_restricted: ParamBool = find("shop_sale_restricted")
}
