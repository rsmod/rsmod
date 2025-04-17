package org.rsmod.api.config.editors

import org.rsmod.api.type.editors.npc.NpcEditor

private val shopKeepers = setOf("generalshopkeeper1", "generalassistant1")
private val fishingSpots = setOf("0_50_50_freshfish", "0_50_49_saltfish")
private val imps = setOf("imp")

internal object NpcEdits : NpcEditor() {
    init {
        fishingSpots.forEach(::fishingSpot)
        imps.forEach(::imp)
        shopKeepers.forEach(::shopKeeper)

        edit("farming_tools_leprechaun") {
            respawnDir = south
            wanderRange = 0
        }
    }
}

private fun NpcEditor.fishingSpot(internal: String) =
    edit(internal) {
        moveRestrict = nomove
        wanderRange = 0
    }

private fun NpcEditor.shopKeeper(internal: String) = edit(internal) { moveRestrict = indoors }

private fun NpcEditor.imp(internal: String) = edit(internal) { giveChase = false }
