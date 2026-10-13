package org.runerealm.api.player.protect

import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.seqs
import org.runerealm.api.config.refs.synths
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.script.onOpLoc2
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.factory.locTypeFactory
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class ProtectedAccessPlayerMoveWithMinDelayTest {
    @Test
    fun GameTestState.`pick grain from neighbouring coord`() =
        runGameTest(WheatTestScript::class) {
            val loc = placeMapLoc(CoordGrid(0, 49, 51, 25, 28), wheat)
            val start = loc.coords.translateX(1)
            player.placeAt(start)
            player.enableRun()
            player.opLoc2(loc)
            player.clearInv()

            advance(ticks = 1)
            assertDoesNotContain(player.inv, objs.grain)
            assertEquals(start, player.coords)

            advance(ticks = 1)
            assertContains(player.inv, objs.grain)
            assertEquals(loc.coords, player.coords)
        }

    @Test
    fun GameTestState.`pick grain from diagonal coord`() =
        runGameTest(WheatTestScript::class) {
            val loc = placeMapLoc(CoordGrid(0, 49, 51, 26, 29), wheat)
            val start = loc.coords.translate(-1, -1)
            player.placeAt(start)
            player.enableRun()
            player.opLoc2(loc)
            player.clearInv()

            advance(ticks = 1)
            assertDoesNotContain(player.inv, objs.grain)
            assertEquals(start.translateX(1), player.coords)

            advance(ticks = 1)
            assertDoesNotContain(player.inv, objs.grain)
            assertEquals(start.translateX(1), player.coords)

            advance(ticks = 1)
            assertDoesNotContain(player.inv, objs.grain)
            assertEquals(loc.coords, player.coords)

            advance(ticks = 1)
            assertContains(player.inv, objs.grain)
            assertEquals(loc.coords, player.coords)
        }

    @Test
    fun GameTestState.`pick grain on same coord with full inv`() =
        runGameTest(WheatTestScript::class) {
            val loc = placeMapLoc(CoordGrid(0, 49, 51, 25, 28), wheat)
            player.placeAt(loc.coords)
            player.enableRun()
            player.opLoc2(loc)
            player.fillInv()

            advance(ticks = 1)
            assertMessageSent("You can't carry any more grain.")
            assertDoesNotContain(player.inv, objs.grain)
            assertEquals(loc.coords, player.coords)
        }

    @Test
    fun GameTestState.`pick grain from neighbouring coord with full inv`() =
        runGameTest(WheatTestScript::class) {
            val loc = placeMapLoc(CoordGrid(0, 49, 51, 25, 28), wheat)
            player.placeAt(loc.coords.translateX(1))
            player.enableRun()
            player.opLoc2(loc)
            player.fillInv()

            advance(ticks = 1)
            assertMessageSent("You can't carry any more grain.")
            assertDoesNotContain(player.inv, objs.grain)
            assertNotEquals(loc.coords, player.coords)

            advance(ticks = 1)
            assertEquals(loc.coords, player.coords)
        }

    private class WheatTestScript
    @Inject
    constructor(private val locRepo: LocRepository, private val objRepo: ObjRepository) :
        PluginScript() {
        override fun ScriptContext.startup() {
            onOpLoc2(wheat) { pickCrop(it.loc) }
        }

        private suspend fun ProtectedAccess.pickCrop(loc: BoundLocInfo) {
            arriveDelay()

            if (inv.isFull()) {
                playerWalk(loc.coords)
                mes("You can't carry any more grain.")
                return
            }

            anim(seqs.human_pickupfloor)
            playerWalkWithMinDelay(loc.coords)

            locRepo.del(loc, 20)

            mes("You pick some grain.")
            soundSynth(synths.pick)
            invAddOrDrop(objRepo, objs.grain)
        }
    }

    private companion object {
        private val wheat =
            locTypeFactory.create {
                name = "Wheat"
                op[1] = "Pick"
                width = 1
                length = 1
                blockWalk = 0
                blockRange = false
            }
    }
}
