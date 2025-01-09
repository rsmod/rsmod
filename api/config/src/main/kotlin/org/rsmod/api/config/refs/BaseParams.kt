@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.config.aliases.ParamBool
import org.rsmod.api.config.aliases.ParamInt
import org.rsmod.api.config.aliases.ParamLoc
import org.rsmod.api.config.aliases.ParamObj
import org.rsmod.api.config.aliases.ParamSeq
import org.rsmod.api.config.aliases.ParamStr
import org.rsmod.api.config.aliases.ParamSynth
import org.rsmod.api.type.refs.param.ParamReferences

public typealias params = BaseParams

public object BaseParams : ParamReferences() {
    public val wear_op_index: ParamInt = find("wear_op_index", 88714906312)
    public val wear_op1: ParamStr = find("wear_op1", 97133155617)
    public val wear_op2: ParamStr = find("wear_op2", 97133155618)
    public val wear_op3: ParamStr = find("wear_op3", 97133155619)
    public val wear_op4: ParamStr = find("wear_op4", 97133155620)
    public val wear_op5: ParamStr = find("wear_op5", 97133155621)
    public val wear_op6: ParamStr = find("wear_op6", 97133155622)
    public val wear_op7: ParamStr = find("wear_op7", 97133155623)
    public val wear_op8: ParamStr = find("wear_op8", 97133155624)
    public val statreq0_level: ParamInt = find("statreq0_level", 88687192592)
    public val statreq1_level: ParamInt = find("statreq1_level", 88687192593)

    /* Server-side only types */
    public val respawn_time: ParamInt = find("respawn_time")
    public val despawn_time: ParamInt = find("despawn_time")
    public val deplete_chance: ParamInt = find("deplete_chance")
    public val skill_sound: ParamSynth = find("skill_sound")
    public val skill_productitem: ParamObj = find("skill_productitem")
    public val skill_xp: ParamInt = find("skill_xp")
    public val skill_anim: ParamSeq = find("skill_anim")
    public val skill_levelreq: ParamInt = find("skill_levelreq")
    public val game_message: ParamStr = find("game_message")
    public val game_message2: ParamStr = find("game_message2")
    public val climb_anim: ParamSeq = find("climb_anim")
    public val closesound: ParamSynth = find("closesound")
    public val opensound: ParamSynth = find("opensound")
    public val next_loc_stage: ParamLoc = find("next_loc_stage")
    public val shop_sale_restricted: ParamBool = find("shop_sale_restricted")
}
