package org.runerealm.content.skills.crafting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.stats
import org.runerealm.api.inv.HeldUOpScript
import org.runerealm.api.inv.LocUOpScript
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.skills.crafting.configs.CraftingGoldComponents
import org.runerealm.content.skills.crafting.configs.CraftingInterfaces
import org.runerealm.content.skills.crafting.configs.crafting_objs
import org.runerealm.content.skills.crafting.scripts.Crafting
import org.runerealm.content.skills.crafting.scripts.CraftingStations
import org.runerealm.content.skills.crafting.scripts.JewelleryCrafting
import org.runerealm.game.inv.InvObj
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.map.CoordGrid

@Execution(ExecutionMode.SAME_THREAD)
class CraftingScriptTest {
    private val objs = crafting_objs

    @Test
    fun GameTestState.`cut sapphire with chisel`() =
        runGameTest(Crafting::class, HeldUOpScript::class) {
            give(InvObj(objs.chisel), InvObj(objs.uncut_sapphire))
            player.stats[stats.crafting] = 20
            val xp0 = xp()
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertFalse(player.ui.containsModal(interfaces.skillmulti))
            assertEquals(1, player.count(objs.sapphire))
            assertEquals(50, xp() - xp0)
        }

    @Test
    fun GameTestState.`semi-precious gems can be crushed`() =
        runGameTest(Crafting::class, HeldUOpScript::class) {
            give(InvObj(objs.chisel), InvObj(objs.uncut_opal))
            player.stats[stats.crafting] = 1
            val xp0 = xp()
            random.next = 255 // Fail the 128/256 roll.
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(1, player.count(objs.crushed_gem))
            assertEquals(0, player.count(objs.opal))
            assertMessageSent("You mis-hit the chisel and smash the opal to pieces!")
            assertEquals(3, xp() - xp0)
        }

    @Test
    fun GameTestState.`gem cutting requires level`() =
        runGameTest(Crafting::class, HeldUOpScript::class) {
            give(InvObj(objs.chisel), InvObj(objs.uncut_diamond))
            player.stats[stats.crafting] = 42
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertMessageSent("You need a Crafting level of 43 to make a diamond.")
            assertEquals(1, player.count(objs.uncut_diamond))
        }

    @Test
    fun GameTestState.`needle on leather offers leather items and uses thread`() =
        runGameTest(Crafting::class, HeldUOpScript::class) {
            give(InvObj(objs.needle), InvObj(objs.thread), *Array(6) { InvObj(objs.leather) })
            player.stats[stats.crafting] = 1
            val xp0 = xp()
            player.opHeldU(0, 2)
            advance(ticks = 1)
            assertTrue(player.ui.containsModal(interfaces.skillmulti))
            player.resumePauseButton(components.skillmulti_a, sub = 6) // Leather gloves.
            advance(ticks = 2)
            assertEquals(1, player.count(objs.leather_gloves))
            assertEquals(13, xp() - xp0)

            advance(ticks = 3 * 4)
            // One thread is used up every five items; there was only one thread.
            assertEquals(5, player.count(objs.leather_gloves))
            assertEquals(0, player.count(objs.thread))
            assertEquals(1, player.count(objs.leather))
            assertEquals(1, player.count(objs.needle))
        }

