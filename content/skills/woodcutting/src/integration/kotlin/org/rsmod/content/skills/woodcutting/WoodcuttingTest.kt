package org.rsmod.content.skills.woodcutting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.righthand
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.assertions.assertNotNullContract
import org.rsmod.content.skills.woodcutting.Woodcutting.Companion.treeLevelReq
import org.rsmod.content.skills.woodcutting.Woodcutting.Companion.treeLogs
import org.rsmod.content.skills.woodcutting.Woodcutting.Companion.treeRespawnTimeHigh
import org.rsmod.content.skills.woodcutting.Woodcutting.Companion.treeStump
import org.rsmod.game.obj.InvObj
import org.rsmod.map.CoordGrid

class WoodcuttingTest {
    @Test
    fun GameTestState.`ensure all axe objs have required params`() = runBasicGameTest {
        val axes = cacheTypes.objs.values.filter { it.isAssociatedWith(content.woodcutting_axe) }
        for (axe in axes) {
            val axeParams = axe.paramMap
            assertNotNullContract(axeParams)
            assertTrue(params.skill_levelreq in axeParams)
            assertTrue(params.skill_anim in axeParams)
        }
    }

    @Test
    fun GameTestState.`ensure all tree locs have required params`() = runBasicGameTest {
        val trees = cacheTypes.locs.values.filter { it.isAssociatedWith(content.tree) }
        for (tree in trees) {
            val treeParams = tree.paramMap
            assertNotNullContract(treeParams)
            assertTrue(params.skill_levelreq in treeParams)
            assertTrue(params.skill_productitem in treeParams)
            assertTrue(params.skill_xp in treeParams)
            assertTrue(params.next_loc_stage in treeParams)
            assertTrue(params.respawn_time in treeParams)
        }

        // Trees which turn into stumps after a set period of time.
        val timed = trees.filter { it.paramMap?.contains(params.despawn_time) == true }
        for (tree in timed) {
            val treeParams = tree.paramMap
            assertNotNullContract(treeParams)
            assertFalse(params.deplete_chance in treeParams)
        }

        // Trees which turn into stumps based on random rates.
        val standard = trees - timed.toSet()
        for (tree in standard) {
            val treeParams = tree.paramMap
            assertNotNullContract(treeParams)
            assertTrue(params.deplete_chance in treeParams)
            assertTrue(treeParams[params.deplete_chance] in 0..255)
        }

        // Trees which have a variable respawn time.
        val respawnVariable =
            trees.filter {
                it.hasParam(params.respawn_time_low) || it.hasParam(params.respawn_time_high)
            }
        for (tree in respawnVariable) {
            val treeParams = tree.paramMap
            assertNotNullContract(treeParams)
            assertTrue(params.respawn_time_low in treeParams)
            assertTrue(params.respawn_time_high in treeParams)
            assertEquals(0, tree.param(params.respawn_time))
        }
    }

    @Test
    fun GameTestState.`validate axe requirement`() =
        runGameTest(Woodcutting::class) {
            val type = findLocType(content.tree) { it.treeLevelReq == 1 }
            val tree = placeMapLoc(CoordGrid(0, 50, 50, 34, 31), type)
            player.teleport(tree.coords.translateX(-1))
            player.clearInv()

            player.righthand = InvObj(objs.rune_axe)
            player.stats[stats.woodcutting] = 1
            player.opLoc1(tree)
            advance(ticks = 2)
            assertMessagesSent(
                "You need an axe to chop down this tree.",
                "You do not have an axe which you have the woodcutting level to use.",
            )

            player.actionDelay = -1 // Reset action delay set by initial axe check.
            player.stats[stats.woodcutting] = 100
            player.opLoc1(tree)
            advance(ticks = 2)
            assertMessageSent("You swing your axe at the tree.")
        }

