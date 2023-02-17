@file:Suppress("UNUSED")

package org.rsmod.plugins.info

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
import org.rsmod.plugins.info.PlayerInfo.Companion.CACHED_EXT_INFO_BUFFER_SIZE
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

open class SimplePlayerInfoBenchmark : PlayerInfoBenchmark()

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 1, time = 5)
@Fork(value = 1, warmups = 0)
abstract class PlayerInfoBenchmark {

    private lateinit var info: PlayerInfo
    private lateinit var buf: ByteBuffer
    private lateinit var staticExtInfo: ByteArray

    @Setup
    fun setup() {
        info = PlayerInfo()
        buf = ByteBuffer.allocate(200_000)
        staticExtInfo = ByteArray(CACHED_EXT_INFO_BUFFER_SIZE)
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
}
