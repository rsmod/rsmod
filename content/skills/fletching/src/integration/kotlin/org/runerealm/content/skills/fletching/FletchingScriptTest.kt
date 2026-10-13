package org.runerealm.content.skills.fletching

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
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.skills.fletching.configs.fletching_objs
import org.runerealm.content.skills.fletching.scripts.Fletching
import org.runerealm.game.inv.InvObj

@Execution(ExecutionMode.SAME_THREAD)
class FletchingScriptTest {
    private val objs = fletching_objs

    @Test
    fun GameTestState.`knife on logs offers products and makes chosen bow`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.knife), InvObj(objs.logs), InvObj(objs.logs))
            player.stats[stats.fletching] = 99
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertTrue(player.ui.containsModal(interfaces.skillmulti))

            // Options: arrow shafts, javelin shafts, shortbow (u), longbow (u), stock.
            player.resumePauseButton(components.skillmulti_c, sub = 2)
            advance(ticks = 2)
            assertEquals(1, player.count(objs.shortbow_u))
            assertEquals(1, player.count(objs.logs))
            assertEquals(5, player.statMap.getXP(stats.fletching) - xp0)

            advance(ticks = 2)
            assertEquals(1, player.count(objs.shortbow_u))
            advance(ticks = 1) // Three ticks per bow.
            assertEquals(2, player.count(objs.shortbow_u))
            assertEquals(0, player.count(objs.logs))
            assertEquals(10, player.statMap.getXP(stats.fletching) - xp0)
        }

    @Test
    fun GameTestState.`knife on logs makes 15 arrow shafts`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.knife), InvObj(objs.oak_logs))
            player.stats[stats.fletching] = 15
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_a, sub = 1)
            advance(ticks = 2)
            assertEquals(30, player.count(objs.arrow_shaft))
            assertEquals(10, player.statMap.getXP(stats.fletching) - xp0)
            assertMessageSent("You carefully cut the wood into 30 arrow shafts.")
        }

    @Test
    fun GameTestState.`fletching level is required`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.knife), InvObj(objs.yew_logs))
            player.stats[stats.fletching] = 64
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            player.resumePauseButton(components.skillmulti_b, sub = 1) // Yew shortbow (u).
            advance(ticks = 1)
            assertMessageSent("You need a Fletching level of 65 to make a yew shortbow (u).")
            assertEquals(1, player.count(objs.yew_logs))
        }

    @Test
    fun GameTestState.`string a single bow without interface`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.bow_string), InvObj(objs.maple_longbow_u))
            player.stats[stats.fletching] = 55
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertFalse(player.ui.containsModal(interfaces.skillmulti))
            assertEquals(1, player.count(objs.maple_longbow))
            assertEquals(0, player.count(objs.bow_string))
            assertEquals(58, player.statMap.getXP(stats.fletching) - xp0)
        }

    @Test
    fun GameTestState.`arrows are made in sets of 15`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.headless_arrow, 40), InvObj(objs.rune_arrowheads, 100))
            player.stats[stats.fletching] = 75
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(1, 0)
            advance(ticks = 1)
            assertTrue(player.ui.containsModal(interfaces.skillmulti))
            player.resumePauseButton(components.skillmulti_a, sub = 3) // "3" sets.
            advance(ticks = 2)
            assertEquals(15, player.count(objs.rune_arrow))
            assertEquals(187, player.statMap.getXP(stats.fletching) - xp0) // 15 * 12.5

            advance(ticks = 4)
            assertEquals(40, player.count(objs.rune_arrow))
            assertEquals(0, player.count(objs.headless_arrow))
            assertEquals(60, player.count(objs.rune_arrowheads))
            assertEquals(500, player.statMap.getXP(stats.fletching) - xp0)
        }

    @Test
    fun GameTestState.`headless arrows and bolts`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.feather, 5), InvObj(objs.arrow_shaft, 5))
            player.stats[stats.fletching] = 1
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(5, player.count(objs.headless_arrow))
            assertMessageSent("You attach feathers to 5 arrow shafts.")

            give(InvObj(objs.feather, 10), InvObj(objs.mithril_bolts_unf, 10))
            player.stats[stats.fletching] = 54
            player.opHeldU(1, 0)
            advance(ticks = 1)
            assertEquals(10, player.count(objs.mithril_bolts))
        }

    @Test
    fun GameTestState.`chisel cuts gems into bolt tips and tips bolts`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.chisel), InvObj(objs.ruby))
            player.stats[stats.fletching] = 63
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(12, player.count(objs.ruby_bolt_tips))

            give(InvObj(objs.ruby_bolt_tips, 12), InvObj(objs.adamant_bolts, 8))
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(8, player.count(objs.ruby_bolts))
            assertEquals(4, player.count(objs.ruby_bolt_tips))
        }

    @Test
    fun GameTestState.`crossbows need a hammer to attach limbs`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.bronze_limbs), InvObj(objs.wooden_stock))
            player.stats[stats.fletching] = 9
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertMessageSent("You need a hammer to do that.")
            assertEquals(0, player.count(objs.bronze_crossbow_u))

            give(InvObj(objs.bronze_limbs), InvObj(objs.wooden_stock), InvObj(objs.hammer))
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(1, player.count(objs.bronze_crossbow_u))

            player.inv[3] = InvObj(objs.crossbow_string)
            player.opHeldU(3, player.inv.indexOfFirst { it?.id == objs.bronze_crossbow_u.id })
            advance(ticks = 1)
            assertEquals(1, player.count(objs.bronze_crossbow))
            assertEquals(18, player.statMap.getXP(stats.fletching) - xp0)
        }

    @Test
    fun GameTestState.`darts and javelins`() =
        runGameTest(Fletching::class, HeldUOpScript::class) {
            give(InvObj(objs.feather, 10), InvObj(objs.rune_dart_tip, 10))
            player.stats[stats.fletching] = 81
            val xp0 = player.statMap.getXP(stats.fletching)
            player.opHeldU(0, 1)
            advance(ticks = 1)
            assertEquals(10, player.count(objs.rune_dart))
            assertEquals(188, player.statMap.getXP(stats.fletching) - xp0)

            give(InvObj(objs.javelin_shaft, 15), InvObj(objs.iron_javelin_heads, 15))
            player.opHeldU(1, 0)
            advance(ticks = 1)
            assertEquals(15, player.count(objs.iron_javelin))
        }

    private fun GameTestScope.give(vararg objs: InvObj) {
        player.clearInv()
        objs.forEachIndexed { slot, obj -> player.inv[slot] = obj }
    }
}
