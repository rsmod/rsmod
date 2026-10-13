package org.runerealm.content.skills.fishing.scripts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.npcs
import org.runerealm.api.config.refs.stats
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.skills.fishing.configs.fishing_objs
import org.runerealm.game.entity.Npc
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.npc.NpcType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.map.CoordGrid

@Execution(ExecutionMode.SAME_THREAD)
class FishingScriptTest {
    @Test
    fun GameTestState.`spot ops match fishing methods`() =
        runGameTest(Fishing::class) {
            val netBait = npcTypes[npcs.fishing_spot_1530]
            assertEquals("Net", netBait.op[0])
            assertEquals("Bait", netBait.op[2])

            val lureBait = npcTypes[npcs.rod_fishing_spot_1527]
            assertEquals("Lure", lureBait.op[0])
            assertEquals("Bait", lureBait.op[2])
        }

    @Test
    fun GameTestState.`require small fishing net`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.fishing_spot_1530)
            player.clearInv()
            player.opNpc1(spot)
            advance(ticks = 1)
            assertMessageSent("You need a net to catch these fish.")
        }

    @Test
    fun GameTestState.`require fishing level for lure`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.rod_fishing_spot_1527)
            give(fishing_objs.fly_fishing_rod, fishing_objs.feather)
            player.stats[stats.fishing] = 19
            player.opNpc1(spot)
            advance(ticks = 1)
            assertMessageSent("You need a Fishing level of at least 20 to lure these fish.")
        }

    @Test
    fun GameTestState.`require bait`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.fishing_spot_1530)
            give(fishing_objs.fishing_rod)
            player.stats[stats.fishing] = 5
            player.opNpc3(spot)
            advance(ticks = 1)
            assertMessageSent("You don't have any bait left.")
        }

    @Test
    fun GameTestState.`net shrimps on five tick cycle`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.fishing_spot_1530)
            give(fishing_objs.small_fishing_net)
            player.stats[stats.fishing] = 1
            random.next = 1 // Regular 5-tick cycle (no 1/5 extended tick).
            player.opNpc1(spot)
            advance(ticks = 1)
            assertMessageSent("You cast out your net...")

            advance(ticks = 4)
            assertDoesNotContain(player.inv, fishing_objs.raw_shrimps)

            random.next = 1 // Next cycle is not extended.
            random.then = 0 // Guarantee catch roll success.
            advance(ticks = 1)
            assertContains(player.inv, fishing_objs.raw_shrimps)
            assertMessageSent("You catch some raw shrimps.")
            assertEquals(10, player.statMap.getXP(stats.fishing))
        }

    @Test
    fun GameTestState.`failed roll catches nothing`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.fishing_spot_1530)
            give(fishing_objs.small_fishing_net)
            player.stats[stats.fishing] = 1
            random.next = 1
            player.opNpc1(spot)
            advance(ticks = 5)

            random.next = 1
            random.then = 99 // Fail the shrimp roll.
            advance(ticks = 1)
            assertDoesNotContain(player.inv, fishing_objs.raw_shrimps)
            assertEquals(0, player.statMap.getXP(stats.fishing))
        }

    @Test
    fun GameTestState.`anchovies are rolled before shrimps`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.fishing_spot_1530)
            give(fishing_objs.small_fishing_net)
            player.stats[stats.fishing] = 15
            random.next = 1
            player.opNpc1(spot)
            advance(ticks = 5)

            random.next = 1
            random.then = 0 // Anchovies roll succeeds.
            advance(ticks = 1)
            assertContains(player.inv, fishing_objs.raw_anchovies)
            assertDoesNotContain(player.inv, fishing_objs.raw_shrimps)
            assertMessageSent("You catch some raw anchovies.")
        }

    @Test
    fun GameTestState.`shrimps are rolled after failed anchovies roll`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.fishing_spot_1530)
            give(fishing_objs.small_fishing_net)
            player.stats[stats.fishing] = 15
            random.next = 1
            player.opNpc1(spot)
            advance(ticks = 5)

            random.next = 1
            random.then = 99 // Anchovies roll fails.
            random.then = 0 // Shrimps roll succeeds.
            advance(ticks = 1)
            assertContains(player.inv, fishing_objs.raw_shrimps)
            assertDoesNotContain(player.inv, fishing_objs.raw_anchovies)
        }

    @Test
    fun GameTestState.`bait fishing consumes bait`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.fishing_spot_1530)
            player.clearInv()
            player.inv[0] = InvObj(fishing_objs.fishing_rod)
            player.inv[1] = InvObj(fishing_objs.fishing_bait, count = 2)
            player.stats[stats.fishing] = 5
            random.next = 1
            player.opNpc3(spot)
            advance(ticks = 1)
            assertMessageSent("You cast out your line...")
            advance(ticks = 4)

            random.next = 1
            random.then = 0
            advance(ticks = 1)
            assertContains(player.inv, fishing_objs.raw_sardine)
            assertDoesNotContain(player.inv, fishing_objs.raw_herring)
            assertMessageSent("You catch a raw sardine.")
            assertEquals(1, player.inv[1]?.count)
        }

    @Test
    fun GameTestState.`stop when inventory is full`() =
        runGameTest(Fishing::class) {
            val spot = spawnSpot(npcs.fishing_spot_1530)
            player.clearInv()
            for (slot in player.inv.indices) {
                player.inv[slot] = InvObj(fishing_objs.small_fishing_net)
            }
            player.opNpc1(spot)
            advance(ticks = 1)
            assertMessageSent("You can't carry any more fish.")
        }

    private fun GameTestScope.spawnSpot(type: NpcType): Npc {
        val coords = CoordGrid(0, 50, 50, 34, 31)
        val spot = spawnNpc(coords, npcTypes[type])
        player.teleport(coords.translateX(-1))
        return spot
    }

    private fun GameTestScope.give(vararg objs: ObjType) {
        player.clearInv()
        objs.forEachIndexed { slot, obj -> player.inv[slot] = InvObj(obj) }
    }
}
