@file:Suppress("UNUSED")

package org.rsmod.plugins.info.player

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
import org.rsmod.plugins.info.model.coord.HighResCoord
import org.rsmod.plugins.info.player.PlayerInfo.Companion.CACHED_EXT_INFO_BUFFER_SIZE
import java.nio.ByteBuffer
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 1, time = 5)
@Fork(value = 1, warmups = 2)
abstract class SingleThreadPlayerInfoBenchmark(
    private val bufCapacity: Int,
    private val startInHighRes: Boolean = false
) {

    private lateinit var info: PlayerInfo
    private lateinit var buf: ByteBuffer
    private lateinit var staticExtInfo: ByteArray

    private val random: ThreadLocalRandom get() = ThreadLocalRandom.current()

    @Setup
    fun setup() {
        info = PlayerInfo()
        buf = ByteBuffer.allocate(bufCapacity)
        staticExtInfo = ByteArray(CACHED_EXT_INFO_BUFFER_SIZE)
        if (startInHighRes) {
            for (i in info.indices) {
                for (j in info.indices) {
                    info.clients[i].isHighResolution[j] = true
                }
            }
        }
    }

    @Benchmark
    fun registerAndUpdateMaxPlayersNoExtInfo(bh: Blackhole) {
        for (i in info.indices) {
            info.register(i)
        }
        for (i in info.indices) {
            bh.consume(info.put(buf, i))
        }
        for (i in info.indices) {
            info.unregister(i)
        }
    }

    @Benchmark
    fun registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo(bh: Blackhole) {
        for (i in info.indices) {
            info.register(i)
            info.cacheStaticExtendedInfo(i, staticExtInfo)
        }
        for (i in info.indices) {
            bh.consume(info.put(buf, i))
        }
        for (i in info.indices) {
            info.unregister(i)
        }
    }

    @Benchmark
    fun registerAndUpdateMaxHighResPlayersWithMovement(bh: Blackhole) {
        for (i in info.indices) {
            info.register(i)
            val coords = HighResCoord(random.nextInt(3200, 3213), random.nextInt(3200, 3213))
            info.updateCoords(i, coords, coords)
        }
        for (i in info.indices) {
            bh.consume(info.put(buf, i))
        }
        for (i in info.indices) {
            info.unregister(i)
        }
    }
}
