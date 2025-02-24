package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

typealias canoe_npcs = CanoeNpcs

object CanoeNpcs : NpcReferences() {
    val cave_scenery_1 = find("cave_scenery_id_3337", 3098833207782952902)
    val cave_scenery_2 = find("cave_scenery_id_3338", 3098833207782952903)
    val cave_scenery_3 = find("cave_scenery_id_3339", 452813928103248785)

    val tree_scenery_1 = find("trees_id_3332", 825528632376001212)
    val tree_scenery_2 = find("trees_id_3333", 825528632376001213)
    val bullrush_scenery_1 = find("bullrush_id_3335", 2212393485205516873)
    val bullrush_scenery_2 = find("bullrush_id_3336", 2212393485205516874)
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
