package org.rsmod.content.generic.locs.gate

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias gate_locs = GateLocs

internal object GateConstants {
    /** The (cycle) duration that gates remains changed before reverting to their original state. */
    const val DURATION = 500
}

internal object GateLocs : LocReferences() {
    val picketgate_left_closed = find("fencegate_l", 6120710262097893169)
    val picketgate_right_closed = find("fencegate_r", 6120710262097893171)
    val picketgate_left_opened = find("openfencegate_l", 135511893709034488)
    val picketgate_right_opened = find("openfencegate_r", 135511893709034496)
    val nicepicketgate_left_closed = find("rustic_fencegate_l", 8381220604490231462)
    val nicepicketgate_right_closed = find("rustic_fencegate_r", 8381220604490254496)
    val nicepicketgate_left_opened = find("rustic_openfencegate_l", 2396022236101395815)
    val nicepicketgate_right_opened = find("rustic_openfencegate_r", 2396022236101395816)
    val farmerfred_gate_left_closed = find("qip_sheep_shearer_fencegate_l", 8381220604490243565)
    val farmerfred_gate_right_closed = find("qip_sheep_shearer_fencegate_r", 8381220604490243566)
    val farmerfred_gate_left_opened = find("qip_sheep_shearer_openfencegate_l", 2396022236101384885)
    val farmerfred_gate_right_opened =
        find("qip_sheep_shearer_openfencegate_r", 2395638821475390324)
}

internal object GateLocEditor : LocEditor() {
    init {
        edit("fencegate_l") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = gate_locs.picketgate_left_opened
            contentGroup = content.closed_left_picketgate
        }

        edit("fencegate_r") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = gate_locs.picketgate_right_opened
            contentGroup = content.closed_right_picketgate
        }

        edit("openfencegate_l") {
            param[params.closesound] = synths.picketgate_close
            param[params.next_loc_stage] = gate_locs.picketgate_left_closed
            contentGroup = content.opened_left_picketgate
        }

        edit("openfencegate_r") {
            param[params.closesound] = synths.picketgate_close
            param[params.next_loc_stage] = gate_locs.picketgate_right_closed
            contentGroup = content.opened_right_picketgate
        }

        edit("rustic_fencegate_l") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = gate_locs.nicepicketgate_left_opened
            contentGroup = content.closed_left_picketgate
        }

        edit("rustic_fencegate_r") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = gate_locs.nicepicketgate_right_opened
            contentGroup = content.closed_right_picketgate
        }

        edit("rustic_openfencegate_l") {
            param[params.closesound] = synths.door_close
            param[params.next_loc_stage] = gate_locs.nicepicketgate_left_closed
            contentGroup = content.opened_left_picketgate
        }

        edit("rustic_openfencegate_r") {
            param[params.closesound] = synths.door_close
            param[params.next_loc_stage] = gate_locs.nicepicketgate_right_closed
            contentGroup = content.opened_right_picketgate
        }

        edit("qip_sheep_shearer_fencegate_l") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = gate_locs.farmerfred_gate_left_opened
            contentGroup = content.closed_left_picketgate
        }

        edit("qip_sheep_shearer_fencegate_r") {
            param[params.opensound] = synths.picketgate_open
            param[params.next_loc_stage] = gate_locs.farmerfred_gate_right_opened
            contentGroup = content.closed_right_picketgate
        }

        edit("qip_sheep_shearer_openfencegate_l") {
            param[params.closesound] = synths.door_close
            param[params.next_loc_stage] = gate_locs.farmerfred_gate_left_closed
            contentGroup = content.opened_left_picketgate
        }

        edit("qip_sheep_shearer_openfencegate_r") {
            param[params.closesound] = synths.door_close
            param[params.next_loc_stage] = gate_locs.farmerfred_gate_right_closed
            contentGroup = content.opened_right_picketgate
        }
    }
}
