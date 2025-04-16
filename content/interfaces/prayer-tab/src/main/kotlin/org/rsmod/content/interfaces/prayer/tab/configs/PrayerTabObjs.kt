package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.game.type.varbit.VarBitType

@Suppress("SameParameterValue")
internal object PrayerTabObjEditor : ObjEditor() {
    init {
        prayer("prayer_thick_skin", varbits.thick_skin)
        prayer("prayer_burst_of_strength", varbits.burst_of_strength)
        prayer("prayer_clarity_of_thought", varbits.clarity_of_thought)
        prayer("prayer_rock_skin", varbits.rock_skin)
        prayer("prayer_superhuman_strength", varbits.superhuman_strength)
        prayer("prayer_improved_reflexes", varbits.improved_reflexes)
        prayer("prayer_rapid_restore", varbits.rapid_restore)
        prayer("prayer_rapid_heal", varbits.rapid_heal)
        prayer("prayer_protect_item", varbits.protect_item)
        prayer("prayer_steel_skin", varbits.steel_skin)
        prayer("prayer_ultimate_strength", varbits.ultimate_strength)
        prayer("prayer_incredible_reflexes", varbits.incredible_reflexes)
        prayer(
            "prayer_protect_from_magic",
            varbits.protect_from_magic,
            prayer_constants.overhead_protect_from_magic,
        )
        prayer(
            "prayer_protect_from_missiles",
            varbits.protect_from_missiles,
            prayer_constants.overhead_protect_from_missiles,
        )
        prayer(
            "prayer_protect_from_melee",
            varbits.protect_from_melee,
            prayer_constants.overhead_protect_from_melee,
        )
        prayer("prayer_retribution", varbits.retribution, prayer_constants.overhead_retribution)
        prayer("prayer_redemption", varbits.redemption, prayer_constants.overhead_redemption)
        prayer("prayer_smite", varbits.smite, prayer_constants.overhead_smite)
        prayer("prayer_sharp_eye", varbits.sharp_eye)
        prayer("prayer_mystic_will", varbits.mystic_will)
        prayer("prayer_hawk_eye", varbits.hawk_eye)
        prayer("prayer_mystic_lore", varbits.mystic_lore)
        prayer("prayer_eagle_eye", varbits.eagle_eye)
        prayer("prayer_mystic_might", varbits.mystic_might)
        prayer(
            "prayer_preserve",
            varbits.preserve,
            varbits.preserve_unlocked,
            "You need a <col=000080>Prayer</col> level of 55 and to have " +
                "learnt the prayer in<br>order to use <col=000080>Preserve</col>.",
        )
        prayer(
            "prayer_chivalry",
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
            "prayer_piety",
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
            "prayer_rigour",
            varbits.rigour,
            unlock = varbits.rigour_unlocked,
            defenceReq = 70,
            "You need a <col=000080>Prayer</col> level of 74, a " +
                "<col=000080>Defence</col> level of 70, and to have<br>" +
                "learnt the prayer in order to use <col=000080>Rigour</col>.",
        )
        prayer(
            "prayer_augury",
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
