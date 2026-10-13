package org.runerealm.api.freeze

import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.walktriggers
import org.runerealm.api.random.GameRandom
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.factory.npcTypeFactory
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.game.entity.npc.NpcMode
import org.runerealm.game.type.npc.UnpackedNpcType
import org.runerealm.game.type.util.ParamMap
import org.runerealm.game.type.walktrig.isType
import org.runerealm.map.CoordGrid

class FreezeTest {
    @Test
    fun GameTestState.`frozen npc cannot move until the freeze ends`() = runFreezeTest { deps ->
        val npc = spawnNpc(START, dummy)
        advance()
        assertTrue(npc.freeze(cycles = 4, deps.random))

        npc.walk(START.translateX(3))
        advance(ticks = 3)
        assertEquals(START, npc.coords)
        assertTrue(npc.isFrozen())

        advance()
        assertFalse(npc.isFrozen())
        npc.walk(START.translateX(3))
        advance()
        assertEquals(START.translateX(1), npc.coords)
    }

    @Test
    fun GameTestState.`entity is immune to freezes while frozen and shortly after`() =
        runFreezeTest { deps ->
            val npc = spawnNpc(START, dummy)
            advance()
            assertTrue(npc.freeze(cycles = 2, deps.random))
            assertFalse(npc.freeze(cycles = 10, deps.random))
            assertEquals(2, npc.remainingFreezeCycles())

            advance(ticks = 2)
            assertFalse(npc.isFrozen())
            assertTrue(npc.isFreezeImmune())
            assertFalse(npc.freeze(cycles = 10, deps.random))

            advance(ticks = FREEZE_IMMUNITY_CYCLES)
            assertFalse(npc.isFreezeImmune())
            assertTrue(npc.freeze(cycles = 10, deps.random))
        }

    @Test
    fun GameTestState.`npc freeze resistance is a percentage chance to resist`() =
        runFreezeTest { deps ->
            val immune = spawnNpc(START, dummyWithFreezeResistance(100))
            assertFalse(immune.freeze(cycles = 10, deps.random))
            assertFalse(immune.isFrozen())

            val resistant = spawnNpc(START.translateX(2), dummyWithFreezeResistance(50))
            advance()
            random.next = 49
            assertFalse(resistant.freeze(cycles = 10, deps.random))
            random.next = 50
            assertTrue(resistant.freeze(cycles = 10, deps.random))
        }

    @Test
    fun GameTestState.`frozen player cannot walk and the walk trigger clears on thaw`() =
        runFreezeTest { deps ->
            player.placeAt(START)
            advance()
            assertTrue(player.freeze(cycles = 4))

            player.moveGameClick(START.translateX(3))
            advance()
            assertEquals(START, player.coords)
            // The walk trigger is set again to keep blocking movement.
            assertTrue(player.walkTrigger.isType(walktriggers.frozen))

            advance(ticks = 3)
            assertFalse(player.isFrozen())
            assertNull(player.walkTrigger)

            player.moveGameClick(START.translateX(3))
            advance()
            assertNotEquals(START, player.coords)
        }

    @Test
    fun GameTestState.`pvp freeze ends once the source is more than 10 tiles away`() =
        runFreezeTest { deps ->
            player.placeAt(START)
            val source = registerPlayer(START.translateX(PVP_FREEZE_MAX_DISTANCE))
            advance()
            assertTrue(player.freeze(cycles = 32, source = source))
            assertTrue(player.walkTrigger.isType(walktriggers.pvp_frozen))

            // Still in range: movement is blocked.
            player.moveGameClick(START.translateZ(3))
            advance()
            assertEquals(START, player.coords)
            assertTrue(player.isFrozen())

            // Only x/z are compared, so a different level does not break the freeze.
            source.placeAt(source.coords.translateLevel(1))
            player.moveGameClick(START.translateZ(3))
            advance()
            assertEquals(START, player.coords)
            assertTrue(player.isFrozen())

            source.placeAt(START.translateX(PVP_FREEZE_MAX_DISTANCE + 1))
            player.moveGameClick(START.translateZ(3))
            advance()
            assertNotEquals(START, player.coords)
            assertFalse(player.isFrozen())
            assertTrue(player.isFreezeImmune())
        }

    @Test
    fun GameTestState.`clearing a freeze also removes freeze immunity`() = runFreezeTest { deps ->
        val npc = spawnNpc(START, dummy)
        advance()
        assertTrue(npc.freeze(cycles = 10, deps.random))
        npc.clearFreeze()
        assertFalse(npc.isFrozen())
        assertFalse(npc.isFreezeImmune())
        assertNull(npc.walkTrigger)
    }

    private fun GameTestState.runFreezeTest(
        testBody: GameTestScope.(FreezeTestDependencies) -> Unit
    ) =
        runInjectedGameTest(
            FreezeTestDependencies::class,
            null,
            FreezeScript::class,
            testBody = testBody,
        )

    class FreezeTestDependencies @Inject constructor(val random: GameRandom)

    private fun dummyWithFreezeResistance(resistance: Int): UnpackedNpcType {
        val id = params.freeze_resistance.id
        val paramMap = ParamMap(mapOf(id to resistance), mapOf(id to resistance))
        return npcTypeFactory.create {
            name = "Resistant dummy"
            size = 1
            hitpoints = 100
            defaultMode = NpcMode.None
            this.paramMap = paramMap
        }
    }

    private companion object {
        private val START = CoordGrid(0, 50, 50, 0, 0)

        private val dummy =
            npcTypeFactory.create {
                name = "Dummy"
                size = 1
                hitpoints = 100
                defaultMode = NpcMode.None
            }
    }
}
