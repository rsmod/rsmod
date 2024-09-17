@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.config.aliases.ParamInt
import org.rsmod.api.config.aliases.ParamLoc
import org.rsmod.api.config.aliases.ParamSeq
import org.rsmod.api.config.aliases.ParamStr
import org.rsmod.api.config.aliases.ParamSynth
import org.rsmod.api.type.refs.param.ParamReferences

public typealias params = BaseParams

public object BaseParams : ParamReferences() {
    public val wear_op_index: ParamInt = find(88714906312)
    public val wear_op1: ParamStr = find(97133155617)
    public val wear_op2: ParamStr = find(97133155618)
    public val wear_op3: ParamStr = find(97133155619)
    public val wear_op4: ParamStr = find(97133155620)
    public val wear_op5: ParamStr = find(97133155621)
    public val wear_op6: ParamStr = find(97133155622)
    public val wear_op7: ParamStr = find(97133155623)
    public val wear_op8: ParamStr = find(97133155624)
    public val statreq0_level: ParamInt = find(88687192592)
    public val statreq1_level: ParamInt = find(88687192593)

    public val game_message: ParamStr = find(97133220695)
    public val game_message2: ParamStr = find(97133220696)
    public val climb_anim: ParamSeq = find(66367761995)
    public val closesound: ParamSynth = find(68403100623)
    public val opensound: ParamSynth = find(68430792307)
    public val next_loc_stage: ParamLoc = find(91221046592)
}
