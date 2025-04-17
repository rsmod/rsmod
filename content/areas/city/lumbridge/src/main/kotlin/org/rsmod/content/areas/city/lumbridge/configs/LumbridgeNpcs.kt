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
    val father_aereck = find("father_aereck", 2458602357142569756)
    val cook = find("cook", 3737636038939424319)
    val perdu = find("lost_property_merchant_standard", 361155506698743338)
    val guide = find("lumbridge_guide", 5317197919161804353)
    val doomsayer = find("cws_doomsayer", 7027711953079293399)
    val abigaila = find("tob_spectator_misthalin", 5506442100218713377)
    val count_check = find("count_check", 2233208351397563395)
    val veos = find("veos_lumbridge", 4215600836046426988)
    val adventurer_jon = find("ap_guide_parent", 2190791507308492530)
    val hewey = find("mistmyst_hewey", 7247092703186427097)
    val fishing_tutor = find("aide_tutor_fishing", 6240639608569249075)
    val millie = find("millie_the_miller", 5433484057267480234)
}

internal object LumbridgeNpcEditor : NpcEditor() {
    init {
        edit(lumbridge_npcs.shop_keeper) { moveRestrict = indoors }

        edit(lumbridge_npcs.shop_assistant) { moveRestrict = indoors }

        edit(lumbridge_npcs.banker) { contentGroup = content.banker }

        edit(lumbridge_npcs.banker_tutor) { contentGroup = content.banker_tutor }

        edit(lumbridge_npcs.prayer_tutor) { moveRestrict = indoors }

        edit(lumbridge_npcs.father_aereck) { moveRestrict = indoors }

        edit(lumbridge_npcs.hans) {
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

        edit(lumbridge_npcs.cook) { moveRestrict = indoors }

        edit(lumbridge_npcs.perdu) {
            respawnDir = west
            wanderRange = 0
        }

        edit(lumbridge_npcs.guide) {
            respawnDir = west
            wanderRange = 0
        }

        edit(lumbridge_npcs.doomsayer) {
            respawnDir = east
            wanderRange = 0
        }

        edit(lumbridge_npcs.abigaila) {
            respawnDir = south
            wanderRange = 0
        }

        edit(lumbridge_npcs.count_check) {
            respawnDir = east
            wanderRange = 0
        }

        edit(lumbridge_npcs.arthur_the_clue_hunter) {
            respawnDir = north
            wanderRange = 0
            timer = 20
        }

        edit(lumbridge_npcs.bartender) {
            respawnDir = west
            wanderRange = 0
        }

        edit(lumbridge_npcs.smithing_apprentice) { moveRestrict = indoors }

        edit(lumbridge_npcs.veos) {
            respawnDir = south
            wanderRange = 0
        }

        edit(lumbridge_npcs.adventurer_jon) {
            respawnDir = south
            wanderRange = 0
        }

        edit(lumbridge_npcs.hewey) {
            respawnDir = east
            wanderRange = 0
        }

        edit(lumbridge_npcs.fishing_tutor) {
            respawnDir = east
            wanderRange = 0
        }

        edit(lumbridge_npcs.bob) {
            moveRestrict = indoors
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_buy_percentage] = 600
            param[ShopParams.shop_change_percentage] = 20
        }

        edit(lumbridge_npcs.millie) { wanderRange = 1 }
    }
}
