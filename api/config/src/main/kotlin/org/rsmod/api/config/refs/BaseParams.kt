@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.param.ParamReferences

typealias params = BaseParams

object BaseParams : ParamReferences() {
    val wear_op_index = find("wear_op_index", 88714906312)
    val wear_op1 = find("wear_op1", 97133155617)
    val wear_op2 = find("wear_op2", 97133155618)
    val wear_op3 = find("wear_op3", 97133155619)
    val wear_op4 = find("wear_op4", 97133155620)
    val wear_op5 = find("wear_op5", 97133155621)
    val wear_op6 = find("wear_op6", 97133155622)
    val wear_op7 = find("wear_op7", 97133155623)
    val wear_op8 = find("wear_op8", 97133155624)
    val statreq1_skill = find("statreq1_skill")
    val statreq1_level = find("statreq1_level", 88687192592)
    val statreq2_skill = find("statreq2_skill")
    val statreq2_level = find("statreq2_level", 88687192593)
    val no_alchemy = find("no_alchemy")

    /* Server-side only types */
    val release_note_title = find("release_note_title")
    val release_note_message = find("release_note_message")
    val statreq_failmessage1 = find("statreq_failmessage1")
    val statreq_failmessage2 = find("statreq_failmessage2")
    val destroy_note_title = find("destroy_note_title")
    val destroy_note_desc = find("destroy_note_desc")
    val xpmod_percent = find("xpmod_percent")
    val xpmod_stat = find("xpmod_stat")
    val respawn_time_low = find("respawn_time_low")
    val respawn_time_high = find("respawn_time_high")
    val respawn_time = find("respawn_time")
    val despawn_time = find("despawn_time")
    val deplete_chance = find("deplete_chance")
    val skill_sound = find("skill_sound")
    val skill_productitem = find("skill_productitem")
    val skill_xp = find("skill_xp")
    val skill_anim = find("skill_anim")
    val skill_levelreq = find("skill_levelreq")
    val game_message = find("game_message")
    val game_message2 = find("game_message2")
    val climb_anim = find("climb_anim")
    val closesound = find("closesound")
    val opensound = find("opensound")
    val next_loc_stage = find("next_loc_stage")
    val shop_sale_restricted = find("shop_sale_restricted")
}
