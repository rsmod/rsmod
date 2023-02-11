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
import org.rsmod.plugins.info.player.PlayerInfo
import org.rsmod.plugins.info.player.extended.ExtendedInfo
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

/*open class PlayerInfoSmall : PlayerInfoBenchmark(250)
open class PlayerInfoMedium : PlayerInfoBenchmark(500)
open class PlayerInfoLarge : PlayerInfoBenchmark(1000)*/
open class PlayerInfoMax : PlayerInfoBenchmark(2047)

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 1, time = 5)
@Fork(value = 1, warmups = 0)
abstract class PlayerInfoBenchmark(private val playerCapacity: Int) {

    private lateinit var info: PlayerInfo
    private lateinit var buffers: Array<ByteBuffer>
    private lateinit var appearanceData: ByteArray

    @Setup
    fun setup() {
        info = PlayerInfo(playerCapacity)
        buffers = Array(info.playerCapacity) { ByteBuffer.allocate(40_000) }
        appearanceData = ByteArray(ExtendedInfo.APPEARANCE_MAX_BYTE_SIZE - 1)
        for (i in 0 until info.playerCapacity) info.registerClient(i)
    }

    @Benchmark
    fun readInfoBuffersAtCapacityNoSkips() {
        for (i in 1 until info.playerCapacity) {
            info.add(i, 3200000, 0)
        }
        for (playerIndex in 1 until info.playerCapacity) {
            val buf = buffers[playerIndex].clear()
            info.read(buf, playerIndex)
        }
        info.clear()
    }

    @Benchmark
    fun readInfoBuffersAtCapacityAllowSkips() {
        for (i in 1 until info.playerCapacity) {
            info.add(i, 3200000, 3200000)
        }
        for (playerIndex in 1 until info.playerCapacity) {
            val buf = buffers[playerIndex].clear()
            info.read(buf, playerIndex)
        }
        info.clear()
    }

    @Benchmark
    fun readInfoWithAppBuffersAtCapacityNoSkips() {
        for (i in 1 until info.playerCapacity) {
            info.add(i, 3200000, 0)
            info.setAppearance(i, appearanceData, appearanceData.size)
        }
        for (playerIndex in 1 until info.playerCapacity) {
            val buf = buffers[playerIndex].clear()
            info.read(buf, playerIndex)
        }
        info.clear()
    }

    @Benchmark
    fun readInfoWithAppBuffersAtCapacityAllowSkips() {
        for (i in 1 until info.playerCapacity) {
            info.add(i, 3200000, 3200000)
            info.setAppearance(i, appearanceData, appearanceData.size)
        }
        for (playerIndex in 1 until info.playerCapacity) {
            val buf = buffers[playerIndex].clear()
            info.read(buf, playerIndex)
        }
        info.clear()
    }
}
