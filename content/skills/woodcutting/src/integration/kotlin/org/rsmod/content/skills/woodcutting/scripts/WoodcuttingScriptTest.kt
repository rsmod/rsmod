package org.rsmod.content.skills.woodcutting.scripts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.righthand
import org.rsmod.api.testing.GameTestState
import org.rsmod.content.skills.woodcutting.scripts.Woodcutting.Companion.treeLevelReq
import org.rsmod.content.skills.woodcutting.scripts.Woodcutting.Companion.treeLogs
import org.rsmod.content.skills.woodcutting.scripts.Woodcutting.Companion.treeRespawnTime
import org.rsmod.content.skills.woodcutting.scripts.Woodcutting.Companion.treeRespawnTimeHigh
import org.rsmod.content.skills.woodcutting.scripts.Woodcutting.Companion.treeStump
import org.rsmod.game.obj.InvObj
import org.rsmod.map.CoordGrid

class WoodcuttingScriptTest {
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
    fun GameTestState.`cut down timed-despawn tree`() =
        runGameTest(Woodcutting::class) {
            val type = findLocType(content.tree) { it.hasParam(params.despawn_time) }
            val tree = placeMapLoc(CoordGrid(0, 50, 50, 34, 31), type)
            val logs = type.treeLogs
            player.teleport(tree.coords.translateX(-1))

            player.righthand = InvObj(objs.bronze_axe)
            player.stats[stats.woodcutting] = type.treeLevelReq
            player.opLoc1(tree)
            advance(ticks = 3)

            random.next = 0 // Set random roll to guarantee log success rate.
            advance(ticks = 1)
            assertContains(player.inv, logs)
            val controller = conRepo.findExact(tree.coords)
            assertNotNull(controller)

            // Fast-forward until despawn controller has 1 tick duration left.
            advance(ticks = controller.durationStart - 1)
            assertExists(tree)
            assertDoesNotExist(tree.coords, type.treeStump)
            assertEquals(controller, conRepo.findExact(tree.coords))

            random.next = 0 // Set random roll to guarantee log success rate.
            player.actionDelay = mapClock.cycle // Set player action delay to force log cut.
            advance(ticks = 1)
            assertExists(tree.coords, type.treeStump)
            assertDoesNotExist(tree)

            advance(ticks = type.treeRespawnTime - 2)
            assertExists(tree.coords, type.treeStump)
            assertDoesNotExist(tree)

            advance(ticks = 1)
            assertExists(tree)
            assertDoesNotExist(tree.coords, type.treeStump)
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
