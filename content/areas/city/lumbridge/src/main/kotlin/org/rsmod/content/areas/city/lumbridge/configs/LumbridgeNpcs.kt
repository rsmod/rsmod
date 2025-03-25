@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.shops.config.ShopParams
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.map.CoordGrid

typealias lumbridge_npcs = LumbridgeNpcs

object LumbridgeNpcs : NpcReferences() {
    val barfy_bill = find("barfy_bill", 4835760530716474293)
    val banker = find("banker_multi_id_6520", 1463025812257320618)
    val banker_tutor = find("banker_tutor", 2615463100687965410)
    val shop_keeper = find("shop_keeper_id_2813", 1438702337870086626)
    val shop_assistant = find("shop_assistant_id_2814", 4200111720959342523)
    val gee = find("gee", 3552460767699378141)
    val donie = find("donie", 5126230244763061230)
    val hans = find("hans", 2483823770976949698)
    val bartender = find("bartender_id_7546", 156533904228193047)
    val arthur_the_clue_hunter = find("arthur_the_clue_hunter", 7969650398637600801)
    val prayer_tutor = find("prayer_tutor", 8000064238265165217)
    val hatius_cosaintus = find("hatius_cosaintus", 4750646677158177000)
    val bob = find("bob_id_10619", 5887098795660136180)
    val woodsman_tutor = find("woodsman_tutor", 3633313576151598194)
    val smithing_apprentice = find("smithing_apprentice", 857616158419739967)
}

internal object LumbridgeNpcEditor : NpcEditor() {
    init {
        edit("banker_multi_id_6520") { contentGroup = content.banker }

        edit("banker_tutor") { contentGroup = content.banker_tutor }

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
            timer = 20
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

        edit("millie_miller") { wanderRange = 1 }
    }
}
