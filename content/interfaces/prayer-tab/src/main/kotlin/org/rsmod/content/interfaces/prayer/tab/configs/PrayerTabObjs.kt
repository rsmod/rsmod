package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.varbit.VarBitType

internal typealias prayer_objs = PrayerTabObjs

internal object PrayerTabObjs : ObjReferences() {
    val thick_skin = find("placeholder_xmas16_ball_inflated_red")
    val burst_of_strength = find("xmas16_ball_inflated_green")
    val clarity_of_thought = find("placeholder_xmas16_ball_inflated_green")
    val rock_skin = find("xmas16_ball_inflated_blue")
    val superhuman_strength = find("placeholder_xmas16_ball_inflated_blue")
    val improved_reflexes = find("xmas16_ball_deflated_red")
    val rapid_restore = find("placeholder_xmas16_ball_deflated_red")
    val rapid_heal = find("xmas16_ball_deflated_green")
    val protect_item = find("placeholder_xmas16_ball_deflated_green")
    val steel_skin = find("xmas16_ball_deflated_blue")
    val ultimate_strength = find("placeholder_xmas16_ball_deflated_blue")
    val incredible_reflexes = find("xmas16_marionette_unpainted")
    val protect_from_magic = find("placeholder_xmas16_marionette_unpainted")
    val protect_from_missiles = find("xmas16_marionette_blue")
    val protect_from_melee = find("placeholder_xmas16_marionette_blue")
    val retribution = find("xmas16_marionette_green")
    val redemption = find("placeholder_xmas16_marionette_green")
    val smite = find("xmas16_marionette_red")
    val sharp_eye = find("placeholder_xmas16_marionette_red")
    val mystic_will = find("xmas16_blank_partyhat")
    val hawk_eye = find("placeholder_xmas16_blank_partyhat")
    val mystic_lore = find("xmas16_red_partyhat")
    val eagle_eye = find("placeholder_xmas16_red_partyhat")
    val mystic_might = find("xmas16_green_partyhat")
    val preserve = find("placeholder_xmas16_teddy")
    val chivalry = find("xmas16_blue_partyhat")
    val piety = find("placeholder_xmas16_blue_partyhat")
    val rigour = find("placeholder_xmas16_green_partyhat")
    val augury = find("xmas16_teddy")
}

