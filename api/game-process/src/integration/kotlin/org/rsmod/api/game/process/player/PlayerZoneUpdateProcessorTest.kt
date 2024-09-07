package org.rsmod.api.game.process.player

import net.rsprot.protocol.api.util.ZonePartialEnclosedCacheBuffer
import net.rsprot.protocol.game.outgoing.zone.header.UpdateZoneFullFollows
import net.rsprot.protocol.game.outgoing.zone.payload.LocAddChange
import net.rsprot.protocol.game.outgoing.zone.payload.ObjAdd
import net.rsprot.protocol.game.outgoing.zone.payload.ObjCount
import net.rsprot.protocol.game.outgoing.zone.payload.ObjDel
import net.rsprot.protocol.message.ZoneProt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.game.process.player.PlayerZoneUpdateProcessor.Companion.ZONE_VIEW_RADIUS
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.api.testing.GameTestScope
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.capture.attachClientCapture
import org.rsmod.api.testing.factory.collisionFactory
import org.rsmod.api.testing.factory.locRegistryFactory
import org.rsmod.api.testing.factory.locTypeListFactory
import org.rsmod.api.testing.factory.objRegistryFactory
import org.rsmod.api.testing.factory.objTypeListFactory
import org.rsmod.api.testing.factory.playerFactory
import org.rsmod.api.testing.factory.smallBlockWalk
import org.rsmod.api.testing.factory.stackable1
import org.rsmod.api.testing.factory.standard1
import org.rsmod.api.utils.map.zone.SharedZoneEnclosedBuffers
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.game.obj.Obj
import org.rsmod.game.obj.ObjEntity
import org.rsmod.game.obj.ObjScope
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.pathfinder.collision.CollisionFlagMap

class PlayerZoneUpdateProcessorTest {
    @Test
    fun GameTestState.`process zones in a new build area`() = runAdvancedGameTest {
        val parameters = createZoneProcess()
        val buildProcessor = PlayerBuildAreaProcessor()
        val zoneProcessor = parameters.zoneProcessor
        val locRegistry = parameters.locRegistry
        val objRegistry = parameters.objRegistry
        val standardLoc = parameters.locTypes.smallBlockWalk().id
        val standardObj = parameters.objTypes.standard1().id
        val startCoords = CoordGrid(0, 1, 1, 32, 32)
        withPlayer(startCoords) {
            val captured = attachClientCapture()

            // Set player observer uuid to view `obj`.
            val observer = 1L
            uuid = observer
            observerUUID = observer

            // NOTE: This may not always be the case. Was told that players' personal zone radius
            // can vary. (i.e., enlarges while inside Tombs of Amascut)
            val zoneRadius = ZONE_VIEW_RADIUS
            val zoneRange = -zoneRadius..zoneRadius

            fun process() {
                // Clear zone updates from previous tick.
                zoneProcessor.clearPendingZoneUpdates()
                // Clear zone prots from previous tick.
                client.flush()
                buildProcessor.process(this)
                zoneProcessor.process(this)
            }

            val startZone = ZoneKey.from(startCoords)
            val expectedZoneKeys =
                zoneRange.flatMap { x -> zoneRange.map { z -> startZone.translate(x, z).packed } }

            // Set a static map loc in loc registry; this loc should not be sent as a zone
            // update, or so we will verify.
            val mapLoc =
                LocInfo(
                    layer = 3,
                    coords = startCoords.translate(0, 1),
                    entity = LocEntity(standardLoc, 10, 0),
                )
            mapLoc.let {
                val zoneKey = ZoneKey.from(it.coords)
                val zoneGrid = ZoneGrid.from(it.coords)
                val locZoneKey = LocZoneKey(zoneGrid.x, zoneGrid.z, it.layer)
                locRegistry.mapLocs[zoneKey, locZoneKey] = mapLoc.entity
            }

            // Spawn a loc in loc registry to be sent as a zone update.
            val loc =
                LocInfo(
                    layer = 0,
                    coords = startCoords.translate(2, 2),
                    entity = LocEntity(standardLoc, 10, 0),
                )
            locRegistry.add(loc)

            // Spawn an obj in obj registry to be sent as a zone update.
            val obj =
                Obj(
                    coords = startCoords.translate(1, 2),
                    entity = ObjEntity(standardObj, 1, ObjScope.Private.id),
                    creationCycle = 0,
                    receiverId = observer,
                )
            objRegistry.add(obj)

            process()

            // `processNewVisibleZones`
            assertEquals(49, captured.countOf<UpdateZoneFullFollows>())

            // `refreshVisibleZoneKeys`
            assertEquals(expectedZoneKeys.toSet(), visibleZoneKeys.toSet())
            assertEquals(startZone, lastProcessedZone)

            // `processVisibleZoneUpdates`
            assertEquals(2, captured.count { it is ZoneProt })
            assertEquals(1, captured.countOf<LocAddChange>())
            assertEquals(standardLoc, captured.singleMapOf(LocAddChange::id))
            assertEquals(1, captured.countOf<ObjAdd>())
            assertEquals(standardObj, captured.singleMapOf(ObjAdd::id))

            process()

            // Should no longer send update follows.
            assertEquals(0, captured.countOf<UpdateZoneFullFollows>())

            // Visible zones should have stayed the same.
            assertEquals(expectedZoneKeys.toSet(), visibleZoneKeys.toSet())

            // No zone update should have been sent.
            assertEquals(0, captured.count())
        }
    }

