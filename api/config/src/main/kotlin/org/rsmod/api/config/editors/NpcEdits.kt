package org.rsmod.api.config.editors

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor

internal object NpcEdits : NpcEditor() {
    init {
        ducks.forEach(::duck)
        ducklings.forEach(::duckling)
        fishingSpots.forEach(::fishingSpot)
        imps.forEach(::imp)
        shopKeepers.forEach(::shopKeeper)

        edit("tool_leprechaun_id_0") {
            respawnDir = south
            wanderRange = 0
        }
    }
}

private val shopKeepers = setOf("shop_keeper_id_2813", "shop_assistant_id_2814")
private val ducks = setOf("duck_id_1838", "duck_id_1839")
private val ducklings = setOf("ducklings")
private val fishingSpots = setOf("rod_fishing_spot_id_1527", "fishing_spot_id_1530")
private val imps = setOf("imp_id_5007")

private fun NpcEditor.fishingSpot(internal: String) =
    edit(internal) {
        moveRestrict = nomove
        wanderRange = 0
    }

private fun NpcEditor.shopKeeper(internal: String) = edit(internal) { moveRestrict = indoors }

private fun NpcEditor.imp(internal: String) = edit(internal) { giveChase = false }

private fun NpcEditor.duck(internal: String) =
    edit(internal) {
        moveRestrict = blocked
        contentType = content.duck
        timer = 0
    }

private fun NpcEdits.duckling(internal: String) =
    edit(internal) {
        moveRestrict = blocked
        contentType = content.duckling
        timer = 0
    }
