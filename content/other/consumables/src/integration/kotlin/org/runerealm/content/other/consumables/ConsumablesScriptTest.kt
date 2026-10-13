package org.runerealm.content.other.consumables

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.stats
import org.runerealm.api.inv.HeldOpScript
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.stat.statSub
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.other.consumables.configs.consumable_objs as objs
import org.runerealm.game.inv.InvObj

@Execution(ExecutionMode.SAME_THREAD)
class ConsumablesScriptTest {
    @Test
    fun GameTestState.`eating shrimps heals 3 and delays the next bite`() =
        runGameTest(ConsumablesScript::class, HeldOpScript::class) {
            player.stats[stats.hitpoints] = 10
            player.statSub(stats.hitpoints, 6, 0)
            player.inv[0] = InvObj(objs.shrimps)
            player.inv[1] = InvObj(objs.shrimps)

            player.opHeld(0)
            advance(ticks = 1)
            assertMessageSent("You eat the shrimps.")
            assertMessageSent("It heals some health.")
            assertEquals(7, player.stat(stats.hitpoints))
            assertNull(player.inv[0])

            // The 3-tick food delay blocks the next bite.
            player.opHeld(1)
            advance(ticks = 1)
            assertEquals(objs.shrimps.id, player.inv[1]?.id)

            advance(ticks = 2)
            player.opHeld(1)
            advance(ticks = 1)
            assertNull(player.inv[1])
            assertEquals(10, player.stat(stats.hitpoints))
        }

    @Test
    fun GameTestState.`pizza is eaten in two halves`() =
        runGameTest(ConsumablesScript::class, HeldOpScript::class) {
            player.inv[0] = InvObj(objs.plain_pizza)
            player.opHeld(0)
            advance(ticks = 1)
            assertMessageSent("You eat half of the plain pizza.")
            assertEquals(objs.half_plain_pizza.id, player.inv[0]?.id)
            advance(ticks = 3)
            player.opHeld(0)
            advance(ticks = 1)
            assertMessageSent("You eat the remaining plain pizza.")
            assertNull(player.inv[0])
        }

    @Test
    fun GameTestState.`pie leaves a pie dish`() =
        runGameTest(ConsumablesScript::class, HeldOpScript::class) {
            player.inv[0] = InvObj(objs.half_apple_pie)
            player.opHeld(0)
            advance(ticks = 1)
            assertEquals(objs.pie_dish.id, player.inv[0]?.id)
        }

    @Test
    fun GameTestState.`karambwan can be combo eaten in the same tick`() =
        runGameTest(ConsumablesScript::class, HeldOpScript::class) {
            player.stats[stats.hitpoints] = 50
            player.statSub(stats.hitpoints, 40, 0)
            player.inv[0] = InvObj(objs.shrimps)
            player.inv[1] = InvObj(objs.karambwan)
            player.opHeld(0)
            player.opHeld(1)
            advance(ticks = 1)
            assertNull(player.inv[0])
            assertNull(player.inv[1])
            assertEquals(10 + 3 + 18, player.stat(stats.hitpoints))
        }

    @Test
    fun GameTestState.`attack potion boosts attack and steps down a dose`() =
        runGameTest(ConsumablesScript::class, HeldOpScript::class) {
            player.stats[stats.attack] = 50
            player.inv[0] = InvObj(objs.attack_potion4)
            player.opHeld(0)
            advance(ticks = 1)
            assertEquals(50 + 3 + 5, player.stat(stats.attack))
            assertEquals(objs.attack_potion3.id, player.inv[0]?.id)
            assertMessageSent("You drink some of your attack potion.")
            assertMessageSent("You have 3 doses of potion left.")
        }

    @Test
    fun GameTestState.`last dose leaves an empty vial`() =
        runGameTest(ConsumablesScript::class, HeldOpScript::class) {
            player.inv[0] = InvObj(objs.energy_potion1)
            player.runEnergy = 0
            player.opHeld(0)
            advance(ticks = 1)
            assertEquals(objs.vial_empty.id, player.inv[0]?.id)
            assertMessageSent("You have finished your potion.")
            // 15% of max run energy, plus the tick's natural regeneration.
            assertTrue(player.runEnergy in 1500 until 1600, "runEnergy=${player.runEnergy}")
        }

    @Test
    fun GameTestState.`saradomin brew boosts hitpoints and defence and lowers combat stats`() =
        runGameTest(ConsumablesScript::class, HeldOpScript::class) {
            player.stats[stats.hitpoints] = 90
            player.stats[stats.defence] = 90
            player.stats[stats.attack] = 90
            player.inv[0] = InvObj(objs.saradomin_brew4)
            player.opHeld(0)
            advance(ticks = 1)
            assertEquals(90 + 2 + 13, player.stat(stats.hitpoints))
            assertEquals(90 + 2 + 18, player.stat(stats.defence))
            assertEquals(90 - (2 + 9), player.stat(stats.attack))
        }
}
