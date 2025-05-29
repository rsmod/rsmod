package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import kotlin.collections.set
import org.junit.jupiter.api.Test
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.player.output.mes
import org.rsmod.api.random.CoreRandom
import org.rsmod.api.random.GameRandom
import org.rsmod.api.script.onAiOpPlayer2
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.locTypeFactory
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.api.testing.random.FixedRandom
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.map.Direction
import org.rsmod.game.map.translate
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class NpcUnderTargetInteractionProcessorTest {
    @Test
    fun GameTestState.`step away when under target and skip interaction turn`() =
        runInjectedGameTest(
            StaticRandomDependency::class,
            childModule = null,
            OpTestScript::class,
        ) {
            val npcCoord = CoordGrid(0, 1, 1, 3, 3)

            // Add blocking crate locs around the npc coords.
            val expectedDir = Direction.South
            val deleteCrate = placeMapLoc(npcCoord.translate(expectedDir), crate)
            placeMapLoc(npcCoord.translate(Direction.North), crate)
            placeMapLoc(npcCoord.translate(Direction.West), crate)
            placeMapLoc(npcCoord.translate(Direction.East), crate)

            // Set the static random value for CoreRandom, which is used by the npc processor to
            // pick a random direction to route towards when under the target.
            val expectedDirRandom = Direction.CARDINAL.indexOf(expectedDir)
            it.setStaticRandom(expectedDirRandom)
            val checkDir = it.random.pick(Direction.CARDINAL)
            check(checkDir == expectedDir) {
                "Expected random to pick `$expectedDir` from " +
                    "index `$expectedDirRandom` but was `$checkDir`"
            }

            val playerCoord = deleteCrate.coords
            player.placeAt(playerCoord)

            val npc = spawnNpc(npcCoord, man)
            // Easy way of keeping track of target player for npc op script.
            npc.vars.backing[0] = player.slotId
            npc.opPlayer2(player)
            advance(ticks = 1)
            assertMessageSent("Receive attack.")

            // Teleport player under npc; as the npc is surrounded by blocking locs, it cannot move
            // to attack player.
            player.teleport(npcCoord)
            advance(ticks = 1)
            assertMessageNotSent("Receive attack.")

            // Delete one of the blocking locs so that the npc can step away from under target.
            delLoc(deleteCrate)
            // Set the random direction pick to an unexpected value to verify that the npc does
            // not move when choosing a random, blocked destination.
            it.setStaticRandom(expectedDirRandom + 1)
            advance(ticks = 1)
            assertEquals(npcCoord, npc.coords)
            assertMessageNotSent("Receive attack.")

            // Ensure npc has not moved or attacked the player one cycle later, just in case.
            advance(ticks = 1)
            assertEquals(npcCoord, npc.coords)
            assertMessageNotSent("Receive attack.")

            // Now set the static random to the expected direction, which should lead to the npc
            // routing properly and stepping away from the player.
            it.setStaticRandom(expectedDirRandom)
            advance(ticks = 1)
            assertEquals(npcCoord.translate(expectedDir), npc.coords)
            // Stepping away from the target while under them forces the npc to skip their
            // interaction turn. This means that they must wait an extra cycle before they can
            // "attack."
            assertMessageNotSent("Receive attack.")

            // Now, the npc should be able to attack their target.
            advance(ticks = 1)
            assertMessageSent("Receive attack.")
        }

    /** Ensures that the Red-X mechanic works as expected. */
    @Test
    fun GameTestState.`skip interaction turn when under player target and they are busy`() =
        runInjectedGameTest(
            StaticRandomDependency::class,
            childModule = null,
            OpTestScript::class,
        ) {
            val npcCoord = CoordGrid(0, 1, 1, 3, 3)

            // Add blocking crate locs around the npc coords.
            val expectedDir = Direction.South
            val deleteCrate = placeMapLoc(npcCoord.translate(expectedDir), crate)
            val northCrate = placeMapLoc(npcCoord.translate(Direction.North), crate)
            placeMapLoc(npcCoord.translate(Direction.West), crate)
            placeMapLoc(npcCoord.translate(Direction.East), crate)

            // Set the static random value for CoreRandom, which is used by the npc processor to
            // pick a random direction to route towards when under the target.
            val expectedDirRandom = Direction.CARDINAL.indexOf(expectedDir)
            it.setStaticRandom(expectedDirRandom)
            val checkDir = it.random.pick(Direction.CARDINAL)
            check(checkDir == expectedDir) {
                "Expected random to pick `$expectedDir` from " +
                    "index `$expectedDirRandom` but was `$checkDir`"
            }

            val playerCoord = deleteCrate.coords
            player.placeAt(playerCoord)

            val npc = spawnNpc(npcCoord, man)
            // Easy way of keeping track of target player for npc op script.
            npc.vars.backing[0] = player.slotId
            npc.opPlayer2(player)
            advance(ticks = 1)
            assertMessageSent("Receive attack.")

            // Teleport player under npc; as the npc is surrounded by blocking locs, it cannot move
            // to attack player.
            player.teleport(npcCoord)
            advance(ticks = 1)
            assertMessageNotSent("Receive attack.")

            // Delete one of the blocking locs so that the npc could step away from under target
            // if they were not busy.
            delLoc(deleteCrate)
            player.opLoc1(northCrate)
            advance(ticks = 1)
            assertEquals(npcCoord, npc.coords)
            assertMessageNotSent("Receive attack.")

            // As long as the player keeps clicking on the op, the npc should not be able to
            // step away.
            repeat(3) {
                player.opLoc1(northCrate)
                advance(ticks = 1)
                assertEquals(npcCoord, npc.coords)
                assertMessageNotSent("Receive attack.")
            }

            // Now that the player is not actively interacting with something, the npc should be
            // able to step away.
            advance(ticks = 1)
            assertEquals(npcCoord.translate(expectedDir), npc.coords)
            // Stepping away from the target while under them forces the npc to skip their
            // interaction turn. This means that they must wait an extra cycle before they can
            // "attack."
            assertMessageNotSent("Receive attack.")

            // The npc should be able to attack their target.
            advance(ticks = 1)
            assertMessageSent("Receive attack.")
        }

    private class OpTestScript @Inject constructor(private val playerList: PlayerList) :
        PluginScript() {
        override fun ScriptContext.startup() {
            onAiOpPlayer2(man) { combatAp() }
        }

        private fun StandardNpcAccess.combatAp() {
            val targetSlot = npc.vars.backing[0]
            val target = playerList[targetSlot]
            checkNotNull(target) { "Invalid target with assigned target slot: $targetSlot" }
            // Npc interactions automatically persist and repeat without `opX` calls.
            target.mes("Receive attack.")
        }
    }

    private class StaticRandomDependency @Inject constructor(@CoreRandom val random: GameRandom) {
        fun setStaticRandom(value: Int) {
            if (random !is FixedRandom) {
                val message = "Expected test CoreRandom to be a FixedRandom implementation: $random"
                throw IllegalArgumentException(message)
            }
            random.set(value)
        }
    }

    private companion object {
        private val man =
            npcTypeFactory.create {
                name = "Man"
                op[1] = "Attack"
                size = 1
                maxRange = 100
            }

        private val crate =
            locTypeFactory.create {
                name = "Crate"
                op[0] = "Search"
                width = 1
                length = 1
                blockWalk = 1
            }
    }
}
