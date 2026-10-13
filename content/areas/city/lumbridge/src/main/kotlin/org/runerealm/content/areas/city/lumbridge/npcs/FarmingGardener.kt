package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.shops.Shops
import org.runerealm.api.shops.config.ShopParams
import org.runerealm.api.type.editors.inv.InvEditor
import org.runerealm.api.type.editors.npc.NpcEditor
import org.runerealm.api.type.refs.inv.InvReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.type.inv.InvScope
import org.runerealm.game.type.inv.InvStackType
import org.runerealm.game.type.npc.NpcType

/**
 * Shared behaviour for the Group of Advanced Gardeners members around Lumbridge (Fayeth and
 * Vasquen). Farming is not implemented, so every patch is treated as having nothing planted.
 */
internal object FarmingGardener {
    fun Player.openFarmingSupplies(shops: Shops, npc: Npc) {
        shops.open(this, npc, "Farming Supplies", FarmingGardenerInvs.farmer_supplies)
    }

    /**
     * Standard gardener dialogue. [notPlanted] is the gardener's response when no crop is planted
     * in their patch.
     */
    suspend fun Dialogue.gardenerDialogue(shops: Shops, npc: Npc, notPlanted: String) {
        // TODO(content): "Would you chop my tree down for me?" when a planted tree has matured.
        while (true) {
            val choice =
                choice4(
                    "Would you look after my crops for me?",
                    1,
                    "Can you give me any farming advice?",
                    2,
                    "Can you sell me something?",
                    3,
                    "I'll come back another time.",
                    4,
                    title = "Select an option",
                )
            when (choice) {
                1 -> {
                    chatPlayer(quiz, "Would you look after my crops for me?")
                    chatNpc(neutral, notPlanted)
                    return
                }
                2 -> {
                    chatPlayer(quiz, "Can you give me any farming advice?")
                    chatNpc(neutral, farmingAdvice.random())
                }
                3 -> {
                    chatNpc(
                        neutral,
                        "That depends on whether I have it to sell. What is it that you're " +
                            "looking for?",
                    )
                    player.openFarmingSupplies(shops, npc)
                    return
                }
                4 -> {
                    chatPlayer(neutral, "I'll come back another time.")
                    return
                }
            }
        }
    }

    private val farmingAdvice =
        listOf(
            "The only way to cure a bush or tree of disease is to prune away the diseased " +
                "leaves with a pair of secateurs. For all other crops I would just apply " +
                "some plant-cure.",
            "Don't just throw away your weeds after you've raked a patch - put them in a " +
                "compost bin and make some compost.",
            "There are many Farming areas out there. I know of Elstan's area near Falador, " +
                "Dantaera's area near Catherby, Kragen's area near Ardougne, Lyra's area in " +
                "Morytania, Marisi's area in Hosidius and Alan's area in the Farming Guild. " +
                "That said, I'm sure there's plenty more that I don't know about.",
            "Supercompost is far better than normal compost, but more expensive to make. You " +
                "need to rot the right type of item; show me and item, and I'll tell you if " +
                "it's super-compostable or not.",
            "There is a special patch for growing Belladonna - I believe that it is " +
                "somewhere near Draynor Manor, where the ground is a tad 'unblessed'.",
            "If you want to grow fruit trees you could try a few places: Catherby, " +
                "Brimhaven, Gnome Stronghold and the Farming Guild all have fruit tree " +
                "patches.",
            "You don't have tp buy all your plantpots you know, you can make them yourself " +
                "on a pottery wheel. If you're good enough at crafting, that is.",
            "You can put up to five tomatoes, strawberries, apples, bananas or oranges into " +
                "a fruit basket, although you can't have a mix in the same basket.",
            "Hops are good for brewing ales. I believe there's a brewery up in Keldagrim " +
                "somewhere, and I've heard rumours that a place called Phasmatys used to be " +
                "good for that type of thing. 'Fore they all died, of course.",
            "Applying compost to a patch will not only reduce the chance that your crops " +
                "will get diseased, but you will also grow more crops to harvest.",
            "You can put up to ten potatoes, cabbages or onions in vegetable sacks, although " +
                "you can't have a mix in the same sack.",
            "You can fill plantpots with soil from any empty patch, if you have a gardening " +
                "trowel.",
            "Vegetables, hops and flowers need constant watering - if you ignore my advice, " +
                "you will sooner or later find yourself in possession of a dead farming " +
                "patch.",
            "You can buy all the farming tools from farming shops, which can be found close " +
                "to the allotments.",
            "If you want to make your own sacks and baskets you'll need to use the loom " +
                "that's near the Farming shop in Falador. If you're good enough at crafting, " +
                "that is.",
            "Bittercap mushrooms can only be grown in a special patch in Morytania, near the " +
                "Mort Myre swamp. There the ground is especially dank and suited to growing " +
                "poisonous fungii.",
            "Tree seeds must be grown in a plantpot of soil into a sapling, and then " +
                "transferred to a tree patch to continue growing to adulthood.",
        )
}

internal object FarmingGardenerInvs : InvReferences() {
    val farmer_supplies = find("farmer_supplies")
}

internal object FarmingGardenerObjs : ObjReferences() {
    val plant_cure = find("plant_cure")
    val bucket_compost = find("bucket_compost")
    val rake = find("rake")
    val plantpot_empty = find("plantpot_empty")
    val watering_can_8 = find("watering_can_8")
    val gardening_trowel = find("gardening_trowel")
    val dibber = find("dibber")
}

internal object FarmingGardenerInvEditor : InvEditor() {
    init {
        // Stock is unlimited in OSRS; a large restocking count approximates that.
        edit(FarmingGardenerInvs.farmer_supplies) {
            scope = InvScope.Shared
            stack = InvStackType.Always
            autoSize = true
            restock = true
            stock += stock(FarmingGardenerObjs.plant_cure, count = 1000, restockCycles = 1)
            stock += stock(FarmingGardenerObjs.bucket_compost, count = 1000, restockCycles = 1)
            stock += stock(FarmingGardenerObjs.rake, count = 1000, restockCycles = 1)
            stock += stock(FarmingGardenerObjs.plantpot_empty, count = 1000, restockCycles = 1)
            stock += stock(FarmingGardenerObjs.watering_can_8, count = 1000, restockCycles = 1)
            stock += stock(FarmingGardenerObjs.gardening_trowel, count = 1000, restockCycles = 1)
            stock += stock(FarmingGardenerObjs.dibber, count = 1000, restockCycles = 1)
        }
    }
}

internal object FarmingGardenerNpcEditor : NpcEditor() {
    init {
        gardener(lumbridge_npcs.fayeth)
        gardener(lumbridge_npcs.vasquen)
    }

    private fun gardener(type: NpcType) {
        edit(type) {
            // Farming Supplies sells at item value regardless of stock.
            param[ShopParams.shop_sell_percentage] = 1000
            param[ShopParams.shop_change_percentage] = 0
        }
    }
}
