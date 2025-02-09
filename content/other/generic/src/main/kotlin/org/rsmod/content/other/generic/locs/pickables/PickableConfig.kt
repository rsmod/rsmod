package org.rsmod.content.other.generic.locs.pickables

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences

internal typealias pickable_locs = PickableLocs

object PickableLocs : LocReferences() {
    init {
        verify("crop_onion", 3040472638361972606)
        verify("crop_potato", 6619139296550516438)
        verify("crop_grain_15506", 7022213377670558384)
        verify("crop_grain_15507", 7022213377670558385)
        verify("crop_grain_15508", 7022213377670558386)
    }

    val cabbage = find("crop_cabbage", 1496620632455889241)
}

internal object PickableLocEditor : LocEditor() {
    init {
        // Cabbage is handled explicitly as it randomly gives cabbage seed.
        edit("crop_cabbage") {
            param[params.game_message] = "You pick a cabbage."
            param[params.game_message2] = "You don't have room for this cabbage."
            param[params.respawn_time] = 75
        }

        edit("crop_onion") {
            contentGroup = content.pickable_crop
            param[params.game_message] = "You pick an onion."
            param[params.game_message2] = "You don't have room for this onion."
            param[params.rewarditem] = objs.onion
            param[params.respawn_time] = 50
        }

        edit("crop_potato") {
            contentGroup = content.pickable_crop
            param[params.game_message] = "You pick a potato."
            param[params.game_message2] = "You don't have room for this potato."
            param[params.rewarditem] = objs.potato
            param[params.respawn_time] = 50
        }

        grain("crop_grain_15506")
        grain("crop_grain_15507")
        grain("crop_grain_15508")
    }

    private fun grain(internal: String) {
        edit(internal) {
            contentGroup = content.pickable_crop
            param[params.game_message] = "You pick some grain."
            param[params.game_message2] = "You can't carry any more grain."
            param[params.rewarditem] = objs.grain
            param[params.respawn_time] = 20
        }
    }
}
