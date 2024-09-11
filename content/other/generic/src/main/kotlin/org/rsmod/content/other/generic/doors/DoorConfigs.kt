package org.rsmod.content.other.generic.doors

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal object DoorConstants {
    /** The (cycle) duration that a door remains changed before reverting to its original state. */
    const val DURATION = 500
}

internal object DoorLocs : LocReferences() {
    val door_opened = find(5065226005806317467)
    val door_closed = find(1827052337340400340)
    val nicedoor_closed = find(1827052337340400345)
    val nicedoor_opened = find(5065226005806317472)
    val door_left_closed = find(8682664997836417386)
    val door_left_opened = find(2697466629447558705)
    val door_right_closed = find(8682664997836417389)
    val door_right_opened = find(2697466629447558708)
}

internal object DoorLocEdits : LocEditor() {
    init {
        edit("door_opened") {
            param[params.next_loc_stage] = DoorLocs.door_closed
            contentType = content.opened_single_door
        }

        edit("door_closed") {
            param[params.next_loc_stage] = DoorLocs.door_opened
            contentType = content.closed_single_door
        }

        edit("nicedoor_opened") {
            param[params.next_loc_stage] = DoorLocs.nicedoor_closed
            param[params.closesound] = synths.nicedoor_close
            contentType = content.opened_single_door
        }

        edit("nicedoor_closed") {
            param[params.next_loc_stage] = DoorLocs.nicedoor_opened
            param[params.opensound] = synths.nicedoor_open
            contentType = content.closed_single_door
        }

        edit("door_left_closed") {
            param[params.next_loc_stage] = DoorLocs.door_left_opened
            contentType = content.closed_left_door
        }

        edit("door_left_opened") {
            param[params.next_loc_stage] = DoorLocs.door_left_closed
            contentType = content.opened_left_door
        }

        edit("door_right_closed") {
            param[params.next_loc_stage] = DoorLocs.door_right_opened
            contentType = content.closed_right_door
        }

        edit("door_right_opened") {
            param[params.next_loc_stage] = DoorLocs.door_right_closed
            contentType = content.opened_right_door
        }
    }
}
