@file:Suppress("UNUSED")

package org.rsmod.game.pathfinder.benchmarks

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
import org.rsmod.game.pathfinder.PathFinder
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

open class GameClickShortPath : PathFinderBenchmark("short-path.json")
open class GameClickMedPath : PathFinderBenchmark("med-path.json")
open class GameClickLongPath : PathFinderBenchmark("long-path.json")
open class GameClickAltPath : PathFinderBenchmark("outofbound-path.json")

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 1, time = 5)
@Fork(value = 1, warmups = 2)
abstract class PathFinderBenchmark(
    private val parameterResourceName: String,
    private val pathRequests: Int = 2000
) {

    private lateinit var params: PathFinderParameter
    private lateinit var pfer: PathFinder
    private lateinit var scope: CoroutineScope

    @Setup
    fun setup() {
        val stream = PathFinderBenchmark::class.java.getResourceAsStream(parameterResourceName)
        val mapper = ObjectMapper(JsonFactory())
        params = stream.use { mapper.readValue(it, PathFinderParameter::class.java) }
        pfer = PathFinder(params.toCollisionFlags())

        val executor = ForkJoinPool(Runtime.getRuntime().availableProcessors())
        val dispatcher = executor.asCoroutineDispatcher()
        scope = CoroutineScope(dispatcher)
    }

    @Benchmark
    fun sequentialPathFinder() {
        val (level, srcX, srcZ, destX, destZ) = params
        repeat(pathRequests) {
            pfer.findPath(level = level, srcX = srcX, srcZ = srcZ, destX = destX, destZ = destZ)
        }
    }

    @Benchmark
    fun threadLocalPathFinder() = runBlocking {
        val (level, srcX, srcZ, destX, destZ) = params
        val flags = params.toCollisionFlags()
        val threadLocal = ThreadLocal.withInitial { PathFinder(flags) }

        fun CoroutineScope.findPath() = launch {
            val pf = threadLocal.get()
            pf.findPath(level = level, srcX = srcX, srcZ = srcZ, destX = destX, destZ = destZ)
        }

        launch(scope.coroutineContext) {
            repeat(pathRequests) {
                findPath()
            }
        }.join()
    }
}

private data class PathFinderParameter(
    val level: Int,
    val srcX: Int,
    val srcZ: Int,
    val destX: Int,
    val destZ: Int,
    val flags: IntArray
) {

    constructor() : this(0, 0, 0, 0, 0, intArrayOf())

    fun toCollisionFlags(): CollisionFlagMap {
        val collisionFlags = CollisionFlagMap()
        val mapSearchSize = sqrt(flags.size.toDouble()).toInt()
        val half = mapSearchSize / 2
        val centerX = srcX
        val centerZ = srcZ
        val rangeX = centerX - half until centerX + half
        val rangeZ = centerZ - half until centerZ + half
        for (z in rangeZ) {
            for (x in rangeX) {
                val lx = x - (centerX - half)
                val lz = z - (centerZ - half)
                val index = (lz * mapSearchSize) + lx
                collisionFlags[x, z, level] = flags[index]
            }
        }
        return collisionFlags
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathFinderParameter

        if (level != other.level) return false
        if (srcX != other.srcX) return false
        if (srcZ != other.srcZ) return false
        if (destX != other.destX) return false
        if (destZ != other.destZ) return false
        if (!flags.contentEquals(other.flags)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level
        result = 31 * result + srcX
        result = 31 * result + srcZ
        result = 31 * result + destX
        result = 31 * result + destZ
        result = 31 * result + flags.contentHashCode()
        return result
    }
}