@Suppress("SameParameterValue")
internal object PrayerTabObjEditor : ObjEditor() {
    init {
        prayer(prayer_objs.thick_skin, varbits.thick_skin, drain = 1)
        prayer(prayer_objs.burst_of_strength, varbits.burst_of_strength, drain = 1)
        prayer(prayer_objs.clarity_of_thought, varbits.clarity_of_thought, drain = 1)
        prayer(prayer_objs.rock_skin, varbits.rock_skin, drain = 6)
        prayer(prayer_objs.superhuman_strength, varbits.superhuman_strength, drain = 6)
        prayer(prayer_objs.improved_reflexes, varbits.improved_reflexes, drain = 6)
        prayer(prayer_objs.rapid_restore, varbits.rapid_restore, drain = 1)
        prayer(prayer_objs.rapid_heal, varbits.rapid_heal, drain = 2)
        prayer(prayer_objs.protect_item, varbits.protect_item, drain = 2)
        prayer(prayer_objs.steel_skin, varbits.steel_skin, drain = 12)
        prayer(prayer_objs.ultimate_strength, varbits.ultimate_strength, drain = 12)
        prayer(prayer_objs.incredible_reflexes, varbits.incredible_reflexes, drain = 12)
        prayer(
            prayer_objs.protect_from_magic,
            varbits.protect_from_magic,
            constants.overhead_protect_from_magic,
            drain = 12,
        )
        prayer(
            prayer_objs.protect_from_missiles,
            varbits.protect_from_missiles,
            constants.overhead_protect_from_missiles,
            drain = 12,
        )
        prayer(
            prayer_objs.protect_from_melee,
            varbits.protect_from_melee,
            constants.overhead_protect_from_melee,
            drain = 12,
        )
        prayer(
            prayer_objs.retribution,
            varbits.retribution,
            constants.overhead_retribution,
            drain = 3,
        )
        prayer(prayer_objs.redemption, varbits.redemption, constants.overhead_redemption, drain = 6)
        prayer(prayer_objs.smite, varbits.smite, constants.overhead_smite, drain = 18)
        prayer(prayer_objs.sharp_eye, varbits.sharp_eye, drain = 1)
        prayer(prayer_objs.mystic_will, varbits.mystic_will, drain = 1)
        prayer(prayer_objs.hawk_eye, varbits.hawk_eye, drain = 6)
        prayer(prayer_objs.mystic_lore, varbits.mystic_lore, drain = 6)
        prayer(prayer_objs.eagle_eye, varbits.eagle_eye, drain = 12)
        prayer(prayer_objs.mystic_might, varbits.mystic_might, drain = 12)
        prayer(
            prayer_objs.preserve,
            varbits.preserve,
            drain = 2,
            unlock = varbits.preserve_unlocked,
            "You need a <col=000080>Prayer</col> level of 55 and to have " +
                "learnt the prayer in<br>order to use <col=000080>Preserve</col>.",
        )
        prayer(
            prayer_objs.chivalry,
            varbits.chivalry,
            drain = 24,
            unlock = varbits.kr_knightwaves_state,
            unlockState = 8,
            defenceReq = 65,
            "You need a <col=000080>Prayer</col> level of 60, a " +
                "<col=000080>Defence</col> level of 65, and to have<br>" +
                "completed the <col=000080>King's Ransom</col> quest in " +
                "order to use <col=000080>Chivalry</col>.",
        )
        prayer(
            prayer_objs.piety,
            varbits.piety,
            drain = 24,
            unlock = varbits.kr_knightwaves_state,
            unlockState = 8,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 70, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "completed the <col=000080>King's Ransom</col> quest in " +
                "order to use <col=000080>Piety</col>.",
        )
        prayer(
            prayer_objs.rigour,
            varbits.rigour,
            drain = 24,
            unlock = varbits.rigour_unlocked,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 74, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "learnt the prayer in order to use <col=000080>Rigour</col>.",
        )
        prayer(
            prayer_objs.augury,
            varbits.augury,
            drain = 24,
            unlock = varbits.augury_unlocked,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 77, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "learnt the prayer in order to use <col=000080>Augury</col>.",
        )
    }

    private fun prayer(type: ObjType, varbit: VarBitType, drain: Int) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.drain_effect] = drain
        }
    }

    private fun prayer(type: ObjType, varbit: VarBitType, overhead: Int, drain: Int) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.overhead] = overhead
            param[prayer_params.drain_effect] = drain
        }
    }

    private fun prayer(
        type: ObjType,
        varbit: VarBitType,
        drain: Int,
        unlock: VarBitType,
        lockedMessage: String,
    ) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.drain_effect] = drain
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.locked_message] = lockedMessage
        }
    }

    private fun prayer(
        type: ObjType,
        varbit: VarBitType,
        drain: Int,
        unlock: VarBitType,
        defenceReq: Int,
        lockedMessage: String,
    ) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.drain_effect] = drain
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.locked_message] = lockedMessage
            param[params.statreq1_skill] = stats.defence
            param[params.statreq1_level] = defenceReq
        }
    }

    private fun prayer(
        type: ObjType,
        varbit: VarBitType,
        drain: Int,
        unlock: VarBitType,
        unlockState: Int,
        defenceReq: Int,
        lockedMessage: String,
    ) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.drain_effect] = drain
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.unlock_state] = unlockState
            param[prayer_params.locked_message] = lockedMessage
            param[params.statreq1_skill] = stats.defence
            param[params.statreq1_level] = defenceReq
        }
    }
}