    @Test
    fun GameTestState.`dragonhide body uses three leathers`() =
        runGameTest(Crafting::class, HeldUOpScript::class) {
            give(InvObj(objs.needle), InvObj(objs.thread), *Array(3) { InvObj(objs.green_leather) })
            player.stats[stats.crafting] = 63
            val xp0 = xp()
            player.opHeldU(0, 2)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_c, sub = 1) // Green d'hide body.
            advance(ticks = 2)
            assertEquals(1, player.count(objs.green_body))
            assertEquals(0, player.count(objs.green_leather))
            assertEquals(186, xp() - xp0)
        }

    @Test
    fun GameTestState.`glassblowing pipe on molten glass`() =
        runGameTest(Crafting::class, HeldUOpScript::class) {
            give(
                InvObj(objs.glassblowing_pipe),
                InvObj(objs.molten_glass),
                InvObj(objs.molten_glass),
            )
            player.stats[stats.crafting] = 33
            player.opHeldU(0, 1)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_d, sub = 2) // Vial.
            advance(ticks = 2)
            assertEquals(1, player.count(objs.vial))
            advance(ticks = 3)
            assertEquals(2, player.count(objs.vial))
            assertEquals(0, player.count(objs.molten_glass))
        }

    @Test
    fun GameTestState.`string an amulet and make a battlestaff`() =
        runGameTest(Crafting::class, HeldUOpScript::class) {
            give(InvObj(objs.ball_of_wool), InvObj(objs.ruby_amulet_u))
            player.stats[stats.crafting] = 66
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(1, player.count(objs.ruby_amulet))

            give(InvObj(objs.air_orb), InvObj(objs.battlestaff))
            val xp0 = xp()
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(1, player.count(objs.air_battlestaff))
            assertEquals(137, xp() - xp0)
        }

    @Test
    fun GameTestState.`soft clay from clay and water`() =
        runGameTest(Crafting::class, HeldUOpScript::class) {
            give(InvObj(objs.bucket_water), InvObj(objs.clay))
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(1, player.count(objs.soft_clay))
            assertEquals(1, player.count(objs.bucket_empty))
        }

    @Test
    fun GameTestState.`crafting stations are found in the cache`() = runGameTest {
        assertTrue(spinningWheel() != null)
        assertTrue(findLocTypes { it.name == "Potter's Wheel" && "Use" in it.op }.any())
        assertTrue(findLocTypes { it.name == "Pottery Oven" && "Fire" in it.op }.any())
        assertTrue(furnace() != null)
    }

    @Test
    fun GameTestState.`spin wool on a spinning wheel`() =
        runGameTest(CraftingStations::class, Crafting::class, LocUOpScript::class) {
            val wheel = place(spinningWheel()!!)
            give(InvObj(objs.wool), InvObj(objs.wool))
            player.stats[stats.crafting] = 1
            player.opLoc2(wheel)
            advance(ticks = 2)
            assertTrue(player.ui.containsModal(interfaces.skillmulti))
            player.resumePauseButton(components.skillmulti_a, sub = 2)
            advance(ticks = 2)
            assertEquals(1, player.count(objs.ball_of_wool))
            advance(ticks = 3)
            assertEquals(2, player.count(objs.ball_of_wool))
        }

    @Test
    fun GameTestState.`shape and fire a bowl`() =
        runGameTest(CraftingStations::class, Crafting::class, LocUOpScript::class) {
            val wheel =
                place(findLocTypes { it.name == "Potter's Wheel" && "Use" in it.op }.first())
            give(InvObj(objs.soft_clay))
            player.stats[stats.crafting] = 8
            val xp0 = xp()
            player.opLoc1(wheel)
            advance(ticks = 2)
            player.resumePauseButton(components.skillmulti_c, sub = 1) // Bowl.
            advance(ticks = 2)
            assertEquals(1, player.count(objs.bowl_unfired))
            assertEquals(18, xp() - xp0)

            val ovenType = findLocTypes { it.name == "Pottery Oven" && "Fire" in it.op }.first()
            val oven = place(ovenType, CoordGrid(0, 50, 50, 20, 20))
            player.opLoc1(oven)
            advance(ticks = 2)
            assertEquals(1, player.count(objs.bowl_empty))
            assertEquals(33, xp() - xp0)
        }

    @Test
    fun GameTestState.`molten glass at a furnace`() =
        runGameTest(CraftingStations::class, Crafting::class, LocUOpScript::class) {
            val furnace = place(furnace()!!)
            give(InvObj(objs.bucket_sand), InvObj(objs.soda_ash))
            player.stats[stats.crafting] = 1
            val xp0 = xp()
            player.opLocU(furnace, invSlot = 0)
            advance(ticks = 2)
            assertEquals(1, player.count(objs.molten_glass))
            assertEquals(1, player.count(objs.bucket_empty))
            assertEquals(20, xp() - xp0)
        }

    @Test
    fun GameTestState.`gold bar on furnace opens jewellery interface and makes rings`() =
        runGameTest(JewelleryCrafting::class, Crafting::class, LocUOpScript::class) {
            val furnace = place(furnace()!!)
            give(
                InvObj(objs.gold_bar),
                InvObj(objs.gold_bar),
                InvObj(objs.sapphire),
                InvObj(objs.sapphire),
                InvObj(objs.ring_mould),
            )
            player.stats[stats.crafting] = 20
            val xp0 = xp()
            player.opLocU(furnace, invSlot = 0)
            advance(ticks = 2)
            assertTrue(player.ui.containsModal(CraftingInterfaces.crafting_gold))

            player.ifButton(CraftingGoldComponents.make_all)
            advance(ticks = 1)
            player.ifButton(CraftingGoldComponents.sapphire_ring)
            advance(ticks = 2)
            assertFalse(player.ui.containsModal(CraftingInterfaces.crafting_gold))
            assertEquals(1, player.count(objs.sapphire_ring))
            advance(ticks = 3)
            assertEquals(2, player.count(objs.sapphire_ring))
            assertEquals(0, player.count(objs.gold_bar))
            assertEquals(1, player.count(objs.ring_mould))
            assertEquals(80, xp() - xp0)
        }

    @Test
    fun GameTestState.`jewellery requires the mould`() =
        runGameTest(JewelleryCrafting::class, Crafting::class, LocUOpScript::class) {
            val furnace = place(furnace()!!)
            give(InvObj(objs.gold_bar))
            player.stats[stats.crafting] = 20
            player.opLocU(furnace, invSlot = 0)
            advance(ticks = 2)
            player.ifButton(CraftingGoldComponents.gold_necklace)
            advance(ticks = 1)
            assertMessageSent("You need a necklace mould to make this.")
            assertEquals(1, player.count(objs.gold_bar))
        }

    private fun GameTestScope.xp(): Int = player.statMap.getXP(stats.crafting)

    private fun GameTestScope.spinningWheel(): UnpackedLocType? =
        findLocTypes { it.name == "Spinning wheel" && "Spin" in it.op }.firstOrNull()

    private fun GameTestScope.furnace(): UnpackedLocType? =
        findLocTypes { it.name == "Furnace" && "Smelt" in it.op }.firstOrNull()

    private fun GameTestScope.place(
        type: UnpackedLocType,
        coords: CoordGrid = CoordGrid(0, 50, 50, 34, 31),
    ): BoundLocInfo {
        val loc = placeMapLoc(coords, type)
        // `forceapproach` flags block the north (1), east (2), south (4) and west (8) sides.
        val flags = type.forceApproachFlags
        val side =
            when {
                flags and 2 == 0 -> coords.translateX(type.width)
                flags and 4 == 0 -> coords.translateZ(-1)
                flags and 8 == 0 -> coords.translateX(-1)
                else -> coords.translateZ(type.length)
            }
        player.teleport(side)
        return loc
    }

    private fun GameTestScope.give(vararg objs: InvObj) {
        player.clearInv()
        objs.forEachIndexed { slot, obj -> player.inv[slot] = obj }
    }
}
