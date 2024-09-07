package org.rsmod.content.other.generic.doors

import org.rsmod.api.config.refs.BaseContent
import org.rsmod.api.config.refs.BaseParams
import org.rsmod.api.config.refs.synths
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.type.util.ParamMapBuilder

internal object DoorConstants {
    /** The (cycle) duration that a door remains changed before reverting to its original state. */
    const val DURATION = 500
}

internal object DoorLocs : LocReferences() {
    val door_opened = find(6709816175547985597)
    val door_closed = find(2872035173077332878)
    val nicedoor_closed = find(2872035173077332883)
    val nicedoor_opened = find(6709816175547985602)
    val door_left_closed = find(2162988469587025116)
    val door_left_opened = find(6000769472057677835)
    val door_right_closed = find(2162988469587025119)
    val door_right_opened = find(6000769472057677838)
}

internal object DoorLocEdits : LocEditor() {
    init {
        edit("door_opened") {
            val param = ParamMapBuilder()
            param[BaseParams.next_loc_stage] = DoorLocs.door_closed
            this.paramMap = param.toParamMap()
            contentType = BaseContent.opened_single_door.id
        }

        edit("door_closed") {
            val param = ParamMapBuilder()
            param[BaseParams.next_loc_stage] = DoorLocs.door_opened
            this.paramMap = param.toParamMap()
            contentType = BaseContent.closed_single_door.id
        }

        edit("nicedoor_opened") {
            val param = ParamMapBuilder()
            param[BaseParams.next_loc_stage] = DoorLocs.nicedoor_closed
            param[BaseParams.closesound] = synths.nicedoor_close
            this.paramMap = param.toParamMap()
            contentType = BaseContent.opened_single_door.id
        }

        edit("nicedoor_closed") {
            val param = ParamMapBuilder()
            param[BaseParams.next_loc_stage] = DoorLocs.nicedoor_opened
            param[BaseParams.opensound] = synths.nicedoor_open
            this.paramMap = param.toParamMap()
            contentType = BaseContent.closed_single_door.id
        }

        edit("door_left_closed") {
            val param = ParamMapBuilder()
            param[BaseParams.next_loc_stage] = DoorLocs.door_left_opened
            this.paramMap = param.toParamMap()
            contentType = BaseContent.closed_left_door.id
        }

        edit("door_left_opened") {
            val param = ParamMapBuilder()
            param[BaseParams.next_loc_stage] = DoorLocs.door_left_closed
            this.paramMap = param.toParamMap()
            contentType = BaseContent.opened_left_door.id
        }

        edit("door_right_closed") {
            val param = ParamMapBuilder()
            param[BaseParams.next_loc_stage] = DoorLocs.door_right_opened
            this.paramMap = param.toParamMap()
            contentType = BaseContent.closed_right_door.id
        }

        edit("door_right_opened") {
            val param = ParamMapBuilder()
            param[BaseParams.next_loc_stage] = DoorLocs.door_right_closed
            this.paramMap = param.toParamMap()
            contentType = BaseContent.opened_right_door.id
        }
    }
}
