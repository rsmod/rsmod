@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.map.CoordGrid

object LumbridgeNpcs : NpcReferences() {
    val shop_keeper = find(1094911069973142126)
    val shop_assistant = find(368508977701121127)
    val gee = find(6698040310459523799)
    val hans = find(6791077204654367342)
    val bartender = find(5628536812085541378)
    val arthur_the_clue_hunter = find(2308854141696014193)
}

internal object LumbridgeNpcEditor : NpcEditor() {
    init {
        edit("hans") {
            defaultMode = patrol
            patrol1 = patrol(CoordGrid(0, 50, 50, 7, 33), 0)
            patrol2 = patrol(CoordGrid(0, 50, 50, 11, 30), 0)
            patrol3 = patrol(CoordGrid(0, 50, 50, 19, 30), 0)
            patrol4 = patrol(CoordGrid(0, 50, 50, 19, 22), 10)
            patrol5 = patrol(CoordGrid(0, 50, 50, 21, 22), 0)
            patrol6 = patrol(CoordGrid(0, 50, 50, 21, 12), 0)
            patrol7 = patrol(CoordGrid(0, 50, 50, 18, 9), 0)
            patrol8 = patrol(CoordGrid(0, 50, 50, 14, 5), 0)
            patrol9 = patrol(CoordGrid(0, 50, 50, 2, 5), 0)
            patrol10 = patrol(CoordGrid(0, 50, 50, 2, 32), 0)
            maxRange = 40
        }

        edit("cook_id_4626") { moveRestrict = indoors }

        edit("perdu_multi_id_7457") {
            respawnDir = west
            wanderRange = 0
        }

        edit("lumbridge_guide_id_306") {
            respawnDir = west
            wanderRange = 0
        }

        edit("doomsayer_multi") {
            respawnDir = east
            wanderRange = 0
        }

        edit("abigaila") {
            respawnDir = south
            wanderRange = 0
        }

        edit("count_check_id_7414") {
            respawnDir = east
            wanderRange = 0
        }

        edit("arthur_the_clue_hunter") {
            respawnDir = north
            wanderRange = 0
        }

        edit("bartender_id_7546") {
            respawnDir = west
            wanderRange = 0
        }

        edit("smithing_apprentice") { moveRestrict = indoors }

        edit("veos_multi_id_8632") {
            respawnDir = south
            wanderRange = 0
        }

        edit("adventurer_jon_multi") {
            respawnDir = south
            wanderRange = 0
        }

        edit("hewey_multi") {
            respawnDir = east
            wanderRange = 0
        }

        edit("fishing_tutor") {
            respawnDir = east
            wanderRange = 0
        }

        edit("bob_id_10619") { moveRestrict = indoors }
    }
}
