package org.runerealm.content.skills.smithing.scripts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.stats
import org.runerealm.api.inv.LocUOpScript
import org.runerealm.api.player.hands
import org.runerealm.api.player.ring
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.skills.smithing.configs.SmithingComponents
import org.runerealm.content.skills.smithing.configs.SmithingInterfaces
import org.runerealm.content.skills.smithing.configs.SmithingLocs
import org.runerealm.content.skills.smithing.configs.SmithingProductComponents
import org.runerealm.content.skills.smithing.configs.SmithingProductObjs
import org.runerealm.content.skills.smithing.configs.SmithingVarps
import org.runerealm.content.skills.smithing.configs.smithing_objs
import org.runerealm.game.inv.InvObj
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.obj.ObjType
import org.runerealm.map.CoordGrid

@Execution(ExecutionMode.SAME_THREAD)
class SmithingScriptTest {
    @Test
    fun GameTestState.`smelt bronze bars at a furnace`() =
        runGameTest(Smithing::class) {
            val furnace = placeFurnace()
            give(
                smithing_objs.copper_ore,
                smithing_objs.tin_ore,
                smithing_objs.copper_ore,
                smithing_objs.tin_ore,
            )
            player.stats[stats.smithing] = 1
            player.opLoc2(furnace)
            advance(ticks = 1)
            assertTrue(player.ui.containsModal(interfaces.skillmulti))

            player.resumePauseButton(components.skillmulti_a, sub = 2) // Bronze, quantity 2.
            advanceUntil({ player.count(smithing_objs.bronze_bar) == 2 }, timeoutTicks = 15)
            assertEquals(0, player.count(smithing_objs.copper_ore))
            assertEquals(0, player.count(smithing_objs.tin_ore))
            assertEquals(124, player.statMap.getFineXP(stats.smithing))
            assertMessageSent("You retrieve a bar of bronze.")
        }

