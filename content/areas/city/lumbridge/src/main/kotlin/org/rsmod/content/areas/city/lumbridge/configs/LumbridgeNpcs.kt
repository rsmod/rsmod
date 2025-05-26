@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.shops.config.ShopParams
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.map.CoordGrid

typealias lumbridge_npcs = LumbridgeNpcs

object LumbridgeNpcs : NpcReferences() {
    val barfy_bill = find("canoeing_bill", 3103331168324788094)
    val banker = find("deadman_banker_blue_south", 1536012232472220879)
    val banker_tutor = find("aide_tutor_banker", 5183772333668041643)
    val shop_keeper = find("generalshopkeeper1", 2140424768095690499)
    val shop_assistant = find("generalassistant1", 2185693774375842236)
    val gee = find("lumbridge_guide2_man", 814251163147109342)
    val donie = find("lumbridge_guide2_woman", 3864107068297254351)
    val hans = find("hans", 6542439647165622063)
    val bartender = find("ram_bartender", 3782636636485213848)
    val arthur_the_clue_hunter = find("aide_tutor_clues", 2398692310679668222)
    val prayer_tutor = find("aide_tutor_prayer", 1945582437385617574)
    val hatius_lumbridge_diary = find("hatius_lumbridge_diary", 7638020467539300041)
    val bob = find("bob", 852523917703846821)
    val woodsman_tutor = find("aide_tutor_woodsman", 7317297890904607119)
    val smithing_apprentice = find("aide_tutor_smithing_apprentice", 4852549530127422344)
    val father_aereck = find("father_aereck", 1703535979713856985)
    val cook = find("cook", 817286823331370240)
    val perdu = find("lost_property_merchant_standard", 4542005190366269979)
    val guide = find("lumbridge_guide", 6588888450883888298)
    val doomsayer = find("cws_doomsayer", 3139264798199587220)
    val abigaila = find("tob_spectator_misthalin", 1602297745389622578)
    val count_check = find("count_check", 8201562267894359108)
    val veos = find("veos_lumbridge", 3697975897058386361)
    val adventurer_jon = find("ap_guide_parent", 8049664089518232275)
    val hewey = find("mistmyst_hewey", 7376540171675674022)
    val fishing_tutor = find("aide_tutor_fishing", 4335984535090215880)
    val millie = find("millie_the_miller", 89073509135537075)
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
