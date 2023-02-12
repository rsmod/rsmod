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
import org.rsmod.plugins.info.player.PlayerInfo
import org.rsmod.plugins.info.player.extended.ExtendedInfoSizes.APPEARANCE_MAX_BYTE_SIZE
import org.rsmod.plugins.info.player.extended.ExtendedInfoSizes.TOTAL_BYTE_SIZE
import org.rsmod.plugins.info.player.extended.ExtendedMetadata
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

open class PlayerInfo250 : PlayerInfoBenchmark(250)
open class PlayerInfo500 : PlayerInfoBenchmark(500)
open class PlayerInfo1000 : PlayerInfoBenchmark(1000)
open class PlayerInfo2047 : PlayerInfoBenchmark(2047)

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 1, time = 5)
@Fork(value = 1, warmups = 0)
abstract class PlayerInfoBenchmark(private val playerCapacity: Int) {

    private lateinit var info: PlayerInfo
    private lateinit var buffers: Array<ByteBuffer>

    @Setup
    fun setup() {
        /* allocate enough memory for a _ton_ of extended info data */
        val bufferLimitPerClient = 1_250_000
        info = PlayerInfo(playerCapacity, bufferLimitPerClient)
        buffers = Array(info.playerCapacity) { ByteBuffer.allocate(bufferLimitPerClient) }

        val appearanceData = ByteArray(APPEARANCE_MAX_BYTE_SIZE)
        for (i in 0 until info.playerCapacity) {
            info.initialize(i, 0x40, appearanceData)
        }
    }

    @Benchmark
    fun singlePlayerWithFullExtendedInfo(bh: Blackhole) {
        val index = 5
        val extended = ByteArray(TOTAL_BYTE_SIZE - 1)
        val buffer = buffers[index].clear()
        info.clear()
        info.add(playerIndex = index, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        info.setExtendedInfo(playerIndex = index, maskFlags = 0x2000, data = extended)
        info.putFully(buffer, playerIndex = index)
        bh.consume(buffer)
    }

    @Benchmark
    fun maxPlayersWithNoUpdates(bh: Blackhole) {
        info.clear()
        for (i in 0 until playerCapacity) {
            info.add(playerIndex = i, currCoords = coords(3200, 3200), prevCoords = coords(3200, 3200))
        }
        for (i in 0 until playerCapacity) {
            val buffer = buffers[i].clear()
            info.putFully(buffer, playerIndex = i)
            bh.consume(buffer)
        }
    }

    @Benchmark
    fun maxPlayersAddAllLowResToHighRes(bh: Blackhole) {
        info.clear()
        for (i in 0 until playerCapacity) {
            info.add(playerIndex = i, currCoords = coords(8400, 8400), prevCoords = coords(3200, 3200))
        }
        for (i in 0 until playerCapacity) {
            val buffer = buffers[i].clear()
            info.putFully(buffer, playerIndex = i)
            bh.consume(buffer)
        }
    }

    @Benchmark
    fun maxPlayersWithLowResLargeTeleportChange(bh: Blackhole) {
        info.clear()
        for (i in 0 until playerCapacity) {
            info.add(playerIndex = i, currCoords = coords(8400, 8400), prevCoords = coords(3200, 3200))
        }
        for (i in 0 until playerCapacity) {
            val buffer = buffers[i].clear()
            // Don't want to be within "view distance" of other avatars.
            info.avatars[i].currCoords = coords(1400, 1400)
            info.putFully(buffer, playerIndex = i)
            // Now set it back so others can do same as above.
            info.avatars[i].currCoords = coords(8400, 8400)
            bh.consume(buffer)
        }
    }

    @Benchmark
    fun maxPlayersWithHighResLargeTeleportChange(bh: Blackhole) {
        info.clear()
        for (playerIndex in 0 until playerCapacity) {
            info.add(
                playerIndex = playerIndex,
                currCoords = coords(8400, 8400),
                prevCoords = coords(3200, 3200)
            )
            for (otherIndex in 0 until playerCapacity) {
                info.clients[playerIndex].highRes[otherIndex] = true
            }
        }
        for (i in 0 until playerCapacity) {
            val buffer = buffers[i].clear()
            info.putFully(buffer, playerIndex = i)
            bh.consume(buffer)
        }
    }

    @Benchmark
    fun maxPlayersWith512BytesExtendedInfoEach(bh: Blackhole) {
        /* each byte is copied to personal buffer so can use this for all */
        val data = ByteArray(512)
        info.clear()
        for (playerIndex in 0 until playerCapacity) {
            info.add(
                playerIndex = playerIndex,
                currCoords = coords(3200, 3200),
                prevCoords = coords(3200, 3200)
            )
            info.setExtendedInfo(playerIndex = playerIndex, maskFlags = 0x2000, data = data)
            for (otherIndex in 0 until playerCapacity) {
                info.clients[playerIndex].highRes[otherIndex] = true
            }
        }
        for (i in 0 until playerCapacity) {
            val buffer = buffers[i].clear()
            val extended = ExtendedMetadata()
            info.putFully(buffer, playerIndex = i, extended)
            bh.consume(buffer)
        }
    }

    private companion object {

        private fun coords(x: Int, y: Int, level: Int = 0): Int {
            return (y and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)
        }
    }
}