    @Test
    fun GameTestState.`only send private obj to receiver`() = runAdvancedGameTest {
        val parameters = createZoneProcess()
        val buildProcessor = PlayerBuildAreaProcessor()
        val zoneProcessor = parameters.zoneProcessor
        val zoneUpdateMap = parameters.zoneUpdateMap
        val objRegistry = parameters.objRegistry
        val stackableObj = parameters.objTypes.stackable1().id
        val startCoords = CoordGrid(0, 1, 1, 32, 32)

        val player1 =
            playerFactory.create(startCoords) {
                uuid = 1
                observerUUID = 1
            }

        val player2 =
            playerFactory.create(startCoords) {
                uuid = 2
                observerUUID = 2
            }

        val client1 = player1.attachClientCapture()
        val client2 = player2.attachClientCapture()

        fun process() {
            buildProcessor.process(player1)
            buildProcessor.process(player2)
            zoneProcessor.process(player1)
            zoneProcessor.process(player2)
        }

        fun endTick() {
            zoneProcessor.clearPendingZoneUpdates()
            client1.flush()
            client2.flush()
        }

        // Skip the initial set-up phase for new build area.
        process()
        endTick()

        // Add a new obj to be sent as an `ObjAdd` update.
        val obj =
            Obj(
                coords = startCoords.translate(1, 0),
                entity = ObjEntity(stackableObj, 1, ObjScope.Private.id),
                creationCycle = 0,
                receiverId = checkNotNull(player2.observerUUID),
            )
        objRegistry.add(obj)

        process()
        check(zoneUpdateMap.updatedZones.size == 1)

        // Obj should be sent to `client2`.
        assertEquals(1, client2.count { it is ZoneProt })
        assertEquals(1, client2.countOf<ObjAdd>())
        assertEquals(stackableObj, client2.singleMapOf(ObjAdd::id))

        // `client1` should be oblivious to the Obj.
        assertEquals(0, client1.count { it is ZoneProt })
        assertEquals(0, client1.count())

        endTick()

        // Add the same type of obj on top of the existing stack, this should trigger an
        // `ObjCount`.
        val countObj =
            Obj(
                coords = startCoords.translate(1, 0),
                entity = ObjEntity(stackableObj, 4, ObjScope.Private.id),
                creationCycle = 0,
                receiverId = checkNotNull(player2.observerUUID),
            )
        objRegistry.add(countObj)

        process()

        // Obj count should be updated for `client2`.
        assertEquals(1, client2.count { it is ZoneProt })
        assertEquals(1, client2.countOf<ObjCount>())
        assertEquals(stackableObj, client2.singleMapOf(ObjCount::id))
        assertEquals(1, client2.singleMapOf(ObjCount::oldQuantity))
        assertEquals(5, client2.singleMapOf(ObjCount::newQuantity))

        // `client1` should be oblivious to the Obj.
        assertEquals(0, client1.count { it is ZoneProt })
        assertEquals(0, client1.count())

        endTick()

        // Finally, delete the obj.
        val deleteObj = objRegistry.findAll(obj.coords).single()
        objRegistry.del(deleteObj)

        process()

        // Obj should be deleted for `client2`.
        assertEquals(1, client2.count { it is ZoneProt })
        assertEquals(1, client2.countOf<ObjDel>())
        assertEquals(stackableObj, client2.singleMapOf(ObjDel::id))
        assertEquals(5, client2.singleMapOf(ObjDel::quantity))

        // `client1` should be oblivious to the Obj.
        assertEquals(0, client1.count { it is ZoneProt })
        assertEquals(0, client1.count())

        endTick()
    }

    private fun GameTestScope.createZoneProcess(): ZoneProcessParams {
        val zoneUpdates = ZoneUpdateMap()
        val collision = collisionFactory.borrowSharedMap()
        val locTypes = locTypeListFactory.createDefault()
        val objTypes = objTypeListFactory.createDefault()
        val locRegistry = locRegistryFactory.create(collision, zoneUpdates, locTypes)
        val objRegistry = objRegistryFactory.create(zoneUpdates, objTypes)
        val enclosedCache = ZonePartialEnclosedCacheBuffer()
        val sharedEnclosed = SharedZoneEnclosedBuffers(playerList, zoneUpdates, enclosedCache)
        val processor =
            PlayerZoneUpdateProcessor(zoneUpdates, locRegistry, objRegistry, sharedEnclosed)
        return ZoneProcessParams(
            collision,
            locRegistry,
            objRegistry,
            zoneUpdates,
            processor,
            locTypes,
            objTypes,
        )
    }

    private data class ZoneProcessParams(
        val collision: CollisionFlagMap,
        val locRegistry: LocRegistry,
        val objRegistry: ObjRegistry,
        val zoneUpdateMap: ZoneUpdateMap,
        val zoneProcessor: PlayerZoneUpdateProcessor,
        val locTypes: LocTypeList,
        val objTypes: ObjTypeList,
    )
}
