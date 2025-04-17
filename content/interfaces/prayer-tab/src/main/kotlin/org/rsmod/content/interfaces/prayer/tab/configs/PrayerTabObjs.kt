package org.rsmod.content.interfaces.prayer.tab.configs

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
        prayer(prayer_objs.thick_skin, varbits.thick_skin)
        prayer(prayer_objs.burst_of_strength, varbits.burst_of_strength)
        prayer(prayer_objs.clarity_of_thought, varbits.clarity_of_thought)
        prayer(prayer_objs.rock_skin, varbits.rock_skin)
        prayer(prayer_objs.superhuman_strength, varbits.superhuman_strength)
        prayer(prayer_objs.improved_reflexes, varbits.improved_reflexes)
        prayer(prayer_objs.rapid_restore, varbits.rapid_restore)
        prayer(prayer_objs.rapid_heal, varbits.rapid_heal)
        prayer(prayer_objs.protect_item, varbits.protect_item)
        prayer(prayer_objs.steel_skin, varbits.steel_skin)
        prayer(prayer_objs.ultimate_strength, varbits.ultimate_strength)
        prayer(prayer_objs.incredible_reflexes, varbits.incredible_reflexes)
        prayer(
            prayer_objs.protect_from_magic,
            varbits.protect_from_magic,
            prayer_constants.overhead_protect_from_magic,
        )
        prayer(
            prayer_objs.protect_from_missiles,
            varbits.protect_from_missiles,
            prayer_constants.overhead_protect_from_missiles,
        )
        prayer(
            prayer_objs.protect_from_melee,
            varbits.protect_from_melee,
            prayer_constants.overhead_protect_from_melee,
        )
        prayer(prayer_objs.retribution, varbits.retribution, prayer_constants.overhead_retribution)
        prayer(prayer_objs.redemption, varbits.redemption, prayer_constants.overhead_redemption)
        prayer(prayer_objs.smite, varbits.smite, prayer_constants.overhead_smite)
        prayer(prayer_objs.sharp_eye, varbits.sharp_eye)
        prayer(prayer_objs.mystic_will, varbits.mystic_will)
        prayer(prayer_objs.hawk_eye, varbits.hawk_eye)
        prayer(prayer_objs.mystic_lore, varbits.mystic_lore)
        prayer(prayer_objs.eagle_eye, varbits.eagle_eye)
        prayer(prayer_objs.mystic_might, varbits.mystic_might)
        prayer(
            prayer_objs.preserve,
            varbits.preserve,
            unlock = varbits.preserve_unlocked,
            "You need a <col=000080>Prayer</col> level of 55 and to have " +
                "learnt the prayer in<br>order to use <col=000080>Preserve</col>.",
        )
        prayer(
            prayer_objs.chivalry,
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
            prayer_objs.piety,
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
            prayer_objs.rigour,
            varbits.rigour,
            unlock = varbits.rigour_unlocked,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 74, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "learnt the prayer in order to use <col=000080>Rigour</col>.",
        )
        prayer(
            prayer_objs.augury,
            varbits.augury,
            unlock = varbits.augury_unlocked,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 77, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "learnt the prayer in order to use <col=000080>Augury</col>.",
        )
    }

    private fun prayer(type: ObjType, varbit: VarBitType) {
        edit(type) { param[prayer_params.varbit] = varbit }
    }

    private fun prayer(type: ObjType, varbit: VarBitType, overhead: Int) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.overhead] = overhead
        }
    }

    private fun prayer(
        type: ObjType,
        varbit: VarBitType,
        unlock: VarBitType,
        lockedMessage: String,
    ) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.locked_message] = lockedMessage
        }
    }

    private fun prayer(
        type: ObjType,
        varbit: VarBitType,
        unlock: VarBitType,
        defenceReq: Int,
        lockedMessage: String,
    ) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.locked_message] = lockedMessage
            param[params.statreq1_skill] = stats.defence
            param[params.statreq1_level] = defenceReq
        }
    }

    private fun prayer(
        type: ObjType,
        varbit: VarBitType,
        unlock: VarBitType,
        unlockState: Int,
        defenceReq: Int,
        lockedMessage: String,
    ) {
        edit(type) {
            param[prayer_params.varbit] = varbit
            param[prayer_params.unlock_varbit] = unlock
            param[prayer_params.unlock_state] = unlockState
            param[prayer_params.locked_message] = lockedMessage
            param[params.statreq1_skill] = stats.defence
            param[params.statreq1_level] = defenceReq
        }
    }
}