    @Test
    fun GameTestState.`iron ore can be too impure without a ring of forging`() =
        runGameTest(Smithing::class) {
            val furnace = placeFurnace()
            give(smithing_objs.iron_ore)
            player.stats[stats.smithing] = 15
            val xpBefore = player.statMap.getFineXP(stats.smithing)
            player.opLoc2(furnace)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_c, sub = 1) // Iron.
            random.next = 51 // Fail the 50% roll.
            advanceUntil({ player.count(smithing_objs.iron_ore) == 0 }, timeoutTicks = 10)
            assertEquals(0, player.count(smithing_objs.iron_bar))
            assertMessageSent("The ore is too impure and you fail to refine it.")
            assertEquals(xpBefore, player.statMap.getFineXP(stats.smithing))
        }

    @Test
    fun GameTestState.`ring of forging guarantees iron bars and uses charges`() =
        runGameTest(Smithing::class) {
            val furnace = placeFurnace()
            give(smithing_objs.iron_ore)
            player.ring = InvObj(smithing_objs.ring_of_forging)
            player.stats[stats.smithing] = 15
            player.opLoc2(furnace)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_c, sub = 1)
            advanceUntil({ player.count(smithing_objs.iron_bar) == 1 }, timeoutTicks = 10)
            assertEquals(1, player.vars[SmithingVarps.ring_of_forging_charges])

            // The last charge melts the ring.
            player.setVarp(SmithingVarps.ring_of_forging_charges, 139)
            give(smithing_objs.iron_ore)
            player.opLoc2(furnace)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_c, sub = 1)
            advanceUntil({ player.count(smithing_objs.iron_bar) == 2 }, timeoutTicks = 10)
            assertEquals(null, player.ring)
        }

    @Test
    fun GameTestState.`steel needs coal and gold uses goldsmith gauntlets`() =
        runGameTest(Smithing::class, LocUOpScript::class) {
            val furnace = placeFurnace()
            give(smithing_objs.iron_ore, smithing_objs.coal)
            player.stats[stats.smithing] = 40
            player.opLocU(furnace, invSlot = 1) // Coal: steel and up.
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_a, sub = 1) // Steel.
            advanceUntilMessage("You need one iron ore and two coal to make a steel bar.")

            player.clearInv()
            give(smithing_objs.gold_ore)
            player.hands = InvObj(smithing_objs.goldsmith_gauntlets)
            val xpBefore = player.statMap.getFineXP(stats.smithing)
            player.opLocU(furnace, invSlot = 0)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_a, sub = 1)
            advanceUntil({ player.count(smithing_objs.gold_bar) == 1 }, timeoutTicks = 10)
            assertEquals(562, player.statMap.getFineXP(stats.smithing) - xpBefore)
        }

    @Test
    fun GameTestState.`anvil requires a hammer`() =
        runGameTest(Smithing::class) {
            val anvil = placeAnvil()
            give(smithing_objs.bronze_bar)
            player.opLoc1(anvil)
            advance(ticks = 1)
            assertModalNotOpen(SmithingInterfaces.smithing)
        }

    @Test
    fun GameTestState.`smith a bronze dagger and a rune platebody`() =
        runGameTest(Smithing::class) {
            val anvil = placeAnvil()
            give(smithing_objs.hammer, smithing_objs.bronze_bar)
            player.stats[stats.smithing] = 1
            player.opLoc1(anvil)
            advance(ticks = 1)
            assertModalOpen(SmithingInterfaces.smithing)

            player.ifButton(SmithingComponents.make_1)
            player.ifButton(SmithingProductComponents.dagger)
            advanceUntil({ player.count(SmithingProductObjs.bronze_dagger) == 1 }, 10)
            assertEquals(0, player.count(smithing_objs.bronze_bar))
            assertEquals(125, player.statMap.getFineXP(stats.smithing))

            player.clearInv()
            give(smithing_objs.hammer)
            repeat(5) { give(smithing_objs.runite_bar) }
            player.stats[stats.smithing] = 98
            player.opLoc1(anvil)
            advance(ticks = 1)
            player.ifButton(SmithingProductComponents.platebody)
            advanceUntilMessage("You need a Smithing level of 99 to make a rune platebody.")

            player.stats[stats.smithing] = 99
            val xpBefore = player.statMap.getFineXP(stats.smithing)
            player.opLoc1(anvil)
            advance(ticks = 1)
            player.ifButton(SmithingProductComponents.platebody)
            advanceUntil({ player.count(SmithingProductObjs.rune_platebody) == 1 }, 10)
            assertEquals(0, player.count(smithing_objs.runite_bar))
            assertEquals(3750, player.statMap.getFineXP(stats.smithing) - xpBefore)
        }

    @Test
    fun GameTestState.`nails come in sets of fifteen`() =
        runGameTest(Smithing::class) {
            val anvil = placeAnvil()
            give(smithing_objs.hammer, smithing_objs.iron_bar)
            player.stats[stats.smithing] = 19
            player.opLoc1(anvil)
            advance(ticks = 1)
            player.ifButton(SmithingProductComponents.nails)
            advanceUntil({ player.count(SmithingProductObjs.nails_iron) > 0 }, 10)
            assertEquals(15, player.count(SmithingProductObjs.nails_iron))
        }

    @Test
    fun GameTestState.`lumbridge rusted anvil only smiths bronze`() =
        runGameTest(Smithing::class, LocUOpScript::class) {
            val anvil = placeMapLoc(CoordGrid(0, 50, 50, 34, 31), SmithingLocs.lumbridge_anvil)
            player.teleport(anvil.coords.translateX(-1))
            give(smithing_objs.hammer, smithing_objs.iron_bar)
            player.opLocU(anvil, invSlot = 1)
            advance(ticks = 1)
            assertMessageSent("You can only use bronze bars to smith on this anvil.")
            assertModalNotOpen(SmithingInterfaces.smithing)
        }

    /** Advances tick by tick until [text] is sent; messages are only captured per tick. */
    private fun GameTestScope.advanceUntilMessage(text: String, timeoutTicks: Int = 6) {
        repeat(timeoutTicks) {
            advance(ticks = 1)
            val sent = runCatching { assertMessageSent(text) }.isSuccess
            if (sent) {
                return
            }
        }
        assertMessageSent(text)
    }

    private fun GameTestScope.placeFurnace(): BoundLocInfo {
        val type = locTypes.values.first { it.internalName == "furnace" }
        val furnace = placeMapLoc(CoordGrid(0, 50, 50, 30, 30), type)
        player.clearAllInvs()
        player.teleport(furnace.coords.translateX(3).translateZ(1))
        return furnace
    }

    private fun GameTestScope.placeAnvil(): BoundLocInfo {
        val type = locTypes.values.first { it.internalName == "anvil" }
        val anvil = placeMapLoc(CoordGrid(0, 50, 50, 34, 31), type)
        player.clearAllInvs()
        player.teleport(anvil.coords.translateX(-1))
        return anvil
    }

    private fun GameTestScope.give(vararg objs: ObjType) {
        for (obj in objs) {
            val slot = player.inv.indices.first { player.inv[it] == null }
            player.inv[slot] = InvObj(obj)
        }
    }
}
