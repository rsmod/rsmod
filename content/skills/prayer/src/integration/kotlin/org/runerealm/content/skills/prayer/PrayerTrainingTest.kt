package org.runerealm.content.skills.prayer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.stats
import org.runerealm.api.inv.HeldOpScript
import org.runerealm.api.inv.LocUOpScript
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.stat.statSub
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.inv.InvObj
import org.runerealm.map.CoordGrid

@Execution(ExecutionMode.SAME_THREAD)
class PrayerTrainingTest {
    private val altarCoords = CoordGrid(0, 50, 50, 22, 18)

    @Test
    fun GameTestState.`burying bones grants prayer experience`() =
        runGameTest(PrayerTraining::class, HeldOpScript::class) {
            player.inv[0] = InvObj(objs.bones)
            player.opHeld(0)
            advance(ticks = 1)
            assertNull(player.inv[0])
            assertMessageSent("You dig a hole in the ground...")
            assertEquals(4, player.statMap.getXP(stats.prayer))
            advance(ticks = 1)
            assertMessageSent("You bury the bones.")
        }

    @Test
    fun GameTestState.`praying at an altar recharges prayer points`() =
        runGameTest(PrayerTraining::class) {
            player.stats[stats.prayer] = 43
            player.statSub(stats.prayer, 40, 0)
            allocZoneCollision(altarCoords)
            val type = findLocTypes { it.internalName == "altar" }.single()
            val altar = placeMapLoc(altarCoords, type)
            player.teleport(altarCoords.translateX(-1))
            player.opLoc1(altar)
            // Messages are captured per tick; stop on the tick the points are recharged.
            advanceUntil(predicate = { player.stat(stats.prayer) == 43 })
            assertMessageSent("You recharge your Prayer points.")
        }

    @Test
    fun GameTestState.`chaos altars outside the wilderness temple refuse bones`() =
        runGameTest(PrayerTraining::class, LocUOpScript::class) {
            allocZoneCollision(altarCoords)
            val altar = placeMapLoc(altarCoords, locTypes.getValue(prayer_locs.chaos_altar.id))
            player.teleport(altarCoords.translateX(-1))
            player.inv[0] = InvObj(objs.bones)
            player.opLocU(altar, invSlot = 0)
            advance(ticks = 1)
            assertMessageSent("You fear the wrath of the gods!")
            assertEquals(0, player.statMap.getXP(stats.prayer))
        }

    @Test
    fun GameTestState.`chaos altar grants 350 percent experience`() =
        runGameTest(PrayerTraining::class, LocUOpScript::class) {
            val templeAltar = CoordGrid(2950, 3820, 0)
            allocZoneCollision(templeAltar)
            val altar = placeMapLoc(templeAltar, locTypes.getValue(prayer_locs.chaos_altar.id))
            player.teleport(templeAltar.translateX(-1))
            player.inv[0] = InvObj(objTypes.values.single { it.internalName == "big_bones" })
            random.next = 2 // The bone is consumed.
            player.opLocU(altar, invSlot = 0)
            advance(ticks = 3)
            assertEquals(52, player.statMap.getXP(stats.prayer)) // 15 * 3.5 = 52.5
            assertNull(player.inv[0])
        }
}
