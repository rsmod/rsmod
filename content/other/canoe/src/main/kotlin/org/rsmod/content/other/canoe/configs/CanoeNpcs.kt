package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

typealias canoe_npcs = CanoeNpcs

object CanoeNpcs : NpcReferences() {
    val cave_scenery_1 = find("cave_scenery_id_3337", 6854927461808461122)
    val cave_scenery_2 = find("cave_scenery_id_3338", 6854927461808461123)
    val cave_scenery_3 = find("cave_scenery_id_3339", 2426668308554884033)

    val tree_scenery_1 = find("trees_id_3332", 2735184026226725076)
    val tree_scenery_2 = find("trees_id_3333", 2735184026226725077)
    val bullrush_scenery_1 = find("bullrush_id_3335", 7747612584735530281)
    val bullrush_scenery_2 = find("bullrush_id_3336", 7747612584735530282)
}

internal object CanoeNpcEditor : NpcEditor() {
    init {
        scenery(canoe_npcs.cave_scenery_1)
        scenery(canoe_npcs.cave_scenery_2)
        scenery(canoe_npcs.cave_scenery_3)

        scenery(canoe_npcs.tree_scenery_1)
        scenery(canoe_npcs.tree_scenery_2)
        scenery(canoe_npcs.bullrush_scenery_1)
        scenery(canoe_npcs.bullrush_scenery_2)
    }

    private fun scenery(npc: NpcType) {
        edit(npc.internalNameValue) {
            defaultMode = none
            moveRestrict = passthru
            respawnDir = north
            timer = 1
        }
    }
}
