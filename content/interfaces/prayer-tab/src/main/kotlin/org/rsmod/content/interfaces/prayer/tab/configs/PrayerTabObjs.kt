package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.game.type.varbit.VarBitType

@Suppress("SameParameterValue")
internal object PrayerTabObjEditor : ObjEditor() {
    init {
        prayer("placeholder_xmas16_ball_inflated_red", varbits.thick_skin)
        prayer("xmas16_ball_inflated_green", varbits.burst_of_strength)
        prayer("placeholder_xmas16_ball_inflated_green", varbits.clarity_of_thought)
        prayer("xmas16_ball_inflated_blue", varbits.rock_skin)
        prayer("placeholder_xmas16_ball_inflated_blue", varbits.superhuman_strength)
        prayer("xmas16_ball_deflated_red", varbits.improved_reflexes)
        prayer("placeholder_xmas16_ball_deflated_red", varbits.rapid_restore)
        prayer("xmas16_ball_deflated_green", varbits.rapid_heal)
        prayer("placeholder_xmas16_ball_deflated_green", varbits.protect_item)
        prayer("xmas16_ball_deflated_blue", varbits.steel_skin)
        prayer("placeholder_xmas16_ball_deflated_blue", varbits.ultimate_strength)
        prayer("xmas16_marionette_unpainted", varbits.incredible_reflexes)
        prayer(
            "placeholder_xmas16_marionette_unpainted",
            varbits.protect_from_magic,
            prayer_constants.overhead_protect_from_magic,
        )
        prayer(
            "xmas16_marionette_blue",
            varbits.protect_from_missiles,
            prayer_constants.overhead_protect_from_missiles,
        )
        prayer(
            "placeholder_xmas16_marionette_blue",
            varbits.protect_from_melee,
            prayer_constants.overhead_protect_from_melee,
        )
        prayer(
            "xmas16_marionette_green",
            varbits.retribution,
            prayer_constants.overhead_retribution,
        )
        prayer(
            "placeholder_xmas16_marionette_green",
            varbits.redemption,
            prayer_constants.overhead_redemption,
        )
        prayer("xmas16_marionette_red", varbits.smite, prayer_constants.overhead_smite)
        prayer("placeholder_xmas16_marionette_red", varbits.sharp_eye)
        prayer("xmas16_blank_partyhat", varbits.mystic_will)
        prayer("placeholder_xmas16_blank_partyhat", varbits.hawk_eye)
        prayer("xmas16_red_partyhat", varbits.mystic_lore)
        prayer("placeholder_xmas16_red_partyhat", varbits.eagle_eye)
        prayer("xmas16_green_partyhat", varbits.mystic_might)
        prayer(
            "placeholder_xmas16_teddy",
            varbits.preserve,
            varbits.preserve_unlocked,
            "You need a <col=000080>Prayer</col> level of 55 and to have " +
                "learnt the prayer in<br>order to use <col=000080>Preserve</col>.",
        )
        prayer(
            "xmas16_blue_partyhat",
            varbits.chivalry,
            unlock = varbits.kr_knightwaves_state,
            unlockState = 8,
            defenceReq = 65,
            "You need a <col=000080>Prayer</col> level of 60, a " +
                "<col=000080>Defence</col> level of 65, and to have<br>" +
                "completed the <col=000080>King's Ransom</col> quest in " +
                "order to use <col=000080>Chivalry</col>.",
        )
        prayer(
            "placeholder_xmas16_blue_partyhat",
            varbits.piety,
            unlock = varbits.kr_knightwaves_state,
            unlockState = 8,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 70, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "completed the <col=000080>King's Ransom</col> quest in " +
                "order to use <col=000080>Piety</col>.",
        )
        prayer(
            "placeholder_xmas16_green_partyhat",
            varbits.rigour,
            unlock = varbits.rigour_unlocked,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 74, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "learnt the prayer in order to use <col=000080>Rigour</col>.",
        )
        prayer(
            "xmas16_teddy",
            varbits.augury,
            unlock = varbits.augury_unlocked,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 77, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "learnt the prayer in order to use <col=000080>Augury</col>.",
        )
    }

    private fun prayer(internal: String, varbit: VarBitType) {
        edit(internal) { param[prayer_params.varbit] = varbit }
    }

    private fun prayer(internal: String, varbit: VarBitType, overhead: Int) {
        edit(internal) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.overhead] = overhead
        }
    }

    private fun prayer(
        internal: String,
        varbit: VarBitType,
        unlock: VarBitType,
        lockedMessage: String,
    ) {
        edit(internal) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.locked_message] = lockedMessage
        }
    }

    private fun prayer(
        internal: String,
        varbit: VarBitType,
        unlock: VarBitType,
        defenceReq: Int,
        lockedMessage: String,
    ) {
        edit(internal) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.locked_message] = lockedMessage
            param[params.statreq1_skill] = stats.defence
            param[params.statreq1_level] = defenceReq
        }
    }

    private fun prayer(
        internal: String,
        varbit: VarBitType,
        unlock: VarBitType,
        unlockState: Int,
        defenceReq: Int,
        lockedMessage: String,
    ) {
        edit(internal) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.unlock_state] = unlockState
            param[prayer_params.locked_message] = lockedMessage
            param[params.statreq1_skill] = stats.defence
            param[params.statreq1_level] = defenceReq
        }
    }
}
