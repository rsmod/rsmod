@file:Suppress("UNUSED")

package org.rsmod.game.map

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.map.entity.obj.ObjectEntry
import org.rsmod.game.map.entity.obj.ObjectKey
import org.rsmod.game.map.util.collect.MutableObjectMap
import org.rsmod.game.map.util.collect.MutableZoneMap
import org.rsmod.game.map.zone.Zone
import org.rsmod.game.map.zone.ZoneKey
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

open class FullStaticGameMapBenchmark : GameMapBenchmark(zoneCapacity = 190_000, objectsPerZone = 18)

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 1, time = 5)
@Fork(value = 1, warmups = 2)
abstract class GameMapBenchmark(private val zoneCapacity: Int, private val objectsPerZone: Int) {

    private var zones: MutableZoneMap = MutableZoneMap.empty(zoneCapacity)

    @Setup
    fun setup() {
        val sqrt = sqrt(zoneCapacity.toDouble()).toInt()
        for (z in 0 until sqrt) {
            for (x in 0 until sqrt) {
                val objects = MutableObjectMap.empty(objectsPerZone)
                repeat(objectsPerZone) {
                    val key = ObjectKey(
                        it and ObjectKey.X_BIT_MASK,
                        it and ObjectKey.Z_BIT_MASK,
                        it and ObjectKey.SLOT_BIT_MASK
                    )
                    val entity = ObjectEntity(
                        it and ObjectEntity.ID_BIT_MASK,
                        it and ObjectEntity.SHAPE_BIT_MASK,
                        it and ObjectEntity.ROT_BIT_MASK
                    )
                    objects[key.packed] = entity.packed
                }
                val zoneKey = ZoneKey(x, z, 0)
                zones[zoneKey.packed] = Zone(objects.immutable())
            }
        }
    }

    @Benchmark
    fun iterateAllZoneEntries(bh: Blackhole) {
        zones.entrySet().forEach { (key, value) ->
            bh.consume(key)
            bh.consume(value)
        }
    }

    @Benchmark
    fun iterateAllZoneObjectEntries(bh: Blackhole) {
        zones.entrySet().forEach { (zoneKey, zone) ->
            bh.consume(zoneKey)
            zone.entrySet().forEach { (objKey, obj) ->
                bh.consume(objKey)
                bh.consume(obj)
            }
        }
    }

    @Benchmark
    fun listAllZoneObjectEntries(bh: Blackhole) {
        zones.entrySet().forEach { zoneEntry ->
            val zoneKey = ZoneKey(zoneEntry.intKey)
            val zoneCoords = zoneKey.toCoords()
            val objs = zoneEntry.value.entrySet().map { entry ->
                val objKey = ObjectKey(entry.byteKey)
                val coords = zoneCoords.translate(objKey.x, objKey.z)
                ObjectEntry(objKey.slot, coords, ObjectEntity(entry.intValue))
            }
            bh.consume(objs)
        }
    }
}
