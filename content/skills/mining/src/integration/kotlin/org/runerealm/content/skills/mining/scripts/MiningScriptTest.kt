package org.runerealm.content.skills.mining.scripts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.front
import org.runerealm.api.player.righthand
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.skills.mining.configs.MiningRocks
import org.runerealm.content.skills.mining.configs.Pickaxes
import org.runerealm.content.skills.mining.configs.mining_locs
import org.runerealm.content.skills.mining.configs.mining_objs
import org.runerealm.content.skills.mining.scripts.Mining.Companion.findPickaxe
import org.runerealm.game.inv.InvObj
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.loc.LocType
import org.runerealm.map.CoordGrid

@Execution(ExecutionMode.SAME_THREAD)
class MiningScriptTest {
    @Test
    fun GameTestState.`mine copper rock until it depletes and respawns`() =
        runGameTest(Mining::class) {
            val copper = rockOfCategory("copperrock1")
            val rock = place(copper)
            player.righthand = InvObj(mining_objs.rune_pickaxe)
            player.stats[stats.mining] = 41
            val xpBefore = player.statMap.getFineXP(stats.mining)

            random.next = 2 // Do not roll the random gem table.
            random.then = 0 // Guarantee the ore roll.
            player.opLoc1(rock)
            advance(ticks = 1)
            assertMessageSent("You swing your pick at the rock.")
            assertDoesNotContain(player.inv, mining_objs.copper_ore)

            advanceUntil({ mining_objs.copper_ore in player.inv }, timeoutTicks = 10)
            assertMessageSent("You manage to mine some copper.")
            assertEquals(175, player.statMap.getFineXP(stats.mining) - xpBefore)
            assertDoesNotExist(rock)
            assertExists(rock.coords, mining_locs.rocks1)

            advance(ticks = MiningRocks.copper.respawnTicks)
            assertExists(rock)
        }

    @Test
    fun GameTestState.`rune pickaxe rolls every three ticks`() =
        runGameTest(Mining::class) {
            val rock = place(rockOfCategory("tinrock2"))
            player.righthand = InvObj(mining_objs.rune_pickaxe)
            player.stats[stats.mining] = 41

            player.opLoc1(rock)
            advance(ticks = 1) // Swing; the first roll happens 3 ticks later.
            random.next = 2
            random.then = 0
            advance(ticks = 2)
            assertDoesNotContain(player.inv, mining_objs.tin_ore)
            advance(ticks = 1)
            assertContains(player.inv, mining_objs.tin_ore)
            assertExists(rock.coords, mining_locs.rocks2)
        }

    @Test
    fun GameTestState.`level and pickaxe requirements`() =
        runGameTest(Mining::class) {
            val rock = place(rockOfCategory("ironrock1"))
            player.stats[stats.mining] = 14
            player.righthand = InvObj(mining_objs.bronze_pickaxe)
            player.opLoc1(rock)
            advance(ticks = 1)
            assertMessageSent("You need a Mining level of 15 to mine this rock.")

            player.stats[stats.mining] = 15
            player.righthand = InvObj(mining_objs.rune_pickaxe)
            player.opLoc1(rock)
            advance(ticks = 1)
            assertMessagesSent(
                "You need a pickaxe to mine this rock.",
                "You do not have a pickaxe which you have the Mining level to use.",
            )
        }

    @Test
    fun GameTestState.`inventory full message`() =
        runGameTest(Mining::class) {
            val rock = place(rockOfCategory("coalrock1"))
            player.stats[stats.mining] = 30
            player.righthand = InvObj(mining_objs.bronze_pickaxe)
            player.fillInv()
            player.opLoc1(rock)
            advance(ticks = 1)
            assertMessageSent("Your inventory is too full to hold any more coal.")
        }

    @Test
    fun GameTestState.`best pickaxe is chosen from worn and carried`() =
        runGameTest(Mining::class) {
            player.clearInv()
            player.stats[stats.mining] = 41
            player.righthand = InvObj(mining_objs.steel_pickaxe)
            player.inv[0] = InvObj(mining_objs.rune_pickaxe)
            player.inv[1] = InvObj(mining_objs.dragon_pickaxe)
            assertEquals(mining_objs.rune_pickaxe, findPickaxe(player)?.obj)

            player.stats[stats.mining] = 61
            assertEquals(mining_objs.dragon_pickaxe, findPickaxe(player)?.obj)

            player.stats[stats.mining] = 5
            assertEquals(null, findPickaxe(player))
            assertEquals(6, Pickaxes[mining_objs.steel_pickaxe.id]?.level)
            player.inv[2] = InvObj(mining_objs.iron_pickaxe)
            assertEquals(mining_objs.iron_pickaxe, findPickaxe(player)?.obj)
        }

    @Test
    fun GameTestState.`random gem replaces the ore roll`() =
        runGameTest(Mining::class) {
            val rock = place(rockOfCategory("copperrock1"))
            player.righthand = InvObj(mining_objs.rune_pickaxe)
            player.front = InvObj(mining_objs.amulet_of_glory_4)
            player.stats[stats.mining] = 41
            val xpBefore = player.statMap.getFineXP(stats.mining)
            player.opLoc1(rock)
            advance(ticks = 1)

            random.next = 1 // Roll the gem table.
            random.then = 0 // First entry: uncut sapphire.
            advance(ticks = 3)
            assertContains(player.inv, mining_objs.uncut_sapphire)
            assertDoesNotContain(player.inv, mining_objs.copper_ore)
            assertMessageSent("You just found a sapphire!")
            assertEquals(xpBefore, player.statMap.getFineXP(stats.mining))
            assertExists(rock)
        }

    @Test
    fun GameTestState.`essence rock gives pure essence from level 30 and never depletes`() =
        runGameTest(Mining::class) {
            val rock = place(mining_locs.rune_essence)
            player.righthand = InvObj(mining_objs.bronze_pickaxe)
            player.stats[stats.mining] = 1
            player.opLoc1(rock)
            advanceUntil({ mining_objs.rune_essence in player.inv }, timeoutTicks = 20)
            assertExists(rock)

            player.clearInv()
            player.stats[stats.mining] = 30
            advanceUntil({ mining_objs.pure_essence in player.inv }, timeoutTicks = 20)
            assertDoesNotContain(player.inv, mining_objs.rune_essence)
            assertExists(rock)
        }

    @Test
    fun GameTestState.`depleted rock message`() =
        runGameTest(Mining::class) {
            val rock = place(mining_locs.rocks1)
            player.righthand = InvObj(mining_objs.rune_pickaxe)
            player.opLoc1(rock)
            advance(ticks = 1)
            assertMessageSent("There is no ore currently available in this rock.")
        }

    /** Advances tick by tick until [text] is sent; messages are only captured per tick. */
    private fun GameTestScope.advanceUntilMessage(text: String, timeoutTicks: Int = 6) {
        repeat(timeoutTicks) {
            advance(ticks = 1)
            if (runCatching { assertMessageSent(text) }.isSuccess) {
                return
            }
        }
        assertMessageSent(text)
    }

    private fun GameTestScope.rockOfCategory(internal: String): LocType =
        locTypes.values.first { it.internalName == internal }

    private fun GameTestScope.place(type: LocType): BoundLocInfo {
        val rock = placeMapLoc(CoordGrid(0, 50, 50, 34, 31), type)
        player.clearAllInvs()
        player.teleport(rock.coords.translateX(-1))
        return rock
    }
}
