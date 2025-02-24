package org.rsmod.api.game.process.npc.mode

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.game.process.npc.NpcMovementProcessor
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcFactory
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.game.entity.npc.NpcPatrol
import org.rsmod.game.entity.npc.NpcPatrolWaypoint
import org.rsmod.game.map.collision.add
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.routefinder.flag.CollisionFlag

class NpcPatrolModeProcessorTest {
    /**
     * When a patrolling NPC is teleported after not moving for the acceptable amount of time, it
     * will not only teleport to its next waypoint, but also immediately queue up its next step to
     * be handled on the same cycle. (patrol/npc modes are processed before movement)
     */
    @Test
    fun GameTestState.`immediately queue movement after teleport`() = runBasicGameTest {
        withCollisionState {
            val patrol = NpcPatrol(LUMBRIDGE_CASTLE_PATROL)
            val start = patrol[0].destination.translateX(-2)
            val type = npcTypeFactory.create { this.patrol = patrol }
            val hans = npcFactory.create(type, start) { patrolWaypointIndex = 0 }
            it.allocateCollision(MapSquareKey(50, 50))
            it.collision.add(patrol[0].destination, CollisionFlag.LOC)
            withNpc(hans) {
                val movement = NpcMovementProcessor(it.collision, it.stepFactory, eventBus)
                val mode = NpcPatrolModeProcessor(it.collision)
                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    mode.process(this)
                    movement.process(this)
                }
                // Move the npc next to the blocked waypoint.
                process()

                // Process until teleport delay hits.
                val teleDelay = IDLE_TELE_DELAY + 1
                repeat(teleDelay) {
                    process()
                    assertEquals(start.translateX(1), coords)
                    assertEquals(patrol[0].destination, routeDestination.peekLast())
                }

                // This is the point where the npc will teleport to the waypoint destination, but
                // should also end up walking one tile towards the following waypoint.
                process()
                assertNotEquals(patrol[0].destination, coords)
                assertEquals(patrol[1].destination, routeDestination.peekLast())

                // This is where the npc will end up, one tile diagonal of where the waypoint was.
                val overstep = patrol[0].destination.translate(1, -1)
                assertEquals(overstep, coords)
            }
        }
    }

    /**
     * When a patrolling NPC is teleported after not moving for the acceptable amount of time, it
     * will not only teleport to its next waypoint, but also immediately queue up its next step to
     * be handled on the same cycle. (patrol/npc modes are processed before movement)
     *
     * Having explained this; if the next waypoint has a defined pause delay, then this behaviour
     * becomes slightly different. Instead of walking one step, it will count as a cycle of being
     * stood still waiting for the pause delay to pass. This means that if a regular waypoint had a
     * pause delay of 11 cycles, and the NPC is teleported to it, it will effectively become a 10
     * cycle pause delay. One less cycle than if the NPC had simply walked there.
     */
    @Test
    fun GameTestState.`emulate pause delay timing after teleport`() = runBasicGameTest {
        withCollisionState {
            val patrol = NpcPatrol(LUMBRIDGE_CASTLE_PATROL)
            val start = patrol[2].destination
            val blocked = patrol[3].destination.translateZ(4)
            val type = npcTypeFactory.create { this.patrol = patrol }
            val hans = npcFactory.create(type, start) { patrolWaypointIndex = 2 }
            it.allocateCollision(MapSquareKey(50, 50))
            it.collision.add(blocked, CollisionFlag.LOC)
            withNpc(hans) {
                val movement = NpcMovementProcessor(it.collision, it.stepFactory, eventBus)
                val mode = NpcPatrolModeProcessor(it.collision)
                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    mode.process(this)
                    movement.process(this)
                }
                // Move the npc next to the blocked square.
                repeat(3) { process() }
                check(coords == blocked.translateZ(1))

                // Start timing from this point when npc is standing still.
                val startClock = currentMapClock

                // Process until teleport delay hits.
                val teleDelay = IDLE_TELE_DELAY + 1
                repeat(teleDelay) {
                    process()
                    assertEquals(blocked.translateZ(1), coords)
                    assertEquals(patrol[3].destination, routeDestination.peekLast())
                    assertEquals(0, patrolPauseCycles)
                }

                // When the teleport delay hits, there is still a one clock delay before the npc
                // is actually teleported.
                process()
                // The npc should be on top of the waypoint and not stuck behind the blocked square.
                assertEquals(patrol[3].destination, coords)
                // The teleport sequence should have taken `[IDLE_TELE_DELAY] + 2` cycles.
                assertEquals(IDLE_TELE_DELAY + 2, currentMapClock - startClock)

                // This is what makes the difference in our pause time: the patrol pause cycle
                // should have instantly been increased following the teleport.
                assertEquals(1, patrolPauseCycles)

                val pauseDelay = patrol[3].pauseDelay
                repeat(pauseDelay - patrolPauseCycles) {
                    process()
                    assertEquals(patrol[3].destination, coords)
                }

                // On the final pause delay cycle, we should begin to move, as the [patrolPauseTick]
                // was greater than usual as the pause began.
                process()
                assertEquals(patrol[3].destination.translateX(1), coords)

                // Given the standard delay values - this entire sequence should have taken 32
                // cycles for the teleport to move the npc onto the waypoint, and 10 cycles for the
                // pause delay to wear off and for the npc to begin moving onto the next waypoint.
                val sequenceDuration = IDLE_TELE_DELAY + 2 + pauseDelay
                assertEquals(sequenceDuration, currentMapClock - startClock)
            }
        }
    }

    /**
     * NPCs should have to wait [NpcPatrolWaypoint.pauseDelay] + 1 cycles standing still after
     * reaching said waypoint before they can resume patrolling.
     */
    @Test
    fun GameTestState.`emulate pause delay timing`() = runBasicGameTest {
        withCollisionState {
            val patrol = NpcPatrol(LUMBRIDGE_CASTLE_PATROL)
            val start = patrol[2].destination
            val type = npcTypeFactory.create { this.patrol = patrol }
            val hans = npcFactory.create(type, start) { patrolWaypointIndex = 2 }
            it.allocateCollision(MapSquareKey(50, 50))
            withNpc(hans) {
                val movement = NpcMovementProcessor(it.collision, it.stepFactory, eventBus)
                val mode = NpcPatrolModeProcessor(it.collision)
                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    mode.process(this)
                    movement.process(this)
                }

                // Process until npc is next to the waypoint with the delay.
                repeat(7) { index ->
                    val steps = index + 1
                    process()
                    assertEquals(start.translateZ(-steps), coords)
                }

                // Make sure we didn't overstep into said waypoint.
                assertNotEquals(patrol[3].destination, coords)

                // Now step into the waypoint where the npc should now wait for the waypoint's
                // respective pause delay.
                process()

                // Start timing from this point when npc is standing still.
                val startClock = currentMapClock

                val pauseDelay = patrol[3].pauseDelay
                repeat(pauseDelay) {
                    process()
                    assertEquals(patrol[3].destination, coords)
                    assertTrue(routeDestination.isEmpty())
                }

                // When the pause delay has passed, there is still a one cycle delay before the npc
                // begins moving.
                process()

                // The npc should have the next waypoint set as its destination.
                assertEquals(patrol[4].destination, routeDestination.peekLast())
                assertEquals(patrol[3].destination.translateX(1), coords)

                // The exact time this sequence should have taken is
                // `[NpcPatrolWaypoint.pauseDelay] + 1.`
                assertEquals(pauseDelay + 1, currentMapClock - startClock)
            }
        }
    }

    /**
     * NPCs should have to wait [IDLE_TELE_DELAY] + 2 cycles standing still before they actually
     * teleport.
     */
    @Test
    fun GameTestState.`emulate teleport delay timing`() = runBasicGameTest {
        withCollisionState {
            val patrol = NpcPatrol(LUMBRIDGE_CASTLE_PATROL)
            val start = patrol[2].destination
            val type = npcTypeFactory.create { this.patrol = patrol }
            val hans = npcFactory.create(type, start) { patrolWaypointIndex = 2 }
            it.allocateCollision(MapSquareKey(50, 50))
            it.collision.add(start.translateZ(-3), CollisionFlag.LOC)
            withNpc(hans) {
                val movement = NpcMovementProcessor(it.collision, it.stepFactory, eventBus)
                val mode = NpcPatrolModeProcessor(it.collision)
                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    mode.process(this)
                    movement.process(this)
                }

                // Let the npc walk south two tiles up to the loc collision.
                repeat(2) { process() }
                assertEquals(start.translateZ(-2), coords)
                assertEquals(patrol[3].destination, routeDestination.peekLast())

                // Start timing from this point when npc is standing still.
                val startClock = currentMapClock

                // The npc should stay on the same coords until the teleport delay hits.
                val teleDelay = IDLE_TELE_DELAY + 1
                repeat(teleDelay) {
                    process()
                    assertEquals(start.translateZ(-2), coords)
                    assertEquals(patrol[3].destination, routeDestination.peekLast())
                }

                // When the teleport delay hits, there is still a one clock delay before the npc is
                // actually teleported.
                process()

                // The npc should have been taken to the teleport location.
                assertEquals(patrol[3].destination, coords)

                // The exact time this sequence should have taken is `[IDLE_TELE_DELAY] + 2`, or
                // `teleDelay + 1`.
                assertEquals(teleDelay + 1, currentMapClock - startClock)
            }
        }
    }

    @Test
    fun GameTestState.`process hans lumbridge castle patrol`() = runBasicGameTest {
        withCollisionState {
            val patrol = NpcPatrol(LUMBRIDGE_CASTLE_PATROL)
            // Start one tile behind the initial patrol waypoint, so we can time a full run
            // from this CoordGrid to this CoordGrid on the next lap.
            val start = patrol[0].destination.translateX(-1)
            val type = npcTypeFactory.create { this.patrol = patrol }
            val hans = npcFactory.create(type, start)
            it.allocateCollision(MapSquareKey(50, 50))
            withNpc(hans) {
                val movement = NpcMovementProcessor(it.collision, it.stepFactory, eventBus)
                val mode = NpcPatrolModeProcessor(it.collision)
                fun process() {
                    previousCoords = coords
                    currentMapClock++
                    processedMapClock++
                    mode.process(this)
                    movement.process(this)
                }

                // Keep track of start time.
                val startClock = currentMapClock

                repeat(1) { process() }
                assertEquals(patrol[0].destination, coords)

                repeat(4) { process() }
                assertEquals(patrol[1].destination, coords)

                repeat(8) { process() }
                assertEquals(patrol[2].destination, coords)

                repeat(8) { process() }
                assertEquals(patrol[3].destination, coords)

                // This waypoint has a pause delay of 10 cycles, so for the next 10 processes, hans
                // should not move.
                repeat(10) {
                    process()
                    assertEquals(patrol[3].destination, coords)
                }

                // Now we take 2 steps towards the next waypoint.
                repeat(2) { process() }
                assertEquals(patrol[4].destination, coords)

                repeat(10) { process() }
                assertEquals(patrol[5].destination, coords)

                repeat(3) { process() }
                assertEquals(patrol[6].destination, coords)

                repeat(4) { process() }
                assertEquals(patrol[7].destination, coords)

                repeat(12) { process() }
                assertEquals(patrol[8].destination, coords)

                repeat(27) { process() }
                assertEquals(patrol[9].destination, coords)

                repeat(5) { process() }
                assertEquals(patrol[0].destination, coords)

                // The entire patrol, uninterrupted, should take 94 cycles.
                assertEquals(94, currentMapClock - startClock)

                repeat(4) { process() }
                assertEquals(patrol[1].destination, coords)
            }
        }
    }

    private companion object {
        private const val IDLE_TELE_DELAY = NpcPatrolModeProcessor.IDLE_TELE_DELAY

        private val LUMBRIDGE_CASTLE_PATROL =
            listOf(
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 7, 33), 0),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 11, 30), 0),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 19, 30), 0),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 19, 22), 10),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 21, 22), 0),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 21, 12), 0),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 18, 9), 0),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 14, 5), 0),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 2, 5), 0),
                NpcPatrolWaypoint(CoordGrid(0, 50, 50, 2, 32), 0),
            )
    }
}
