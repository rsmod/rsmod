package org.runerealm.content.other.ironman

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.ironman.IronmanMode
import org.runerealm.api.player.ironman.ironmanMode
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.other.ironman.configs.ironman_components
import org.runerealm.content.other.ironman.configs.ironman_interfaces
import org.runerealm.content.other.ironman.configs.ironman_npcs
import org.runerealm.content.other.ironman.configs.ironman_objs
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.player.SessionStateEvent
import org.runerealm.map.CoordGrid

class IronmanTutorTest {
    @Test
    fun GameTestState.`fresh account can switch freely between modes`() =
        runGameTest(IronmanTutor::class) {
            player.ifOpenMain(ironman_interfaces.ironman_setup)

            player.ifButton(ironman_components.uim_button)
            advance(ticks = 1)
            assertEquals(IronmanMode.Ultimate, player.ironmanMode)

            player.ifButton(ironman_components.hcim_button)
            advance(ticks = 1)
            assertEquals(IronmanMode.Hardcore, player.ironmanMode)

            player.ifButton(ironman_components.none_button)
            advance(ticks = 1)
            assertEquals(IronmanMode.None, player.ironmanMode)
        }

    @Test
    fun GameTestState.`trained account can only schedule a downgrade`() =
        runGameTest(IronmanTutor::class) {
            val downgrades = IronmanDowngrades(cacheTypes.stats)
            downgrades.setMode(player, IronmanMode.Hardcore)
            player.statMap.setXP(stats.attack, 100)
            assertFalse(downgrades.isFreshAccount(player))
            player.ifOpenMain(ironman_interfaces.ironman_setup)

            // Upgrading is not allowed once the account has left its "Tutorial Island" stage.
            player.ifButton(ironman_components.uim_button)
            advance(ticks = 1)
            assertEquals(IronmanMode.Hardcore, player.ironmanMode)
            assertFalse(downgrades.hasPendingDowngrade(player))

            player.ifButton(ironman_components.im_button)
            advance(ticks = 1)
            assertEquals(IronmanMode.Hardcore, player.ironmanMode)
            assertTrue(downgrades.hasPendingDowngrade(player))
            assertEquals(IronmanDowngrades.SHORT_DAYS, downgrades.daysRemaining(player))

            player.ifButton(ironman_components.downgrade_cancel)
            advance(ticks = 1)
            assertFalse(downgrades.hasPendingDowngrade(player))
        }

    @Test
    fun GameTestState.`due downgrade applies on login`() =
        runGameTest(IronmanTutor::class) {
            val downgrades = IronmanDowngrades(cacheTypes.stats)
            downgrades.setMode(player, IronmanMode.Ultimate)
            val today = downgrades.today()
            downgrades.scheduleDowngrade(player, IronmanMode.Standard, today - 3)
            assertTrue(downgrades.hasPendingDowngrade(player))

            eventBus.publish(SessionStateEvent.Login(player))
            assertEquals(IronmanMode.Ultimate, player.ironmanMode) // Warning period not over yet.

            downgrades.scheduleDowngrade(player, IronmanMode.Standard, today - 7)
            eventBus.publish(SessionStateEvent.Login(player))
            assertEquals(IronmanMode.Standard, player.ironmanMode)
            assertFalse(downgrades.hasPendingDowngrade(player))
        }

    @Test
    fun GameTestState.`long downgrade protection requires 1000 total level`() =
        runGameTest(IronmanTutor::class) {
            val downgrades = IronmanDowngrades(cacheTypes.stats)
            downgrades.setMode(player, IronmanMode.Standard)
            player.ifOpenMain(ironman_interfaces.ironman_setup)
            player.ifButton(ironman_components.downgrade_long)
            advance(ticks = 1)
            assertMessageSent("You need a total level of at least 1,000 to select this option.")
        }

    @Test
    fun GameTestState.`armour op gives ironman armour`() =
        runGameTest(IronmanTutor::class) {
            val downgrades = IronmanDowngrades(cacheTypes.stats)
            downgrades.setMode(player, IronmanMode.Standard)
            val adam = spawnAdam()
            player.opNpc3(adam)
            advance(ticks = 3)
            assertEquals(1, player.inv.count(ironman_objs.ironman_helm))
            assertEquals(1, player.inv.count(ironman_objs.ironman_platebody))
            assertEquals(1, player.inv.count(ironman_objs.ironman_platelegs))
        }

    @Test
    fun GameTestState.`setup op opens the ironman setup interface`() =
        runGameTest(IronmanTutor::class) {
            val adam = spawnAdam()
            player.opNpc4(adam)
            advance(ticks = 3)
            assertTrue(player.ui.containsModal(ironman_interfaces.ironman_setup))
        }

    private fun GameTestScope.spawnAdam(): Npc {
        val coords = CoordGrid(0, 50, 50, 20, 20)
        allocZoneCollision(coords)
        player.teleport(coords)
        return spawnNpc(coords.translateX(1), npcTypes[ironman_npcs.adam])
    }
}
