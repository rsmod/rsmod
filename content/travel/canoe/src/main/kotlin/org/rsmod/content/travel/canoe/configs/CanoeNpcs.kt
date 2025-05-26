package org.rsmod.content.travel.canoe.configs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

typealias canoe_npcs = CanoeNpcs

object CanoeNpcs : NpcReferences() {
    val cave_scenery_1 = find("canoeing_cave_scenery_1", 4799663288595192051)
    val cave_scenery_2 = find("canoeing_cave_scenery_2", 4799663288595192052)
    val cave_scenery_3 = find("canoeing_cave_scenery_3", 6541502719562466358)

    val tree_scenery_1 = find("canoeing_scenery_1", 3673706769028549969)
    val tree_scenery_2 = find("canoeing_scenery_2", 3673706769028549970)
    val bullrush_scenery_1 = find("canoeing_bullrush", 6477528493977644558)
    val bullrush_scenery_2 = find("canoeing_bullrush_leaf", 6477528493977644559)
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
        edit(npc) {
            defaultMode = none
            moveRestrict = passthru
            respawnDir = north
            timer = 1
        }
    }
}
