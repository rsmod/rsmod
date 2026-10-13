package org.runerealm.content.interfaces.prayer.tab

import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.disableProtectionPrayers
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.interfaces.prayer.tab.configs.PrayerTabComponents
import org.runerealm.content.interfaces.prayer.tab.configs.PrayerTabVarBits
import org.runerealm.content.interfaces.prayer.tab.scripts.PrayerTabScript
import org.runerealm.content.interfaces.prayer.tab.scripts.QuickPrayerScript
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.varbit.VarBitType

class ProtectionPrayerLockTest {
    @Test
    fun GameTestState.`protection prayers cannot be activated while disabled`() =
        runPrayerTest { deps ->
            setUpPrayer()
            val protectFromMelee = deps.component(varbits.protect_from_melee)
            val thickSkin = deps.component(varbits.thick_skin)

            player.disableProtectionPrayers(cycles = 8)
            player.ifButton(protectFromMelee)
            advance()
            assertEquals(0, player.vars[varbits.protect_from_melee])

            // Other prayers are not affected.
            player.ifButton(thickSkin)
            advance()
            assertEquals(1, player.vars[varbits.thick_skin])

            advance(ticks = 8)
            player.ifButton(protectFromMelee)
            advance()
            assertEquals(1, player.vars[varbits.protect_from_melee])
        }

    @Test
    fun GameTestState.`quick prayers skip protection prayers while they are disabled`() =
        runPrayerTest { deps ->
            setUpPrayer()
            player.ifOpenOverlay(interfaces.orbs, components.toplevel_target_orbs)
            val selected =
                (1 shl deps.prayer(varbits.protect_from_melee).id) or
                    (1 shl deps.prayer(varbits.thick_skin).id)
            player.setVarBit(PrayerTabVarBits.quickprayer_selected, selected)

            player.disableProtectionPrayers(cycles = 8)
            player.ifButton(PrayerTabComponents.quick_prayers_orb)
            advance()
            assertEquals(1, player.vars[varbits.quickprayer_active])
            assertEquals(1, player.vars[varbits.thick_skin])
            assertEquals(0, player.vars[varbits.protect_from_melee])
        }

    private fun GameTestScope.setUpPrayer() {
        player.setBaseLevel(stats.prayer, 99)
        player.setCurrentLevel(stats.prayer, 99)
        player.ifOpenOverlay(interfaces.prayerbook, components.toplevel_target_side5)
        advance()
    }

    private fun GameTestState.runPrayerTest(
        testBody: GameTestScope.(PrayerTestDependencies) -> Unit
    ) =
        runInjectedGameTest(
            PrayerTestDependencies::class,
            PrayerModule(),
            PrayerTabScript::class,
            QuickPrayerScript::class,
            testBody = testBody,
        )

    class PrayerTestDependencies @Inject constructor(private val repo: PrayerRepository) {
        fun prayer(enabled: VarBitType): Prayer =
            repo.prayerList.single { it.enabled.isType(enabled) }

        fun component(enabled: VarBitType): ComponentType =
            repo.prayerComponents.entries.single { it.value.enabled.isType(enabled) }.key
    }
}
