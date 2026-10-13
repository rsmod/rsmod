package org.runerealm.content.interfaces.settings

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.interfaces.settings.scripts.SpellbookFilterScript
import org.runerealm.content.interfaces.settings.scripts.spellbook_filter_components
import org.runerealm.content.interfaces.settings.scripts.spellbook_filter_varbits
import org.runerealm.game.entity.Player

class SpellbookFilterTest {
    @Test
    fun GameTestState.`filter rows toggle their varbit`() =
        runGameTest(SpellbookFilterScript::class) {
            player.ifOpenOverlay(interfaces.magic_spellbook, components.toplevel_target_side6)
            assertEquals(0, player.blockCombat)

            player.ifButton(spellbook_filter_components.filtermenu, comsub = 0)
            advance()
            assertEquals(1, player.blockCombat)

            player.ifButton(spellbook_filter_components.filtermenu, comsub = 0)
            advance()
            assertEquals(0, player.blockCombat)
        }
}

private val Player.blockCombat by intVarBit(spellbook_filter_varbits.block_combat)
