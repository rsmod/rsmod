package org.rsmod.content.generic.locs.pickables

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.type.loc.LocType

internal typealias pickable_locs = PickableLocs

object PickableLocs : LocReferences() {
    val cabbage = find("cabbage", 1496620632455889241)
    val onion = find("onion", 3040472638361972606)
    val potato = find("potato", 6619139296550516438)
    val fai_varrock_wheat = find("fai_varrock_wheat", 7022213377670558384)
    val fai_varrock_wheat_corner = find("fai_varrock_wheat_corner", 7022213377670558385)
    val fai_varrock_wheat_small = find("fai_varrock_wheat_small", 7022213377670558386)
}

internal object PickableLocEditor : LocEditor() {
    init {
        edit(pickable_locs.cabbage) {
            param[params.game_message] = "You pick a cabbage."
            param[params.game_message2] = "You don't have room for this cabbage."
            param[params.rewarditem] = objs.cabbage
            param[params.respawn_time] = 75
        }

        edit(pickable_locs.onion) {
            contentGroup = content.pickable_crop
            param[params.game_message] = "You pick an onion."
            param[params.game_message2] = "You don't have room for this onion."
            param[params.rewarditem] = objs.onion
            param[params.respawn_time] = 50
        }

        edit(pickable_locs.potato) {
            contentGroup = content.pickable_crop
            param[params.game_message] = "You pick a potato."
            param[params.game_message2] = "You don't have room for this potato."
            param[params.rewarditem] = objs.potato
            param[params.respawn_time] = 50
        }

        grain(pickable_locs.fai_varrock_wheat)
        grain(pickable_locs.fai_varrock_wheat_corner)
        grain(pickable_locs.fai_varrock_wheat_small)
    }

    private fun grain(type: LocType) {
        edit(type) {
            contentGroup = content.pickable_crop
            param[params.game_message] = "You pick some grain."
            param[params.game_message2] = "You can't carry any more grain."
            param[params.rewarditem] = objs.grain
            param[params.respawn_time] = 20
        }
    }
}
