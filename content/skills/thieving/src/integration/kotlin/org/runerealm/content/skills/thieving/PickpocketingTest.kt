package org.runerealm.content.skills.thieving

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.stats
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.npc.NpcType
import org.runerealm.map.CoordGrid

class PickpocketingTest {
    @Test
    fun GameTestState.`pickpocketing a man gives coins and experience`() =
        runGameTest(Pickpocketing::class) {
            val man = spawnNextToPlayer(test_npcs.man)
            player.opNpc3(man)
            awaitMessage("You attempt to pick the man's pocket.")
            random.next = 0 // Guarantee a successful roll.
            awaitMessage("You pick the man's pocket.")
            assertEquals(3, player.inv.count(objs.coins))
            assertTrue(player.statMap.getXP(stats.thieving) >= 8)
        }

    @Test
    fun GameTestState.`failing to pickpocket stuns and damages the player`() =
        runGameTest(Pickpocketing::class) {
            val man = spawnNextToPlayer(test_npcs.man)
            player.setBaseLevel(stats.hitpoints, 10)
            player.setCurrentLevel(stats.hitpoints, 10)
            player.opNpc3(man)
            awaitMessage("You attempt to pick the man's pocket.")
            random.next = 255 // Guarantee a failed roll.
            awaitMessage("You fail to pick the man's pocket.")
            assertMessageSent("You have been stunned.")
            advance(ticks = 2)
            assertEquals(0, player.inv.count(objs.coins))
            assertTrue(player.statMap.getCurrentLevel(stats.hitpoints) < 10)
        }

    @Test
    fun GameTestState.`farmers need level 10 thieving`() =
        runGameTest(Pickpocketing::class) {
            val farmer = spawnNextToPlayer(test_npcs.farmer)
            player.opNpc3(farmer)
            awaitMessage("You need a Thieving level of 10 to pick the farmer's pocket.")
        }

    /** Advances one tick at a time until [text] is sent (messages only last for their tick). */
    private fun GameTestScope.awaitMessage(text: String, maxTicks: Int = 5) {
        repeat(maxTicks) {
            advance()
            if (runCatching { assertMessageSent(text) }.isSuccess) {
                return
            }
        }
        assertMessageSent(text)
    }

    private fun GameTestScope.spawnNextToPlayer(type: NpcType): Npc {
        val coords = CoordGrid(0, 50, 50, 20, 20)
        allocZoneCollision(coords)
        player.teleport(coords)
        return spawnNpc(coords.translateX(1), npcTypes[type])
    }
}

internal object test_npcs : NpcReferences() {
    val man = find("man")
    val farmer = find("farmer1")
}
