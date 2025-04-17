package org.rsmod.content.generic.locs.doors

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias door_locs = DoorLocs

internal object DoorConstants {
    /** The (cycle) duration that a door remains changed before reverting to its original state. */
    const val DURATION = 500
}

internal object DoorLocs : LocReferences() {
    val door_opened = find("poordooropen", 5065226005806317467)
    val door_closed = find("poordoor", 1827052337340400340)
    val poshdoor_closed = find("poshdoor", 1827052337340400345)
    val poshdoor_opened = find("poshdooropen", 5065226005806317472)
    val door_left_closed = find("castledoubledoorl", 8682664997836417386)
    val door_left_opened = find("opencastledoubledoorl", 2697466629447558705)
    val door_right_closed = find("castledoubledoorr", 8682664997836417389)
    val door_right_opened = find("opencastledoubledoorr", 2697466629447558708)
}

internal object DoorLocEdits : LocEditor() {
    init {
        edit(door_locs.door_opened) {
            param[params.next_loc_stage] = door_locs.door_closed
            contentGroup = content.opened_single_door
        }

        edit(door_locs.door_closed) {
            param[params.next_loc_stage] = door_locs.door_opened
            contentGroup = content.closed_single_door
        }

        edit(door_locs.poshdoor_opened) {
            param[params.next_loc_stage] = door_locs.poshdoor_closed
            param[params.closesound] = synths.nicedoor_close
            contentGroup = content.opened_single_door
        }

        edit(door_locs.poshdoor_closed) {
            param[params.next_loc_stage] = door_locs.poshdoor_opened
            param[params.opensound] = synths.nicedoor_open
            contentGroup = content.closed_single_door
        }

        edit(door_locs.door_left_closed) {
            param[params.next_loc_stage] = door_locs.door_left_opened
            contentGroup = content.closed_left_door
        }

        edit(door_locs.door_left_opened) {
            param[params.next_loc_stage] = door_locs.door_left_closed
            contentGroup = content.opened_left_door
        }

        edit(door_locs.door_right_closed) {
            param[params.next_loc_stage] = door_locs.door_right_opened
            contentGroup = content.closed_right_door
        }

        edit(door_locs.door_right_opened) {
            param[params.next_loc_stage] = door_locs.door_right_closed
            contentGroup = content.opened_right_door
        }
    }
}
