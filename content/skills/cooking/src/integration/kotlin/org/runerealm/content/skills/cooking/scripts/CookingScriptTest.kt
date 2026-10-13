package org.runerealm.content.skills.cooking.scripts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varps
import org.runerealm.api.inv.LocUOpScript
import org.runerealm.api.player.vars.VarPlayerIntMapSetter
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.skills.cooking.configs.CookingCategories
import org.runerealm.content.skills.cooking.configs.CookingLocs
import org.runerealm.content.skills.cooking.configs.cooking_objs
import org.runerealm.game.inv.InvObj
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.obj.ObjType
import org.runerealm.map.CoordGrid

@Execution(ExecutionMode.SAME_THREAD)
class CookingScriptTest {
    @Test
    fun GameTestState.`cook single raw shrimps on fire without interface`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val fire = placeFire()
            give(cooking_objs.raw_shrimps)
            player.stats[stats.cooking] = 1
            random.next = 0 // Guarantee success.
            player.opLocU(fire, invSlot = 0)
            advance(ticks = 1)
            assertFalse(player.ui.containsModal(interfaces.skillmulti))
            assertEquals(cooking_objs.shrimps.id, player.inv[0]?.id)
            assertMessageSent("You successfully cook some shrimps.")
            assertEquals(30, player.statMap.getXP(stats.cooking))
        }

    @Test
    fun GameTestState.`burn shrimps`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val fire = placeFire()
            give(cooking_objs.raw_shrimps)
            player.stats[stats.cooking] = 1
            random.next = 99 // Fail the roll (level 1 fire success chance is 129/256).
            player.opLocU(fire, invSlot = 0)
            advance(ticks = 1)
            assertEquals(cooking_objs.burnt_shrimp.id, player.inv[0]?.id)
            assertMessageSent("You accidentally burn the shrimps.")
            assertEquals(0, player.statMap.getXP(stats.cooking))
        }

    @Test
    fun GameTestState.`make-x cooks chosen quantity with 1-3-4 tick timing`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val fire = placeFire()
            give(cooking_objs.raw_shrimps, cooking_objs.raw_shrimps, cooking_objs.raw_shrimps)
            player.stats[stats.cooking] = 99
            player.opLocU(fire, invSlot = 0)
            advance(ticks = 1)
            assertTrue(player.ui.containsModal(interfaces.skillmulti))
            assertEquals(3, player.count(cooking_objs.raw_shrimps))

            player.resumePauseButton(components.skillmulti_a, sub = 2)
            advance(ticks = 1) // Selection is processed.
            assertEquals(3, player.count(cooking_objs.raw_shrimps))

            advance(ticks = 1) // First item: 1 tick after selection.
            assertEquals(2, player.count(cooking_objs.raw_shrimps))
            assertEquals(1, player.count(cooking_objs.shrimps))

            advance(ticks = 2)
            assertEquals(2, player.count(cooking_objs.raw_shrimps))
            advance(ticks = 1) // Second item: 3 ticks after the first.
            assertEquals(1, player.count(cooking_objs.raw_shrimps))
            assertEquals(2, player.count(cooking_objs.shrimps))

            advance(ticks = 8) // Only two were selected.
            assertEquals(1, player.count(cooking_objs.raw_shrimps))
        }

    @Test
    fun GameTestState.`make-x cooks remaining items every 4 ticks`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val fire = placeFire()
            give(*Array(4) { cooking_objs.raw_shrimps })
            player.stats[stats.cooking] = 99
            player.opLocU(fire, invSlot = 0)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_a, sub = 4)
            advance(ticks = 5) // Selection, then first (+1) and second (+3) items.
            assertEquals(2, player.count(cooking_objs.raw_shrimps))

            advance(ticks = 3)
            assertEquals(2, player.count(cooking_objs.raw_shrimps))
            advance(ticks = 1) // Third item: 4 ticks after the second.
            assertEquals(1, player.count(cooking_objs.raw_shrimps))

            advance(ticks = 3)
            assertEquals(1, player.count(cooking_objs.raw_shrimps))
            advance(ticks = 1) // Fourth item: 4 ticks after the third.
            assertEquals(0, player.count(cooking_objs.raw_shrimps))
        }

    @Test
    fun GameTestState.`stop cooking when fire goes out`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val fire = placeFire()
            give(cooking_objs.raw_shrimps, cooking_objs.raw_shrimps, cooking_objs.raw_shrimps)
            player.stats[stats.cooking] = 99
            player.opLocU(fire, invSlot = 0)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_a, sub = 3)
            advance(ticks = 2)
            assertEquals(2, player.count(cooking_objs.raw_shrimps))

            locDel(fire)
            advance(ticks = 10)
            assertEquals(2, player.count(cooking_objs.raw_shrimps))
        }

    @Test
    fun GameTestState.`require cooking level`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val fire = placeFire()
            give(cooking_objs.raw_trout)
            player.stats[stats.cooking] = 14
            player.opLocU(fire, invSlot = 0)
            advance(ticks = 1)
            assertMessageSent("You need a Cooking level of 15 to cook this.")
            assertEquals(cooking_objs.raw_trout.id, player.inv[0]?.id)
        }

    @Test
    fun GameTestState.`lumbridge range requires cooks assistant`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val range = placeLumbridgeRange()
            give(cooking_objs.raw_shrimps)
            player.stats[stats.cooking] = 99
            player.opLocU(range, invSlot = 0)
            advance(ticks = 1)
            assertMessageSent("Hey, who said you could use that?")
            assertEquals(cooking_objs.raw_shrimps.id, player.inv[0]?.id)
        }

    @Test
    fun GameTestState.`lumbridge range has lower burn rate`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val range = placeLumbridgeRange()
            give(cooking_objs.raw_shrimps)
            VarPlayerIntMapSetter.set(player, varps.cookquest, 2)
            player.stats[stats.cooking] = 1
            // 0.52 is above the regular success chance (129/256) but below the Lumbridge range
            // success chance (139/256) at level 1.
            random.next = 52
            player.opLocU(range, invSlot = 0)
            advance(ticks = 2)
            assertEquals(cooking_objs.shrimps.id, player.inv[0]?.id)
        }

    @Test
    fun GameTestState.`regular fire uses regular burn rate`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val fire = placeFire()
            give(cooking_objs.raw_shrimps)
            player.stats[stats.cooking] = 1
            random.next = 52
            player.opLocU(fire, invSlot = 0)
            advance(ticks = 1)
            assertEquals(cooking_objs.burnt_shrimp.id, player.inv[0]?.id)
        }

    @Test
    fun GameTestState.`range cook option offers carried raw food`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val range = placeRange()
            give(cooking_objs.raw_trout, cooking_objs.raw_shrimps, cooking_objs.raw_shrimps)
            player.stats[stats.cooking] = 99
            player.opLoc1(range)
            advance(ticks = 2)
            assertTrue(player.ui.containsModal(interfaces.skillmulti))

            player.resumePauseButton(components.skillmulti_b, sub = 1) // Shrimps.
            advance(ticks = 2)
            assertEquals(1, player.count(cooking_objs.shrimps))
            assertEquals(1, player.count(cooking_objs.raw_trout))
        }

    @Test
    fun GameTestState.`range cook option without raw food`() =
        runGameTest(Cooking::class, LocUOpScript::class) {
            val range = placeRange()
            player.clearInv()
            player.opLoc1(range)
            advance(ticks = 1)
            assertMessageSent("You have nothing to cook.")
        }

    private fun GameTestScope.placeFire(): BoundLocInfo {
        // The standard fire created through Firemaking.
        val type = findLocTypes { it.internalName == "fire" }.single()
        check(type.category == CookingCategories.fire.id)
        return placeHeatSource(type.id, approachFromEast = false)
    }

    private fun GameTestScope.placeRange(): BoundLocInfo {
        val type =
            findLocTypes {
                    it.category == CookingCategories.range.id && it.op.getOrNull(0) == "Cook"
                }
                .first()
        return placeHeatSource(type.id, approachFromEast = true)
    }

    private fun GameTestScope.placeLumbridgeRange(): BoundLocInfo =
        placeHeatSource(CookingLocs.lumbridge_range.id, approachFromEast = true)

    /**
     * Ranges force their approach side (e.g., `forceapproach=east` for the Lumbridge range), so the
     * player is placed on the side they can interact from.
     */
    private fun GameTestScope.placeHeatSource(id: Int, approachFromEast: Boolean): BoundLocInfo {
        val coords = CoordGrid(0, 50, 50, 34, 31)
        val loc = placeMapLoc(coords, locTypes.getValue(id))
        player.teleport(coords.translateX(if (approachFromEast) 1 else -1))
        return loc
    }

    private fun GameTestScope.give(vararg objs: ObjType) {
        player.clearInv()
        objs.forEachIndexed { slot, obj -> player.inv[slot] = InvObj(obj) }
    }
}
