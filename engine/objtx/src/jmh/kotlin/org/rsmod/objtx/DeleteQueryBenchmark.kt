package org.rsmod.objtx

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole

open class InventoryDeleteBenchmark :
    DeleteQueryBenchmark(size = 28, TransactionInventory.NormalStack)

open class BankDeleteBenchmark :
    DeleteQueryBenchmark(size = 1220, TransactionInventory.AlwaysStack, placeholders = true)

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 3, warmups = 2)
@Warmup(iterations = 2)
@Measurement(iterations = 3, time = 5)
abstract class DeleteQueryBenchmark(private val inventory: Inventory) {
    constructor(
        size: Int,
        stackType: TransactionInventory.StackType,
        placeholders: Boolean = false,
    ) : this(Inventory(stackType, arrayOfNulls(size), placeholders))

    @Setup(Level.Invocation)
    fun setup() {
        inventory.objs.fill(Obj(1))
    }

    @Benchmark
    fun deleteFull(bh: Blackhole) {
        val result = transaction {
            val inv = select(inventory)
            for (i in inventory.indices) {
                delete {
                    from = inv
                    obj = 1
                    strictCount = 1
                    strictSlot = i
                }
            }
        }
        bh.consume(result)
    }

    @Benchmark
    fun deleteSingle(bh: Blackhole) {
        val result = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = 1
                strictCount = 1
                strictSlot = 0
            }
        }
        bh.consume(result)
    }
}