    @Test
    fun GameTestState.`validate tree requirement`() =
        runGameTest(Woodcutting::class) {
            val type = findLocType(content.tree) { it.treeLevelReq > 1 }
            val tree = placeMapLoc(CoordGrid(0, 50, 50, 34, 31), type)
            player.teleport(tree.coords.translateX(-1))
            player.clearInv()

            player.righthand = InvObj(objs.bronze_axe)
            player.stats[stats.woodcutting] = type.treeLevelReq - 1
            player.opLoc1(tree)
            advance(ticks = 1)
            assertMessageSent(
                "You need a Woodcutting level of ${type.treeLevelReq} to chop down this tree."
            )

            player.stats[stats.woodcutting] = type.treeLevelReq
            player.opLoc1(tree)
            advance(ticks = 2)
            assertMessageSent("You swing your axe at the tree.")
        }

    @Test
    fun GameTestState.`cut down regular tree`() =
        runGameTest(Woodcutting::class) {
            val type = findLocType(content.tree) { it.treeLevelReq == 1 }
            val tree = placeMapLoc(CoordGrid(0, 50, 50, 34, 31), type)
            val logs = type.treeLogs
            player.teleport(tree.coords.translateX(-1))

            player.righthand = InvObj(objs.bronze_axe)
            player.stats[stats.woodcutting] = type.treeLevelReq
            player.opLoc1(tree)
            advance(ticks = 2)
            assertMessageSent("You swing your axe at the tree.")
            assertMessageNotSent("You get some logs.")
            assertDoesNotContain(player.inv, logs)

            advance(ticks = 1)
            assertMessageNotSent("You swing your axe at the tree.")
            assertMessageNotSent("You get some logs.")
            assertDoesNotContain(player.inv, logs)

            random.next = 0 // Set random roll to guarantee log success rate.
            random.then = 0 // Set random roll to avoid tree turning into stump.
            advance(ticks = 1)
            assertMessageNotSent("You swing your axe at the tree.")
            assertMessageSent("You get some logs.")
            assertContains(player.inv, logs)
            assertExists(tree)

            player.clearInv()

            advance(ticks = 1)
            assertMessageNotSent("You swing your axe at the tree.")
            assertMessageNotSent("You get some logs.")
            assertDoesNotContain(player.inv, logs)

            advance(ticks = 1)
            assertMessageNotSent("You swing your axe at the tree.")
            assertMessageNotSent("You get some logs.")
            assertDoesNotContain(player.inv, logs)

            advance(ticks = 1)
            assertMessageNotSent("You swing your axe at the tree.")
            assertMessageNotSent("You get some logs.")
            assertDoesNotContain(player.inv, logs)

            random.next = 0 // Set random roll to guarantee log success rate.
            random.then = 256 // Set random roll to guarantee tree turning into stump.
            advance(ticks = 1)
            assertMessageNotSent("You swing your axe at the tree.")
            assertMessageSent("You get some logs.")
            assertContains(player.inv, logs)
            assertDoesNotExist(tree)
            assertExists(tree.coords, type.treeStump)
            advance(ticks = type.treeRespawnTimeHigh)
            assertDoesNotExist(tree.coords, type.treeStump)
            assertExists(tree)
        }

    @Test
    fun GameTestState.`spam click tree to cut logs`() =
        runGameTest(Woodcutting::class) {
            val type = findLocType(content.tree) { it.hasParam(params.skill_productitem) }
            val tree = placeMapLoc(CoordGrid(0, 50, 50, 34, 31), type)
            val logs = type.treeLogs
            val logName = objTypes[type.treeLogs].name.lowercase()
            player.teleport(tree.coords.translateX(-1))
            player.clearInv()

            player.righthand = InvObj(objs.bronze_axe)
            player.stats[stats.woodcutting] = type.treeLevelReq
            player.opLoc1(tree)
            advance(ticks = 1) // Initial attempt where player action delay is set.

            player.opLoc1(tree)
            advance(ticks = 1)
            assertMessageSent("You swing your axe at the tree.")
            assertMessageNotSent("You get some $logName.")
            assertDoesNotContain(player.inv, logs)

            player.opLoc1(tree)
            advance(ticks = 1)
            assertMessageSent("You swing your axe at the tree.")
            assertMessageNotSent("You get some $logName.")
            assertDoesNotContain(player.inv, logs)

            player.opLoc1(tree)
            advance(ticks = 1)
            assertMessageSent("You swing your axe at the tree.")
            assertMessageSent("You get some $logName.")
            assertContains(player.inv, logs)
        }
}
