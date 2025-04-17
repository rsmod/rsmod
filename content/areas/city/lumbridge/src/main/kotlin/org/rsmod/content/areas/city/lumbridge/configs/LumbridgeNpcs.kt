@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.shops.config.ShopParams
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.map.CoordGrid

typealias lumbridge_npcs = LumbridgeNpcs

object LumbridgeNpcs : NpcReferences() {
    val barfy_bill = find("canoeing_bill", 4835760530716474293)
    val banker = find("deadman_banker_blue_south", 1463025812257320618)
    val banker_tutor = find("aide_tutor_banker", 2615463100687965410)
    val shop_keeper = find("generalshopkeeper1", 1438702337870086626)
    val shop_assistant = find("generalassistant1", 4200111720959342523)
    val gee = find("lumbridge_guide2_man", 3552460767699378141)
    val donie = find("lumbridge_guide2_woman", 5126230244763061230)
    val hans = find("hans", 2483823770976949698)
    val bartender = find("ram_bartender", 156533904228193047)
    val arthur_the_clue_hunter = find("aide_tutor_clues", 7969650398637600801)
    val prayer_tutor = find("aide_tutor_prayer", 8000064238265165217)
    val hatius_lumbridge_diary = find("hatius_lumbridge_diary", 4750646677158177000)
    val bob = find("bob", 5887098795660136180)
    val woodsman_tutor = find("aide_tutor_woodsman", 3633313576151598194)
    val smithing_apprentice = find("aide_tutor_smithing_apprentice", 857616158419739967)
}

internal object LumbridgeNpcEditor : NpcEditor() {
    init {
        edit("deadman_banker_blue_south") { contentGroup = content.banker }

        edit("aide_tutor_banker") { contentGroup = content.banker_tutor }

        edit("aide_tutor_prayer") { moveRestrict = indoors }

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

        edit("cook") { moveRestrict = indoors }

        edit("lost_property_merchant_standard") {
            respawnDir = west
            wanderRange = 0
        }

        edit("lumbridge_guide") {
            respawnDir = west
            wanderRange = 0
        }

        edit("cws_doomsayer") {
            respawnDir = east
            wanderRange = 0
        }

        edit("tob_spectator_misthalin") {
            respawnDir = south
            wanderRange = 0
        }

        edit("count_check") {
            respawnDir = east
            wanderRange = 0
        }

        edit("aide_tutor_clues") {
            respawnDir = north
            wanderRange = 0
            timer = 20
        }

        edit("ram_bartender") {
            respawnDir = west
            wanderRange = 0
        }

        edit("aide_tutor_smithing_apprentice") { moveRestrict = indoors }

        edit("veos_lumbridge") {
            respawnDir = south
            wanderRange = 0
        }

        edit("ap_guide_parent") {
            respawnDir = south
            wanderRange = 0
        }

        edit("mistmyst_hewey") {
            respawnDir = east
            wanderRange = 0
        }

        edit("aide_tutor_fishing") {
            respawnDir = east
            wanderRange = 0
        }

        edit("bob") {
            moveRestrict = indoors
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }

        edit("millie_the_miller") { wanderRange = 1 }
    }
}
