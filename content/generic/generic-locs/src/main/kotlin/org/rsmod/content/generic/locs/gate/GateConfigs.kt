package org.rsmod.content.generic.locs.gate

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal object GateConstants {
    /** The (cycle) duration that gates remains changed before reverting to their original state. */
    const val DURATION = 500
}

internal object GateLocs : LocReferences() {
    val picketgate_left_closed = find("picketgate_left_closed", 6120710262097893169)
    val picketgate_right_closed = find("picketgate_right_closed", 6120710262097893171)
    val picketgate_left_opened = find("picketgate_left_opened", 135511893709034488)
    val picketgate_right_opened = find("picketgate_right_opened", 135511893709034496)
    val nicepicketgate_left_closed = find("nicepicketgate_left_closed", 8381220604490231462)
    val nicepicketgate_right_closed = find("nicepicketgate_right_closed", 8381220604490254496)
    val nicepicketgate_left_opened = find("nicepicketgate_left_opened", 2396022236101395815)
    val nicepicketgate_right_opened = find("nicepicketgate_right_opened", 2396022236101395816)
    val farmerfred_gate_left_closed = find("farmerfred_gate_left_closed", 8381220604490243565)
    val farmerfred_gate_right_closed = find("farmerfred_gate_right_closed", 8381220604490243566)
    val farmerfred_gate_left_opened = find("farmerfred_gate_left_opened", 2396022236101384885)
    val farmerfred_gate_right_opened = find("farmerfred_gate_right_opened", 2395638821475390324)
}

internal object GateLocEditor : LocEditor() {
    init {
        edit("picketgate_left_closed") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = GateLocs.picketgate_left_opened
            contentGroup = content.closed_left_picketgate
        }

        edit("picketgate_right_closed") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = GateLocs.picketgate_right_opened
            contentGroup = content.closed_right_picketgate
        }

        edit("picketgate_left_opened") {
            param[params.closesound] = synths.picketgate_close
            param[params.next_loc_stage] = GateLocs.picketgate_left_closed
            contentGroup = content.opened_left_picketgate
        }

        edit("picketgate_right_opened") {
            param[params.closesound] = synths.picketgate_close
            param[params.next_loc_stage] = GateLocs.picketgate_right_closed
            contentGroup = content.opened_right_picketgate
        }

        edit("nicepicketgate_left_closed") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = GateLocs.nicepicketgate_left_opened
            contentGroup = content.closed_left_picketgate
        }

        edit("nicepicketgate_right_closed") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = GateLocs.nicepicketgate_right_opened
            contentGroup = content.closed_right_picketgate
        }

        edit("nicepicketgate_left_opened") {
            param[params.closesound] = synths.door_close
            param[params.next_loc_stage] = GateLocs.nicepicketgate_left_closed
            contentGroup = content.opened_left_picketgate
        }

        edit("nicepicketgate_right_opened") {
            param[params.closesound] = synths.door_close
            param[params.next_loc_stage] = GateLocs.nicepicketgate_right_closed
            contentGroup = content.opened_right_picketgate
        }

        edit("farmerfred_gate_left_closed") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = GateLocs.farmerfred_gate_left_opened
            contentGroup = content.closed_left_picketgate
        }

        edit("farmerfred_gate_right_closed") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = GateLocs.farmerfred_gate_right_opened
            contentGroup = content.closed_right_picketgate
        }

        edit("farmerfred_gate_left_opened") {
            param[params.closesound] = synths.door_close
            param[params.next_loc_stage] = GateLocs.farmerfred_gate_left_closed
            contentGroup = content.opened_left_picketgate
        }

        edit("farmerfred_gate_right_opened") {
            param[params.closesound] = synths.door_close
            param[params.next_loc_stage] = GateLocs.farmerfred_gate_right_closed
            contentGroup = content.opened_right_picketgate
        }
    }
}
