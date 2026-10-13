package org.runerealm.content.interfaces.settings.scripts

import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.script.onIfOpen
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The spellbook's "Filters" menu: each row toggles whether a group of spells is shown. Row order
 * follows the client's `magic_spellbook_filtertoggle_createindicator` calls.
 */
class SpellbookFilterScript : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOpen(interfaces.magic_spellbook) {
            player.ifSetEvents(
                spellbook_filter_components.filtermenu,
                0..<FILTER_COUNT,
                IfEvent.Op1,
            )
        }
        onIfOverlayButton(spellbook_filter_components.filtermenu) { player.toggleFilter(comsub) }
    }

    private fun Player.toggleFilter(row: Int) {
        when (row) {
            0 -> blockCombat = !blockCombat
            1 -> blockTeleport = !blockTeleport
            2 -> blockUtility = !blockUtility
            3 -> blockLackLevel = !blockLackLevel
            4 -> blockLackRunes = !blockLackRunes
            5 -> blockLocked = !blockLocked
            6 -> blockResizing = !blockResizing
        }
    }

    private companion object {
        const val FILTER_COUNT = 7
    }
}

private var Player.blockCombat by boolVarBit(spellbook_filter_varbits.block_combat)
private var Player.blockTeleport by boolVarBit(spellbook_filter_varbits.block_teleport)
private var Player.blockUtility by boolVarBit(spellbook_filter_varbits.block_utility)
private var Player.blockLackLevel by boolVarBit(spellbook_filter_varbits.block_lack_level)
private var Player.blockLackRunes by boolVarBit(spellbook_filter_varbits.block_lack_runes)
private var Player.blockLocked by boolVarBit(spellbook_filter_varbits.block_locked)
private var Player.blockResizing by boolVarBit(spellbook_filter_varbits.block_resizing)

typealias spellbook_filter_components = SpellbookFilterComponents

typealias spellbook_filter_varbits = SpellbookFilterVarBits

object SpellbookFilterComponents : ComponentReferences() {
    val filtermenu = find("magic_spellbook:filtermenu")
}

object SpellbookFilterVarBits : VarBitReferences() {
    val block_combat = find("magic_filter_blockcombat")
    val block_teleport = find("magic_filter_blockteleport")
    val block_utility = find("magic_filter_blockutility")
    val block_lack_level = find("magic_filter_blocklacklevel")
    val block_lack_runes = find("magic_filter_blocklackrunes")
    val block_locked = find("magic_filter_blocklocked")
    val block_resizing = find("magic_spellbook_blockresizing")
}
