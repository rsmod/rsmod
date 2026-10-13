package org.runerealm.content.skills.crafting.scripts

import jakarta.inject.Inject
import org.runerealm.api.script.onOpHeldU
import org.runerealm.api.script.onPlayerQueueWithArgs
import org.runerealm.content.skills.crafting.configs.CraftingQueues
import org.runerealm.content.skills.crafting.configs.CraftingRecipes
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Crafting with inventory objs, per the OSRS Wiki
 * ([Crafting](https://oldschool.runescape.wiki/w/Crafting)):
 * - A needle (and thread) on leather, hard leather, snakeskin and dragon leather.
 * - Steel studs on leather bodies and chaps.
 * - A chisel on uncut gems (semi-precious gems may be crushed) and amethyst.
 * - A ball of wool on unstrung amulets.
 * - A glassblowing pipe on molten glass.
 * - Elemental orbs on battlestaves.
 * - Water on clay to make soft clay.
 *
 * Also runs the shared production queue used by every Crafting script.
 */
class Crafting @Inject constructor(private val production: CraftingProduction) : PluginScript() {
    override fun ScriptContext.startup() {
        for (combo in CraftingRecipes.combos) {
            onOpHeldU(combo.first, combo.second) { production.start(this, combo.options, TITLE) }
        }
        onPlayerQueueWithArgs<CraftingProduction.CraftTask>(CraftingQueues.craft) {
            production.craft(this, it.args)
        }
    }

    private companion object {
        const val TITLE = "What would you like to make?"
    }
}
