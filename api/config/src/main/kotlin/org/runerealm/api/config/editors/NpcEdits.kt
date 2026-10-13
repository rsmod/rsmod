package org.runerealm.api.config.editors

import org.runerealm.api.config.refs.npcs
import org.runerealm.api.type.editors.npc.NpcEditor
import org.runerealm.game.type.npc.NpcType

private val fishingSpots = setOf(npcs.rod_fishing_spot_1527, npcs.fishing_spot_1530)
private val imps = setOf(npcs.imp)

internal object NpcEdits : NpcEditor() {
    init {
        fishingSpots.forEach(::fishingSpot)
        imps.forEach(::imp)

        edit(npcs.farming_tools_leprechaun) {
            respawnDir = south
            wanderRange = 0
        }
    }
}

private fun NpcEditor.fishingSpot(type: NpcType) =
    edit(type) {
        moveRestrict = nomove
        wanderRange = 0
    }

private fun NpcEditor.imp(type: NpcType) = edit(type) { giveChase = false }
