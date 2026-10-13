package org.runerealm.api.game.process.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.objs
import org.runerealm.api.death.plugin.PlayerDeathScript
import org.runerealm.api.player.queueDeath
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.map.CoordGrid

class WildernessDeathTest {
    @Test
    fun GameTestState.`unskulled wilderness death keeps the three most valuable items`() =
        runGameTest(PlayerDeathScript::class) {
            player.observerUUID = 1 // Normally assigned on login; required to own ground objs.
            player.telejump(WILDERNESS)
            player.giveItems()
            player.queueDeath()
            advanceUntil(predicate = { !WildernessArea.contains(player.coords) })
            assertEquals(1, player.count(objs.abyssal_whip))
            assertEquals(1, player.count(objs.rune_platebody))
            assertEquals(1, player.count(objs.bronze_axe))
            assertEquals(0, player.count(objs.coins))
        }

    @Test
    fun GameTestState.`skulled wilderness death keeps nothing`() =
        runGameTest(PlayerDeathScript::class) {
            player.observerUUID = 1 // Normally assigned on login; required to own ground objs.
            player.telejump(WILDERNESS)
            player.giveItems()
            player.skullIcon = constants.skullicon_default
            player.queueDeath()
            advanceUntil(predicate = { !WildernessArea.contains(player.coords) })
            assertEquals(0, player.count(objs.abyssal_whip))
            assertEquals(0, player.count(objs.rune_platebody))
            assertEquals(0, player.count(objs.bronze_axe))
        }

    private fun Player.giveItems() {
        inv[0] = InvObj(objs.coins, 1000)
        inv[1] = InvObj(objs.bronze_axe)
        inv[2] = InvObj(objs.rune_platebody)
        inv[3] = InvObj(objs.abyssal_whip)
    }

    private companion object {
        val WILDERNESS = CoordGrid(0, 48, 56, 28, 16)
    }
}
