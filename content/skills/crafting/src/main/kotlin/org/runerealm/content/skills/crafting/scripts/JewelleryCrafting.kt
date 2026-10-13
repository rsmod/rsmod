package org.runerealm.content.skills.crafting.scripts

import jakarta.inject.Inject
import java.util.WeakHashMap
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onIfModalButton
import org.runerealm.api.script.onOpLocU
import org.runerealm.content.skills.crafting.configs.CraftRecipe
import org.runerealm.content.skills.crafting.configs.CraftingGoldComponents
import org.runerealm.content.skills.crafting.configs.CraftingInterfaces
import org.runerealm.content.skills.crafting.configs.CraftingRecipes
import org.runerealm.content.skills.crafting.configs.SilverCraftingComponents
import org.runerealm.content.skills.crafting.configs.crafting_objs
import org.runerealm.game.entity.Player
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Gold and silver jewellery, per the OSRS Wiki
 * ([Jewellery](https://oldschool.runescape.wiki/w/Jewellery)): using a gold bar (or a jewellery
 * mould) on a furnace opens the `crafting_gold` interface, and a silver bar opens
 * `silver_crafting`. Both interfaces draw their own items and mould requirements through their
 * client scripts; the server only handles the quantity buttons and the item clicks.
 */
class JewelleryCrafting
@Inject
constructor(private val locTypes: LocTypeList, private val production: CraftingProduction) :
    PluginScript() {
    /** The quantity selected on the jewellery interfaces, per player. */
    private val quantities = WeakHashMap<Player, Int>()

    override fun ScriptContext.startup() {
        for (furnace in CraftingLocs.furnaces(locTypes)) {
            onOpLocU(furnace, objs.gold_bar) { openGold() }
            onOpLocU(furnace, objs.silver_bar) { openSilver() }
            for (mould in GOLD_MOULDS) {
                onOpLocU(furnace, mould) { openForMould() }
            }
            for (mould in SILVER_ONLY_MOULDS) {
                onOpLocU(furnace, mould) { openSilver() }
            }
        }

        for ((button, recipe) in CraftingRecipes.goldJewellery) {
            onIfModalButton(button) { make(recipe) }
        }
        for ((button, recipe) in CraftingRecipes.silverCrafting) {
            onIfModalButton(button) { make(recipe) }
        }

        registerQuantityButtons(
            CraftingGoldComponents.make_1,
            CraftingGoldComponents.make_5,
            CraftingGoldComponents.make_10,
            CraftingGoldComponents.make_x,
            CraftingGoldComponents.make_all,
        )
        registerQuantityButtons(
            SilverCraftingComponents.make_1,
            SilverCraftingComponents.make_5,
            SilverCraftingComponents.make_10,
            SilverCraftingComponents.make_x,
            SilverCraftingComponents.make_all,
        )
    }

    private fun ScriptContext.registerQuantityButtons(
        one: ComponentType,
        five: ComponentType,
        ten: ComponentType,
        x: ComponentType,
        all: ComponentType,
    ) {
        onIfModalButton(one) { quantities[player] = 1 }
        onIfModalButton(five) { quantities[player] = 5 }
        onIfModalButton(ten) { quantities[player] = 10 }
        onIfModalButton(all) { quantities[player] = ALL }
        onIfModalButton(x) {
            val count = countDialog()
            if (count > 0) {
                quantities[player] = count
            }
        }
    }

    private fun ProtectedAccess.openGold() {
        ifOpenMainModal(CraftingInterfaces.crafting_gold)
    }

    private fun ProtectedAccess.openSilver() {
        ifOpenMainModal(CraftingInterfaces.silver_crafting)
    }

    private fun ProtectedAccess.openForMould() {
        when {
            objs.gold_bar in inv -> openGold()
            objs.silver_bar in inv -> openSilver()
            else -> mes("You need a gold or silver bar to make jewellery.")
        }
    }

    private fun ProtectedAccess.make(recipe: CraftRecipe) {
        ifClose()
        val quantity = quantities[player] ?: 1
        production.queue(this, recipe, quantity)
    }

    private companion object {
        val objs = crafting_objs

        const val ALL = Int.MAX_VALUE

        val GOLD_MOULDS =
            listOf(objs.ring_mould, objs.necklace_mould, objs.amulet_mould, objs.bracelet_mould)

        val SILVER_ONLY_MOULDS =
            listOf(
                objs.holy_mould,
                objs.unholy_mould,
                objs.sickle_mould,
                objs.bolt_mould,
                objs.tiara_mould,
            )
    }
}
