@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.shops.config.ShopParams
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.map.CoordGrid

object LumbridgeNpcs : NpcReferences() {
    val shop_keeper = find(4750494125510652894)
    val shop_assistant = find(7175769830569298007)
    val gee = find(4562549981157667997)
    val donie = find(8328767713494932046)
    val hans = find(3939300316871989450)
    val bartender = find(325196120220401239)
    val arthur_the_clue_hunter = find(6533328399600640885)
    val prayer_tutor = find(8388572464855062601)
    val hatius_cosaintus = find(3864914163306269512)
    val bob = find(8634932672814888402)
}

internal object LumbridgeNpcEditor : NpcEditor() {
    init {
        edit("prayer_tutor") { moveRestrict = indoors }

        edit("father_aereck") { moveRestrict = indoors }

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

        edit("bob_id_10619") {
            moveRestrict = indoors
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }
    }
}
