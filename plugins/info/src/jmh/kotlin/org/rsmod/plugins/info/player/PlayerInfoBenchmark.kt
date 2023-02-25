@file:Suppress("UNUSED")

package org.rsmod.plugins.info.player

open class SingleThreadNoBufLimit : SingleThreadPlayerInfoBenchmark(bufCapacity = 200_000)
open class SingleThreadBufLimited : SingleThreadPlayerInfoBenchmark(bufCapacity = 40_000)
open class SingleThreadBufLimitedStartHighRes :
    SingleThreadPlayerInfoBenchmark(bufCapacity = 40_000, startInHighRes = true)

open class MultiThreadNoBufLimit : MultiThreadPlayerInfoBenchmark(bufCapacity = 200_000)
open class MultiThreadBufLimited : MultiThreadPlayerInfoBenchmark(bufCapacity = 40_000)
open class MultiThreadBufLimitedStartHighRes :
    MultiThreadPlayerInfoBenchmark(bufCapacity = 40_000, startInHighRes = true)
