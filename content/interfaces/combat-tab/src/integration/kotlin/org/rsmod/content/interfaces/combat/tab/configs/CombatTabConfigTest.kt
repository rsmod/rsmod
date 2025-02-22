package org.rsmod.content.interfaces.combat.tab.configs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState
import org.rsmod.game.type.obj.WeaponCategory

class CombatTabConfigTest {
    @Test
    fun GameTestState.`ensure all weapon categories have a style-save varbit`() = runBasicGameTest {
        val enum = cacheTypes.enums[combat_enums.weapons_last_style]
        val missing = WeaponCategory.entries.filter { it.id !in enum.keys }
        assertEquals(emptyList<WeaponCategory>(), missing) {
            "Found missing weapon categories in `weapons_last_style` enum:"
        }
    }
}
