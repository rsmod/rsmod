package org.rsmod.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

public class TaskScheduler(public val ioScope: CoroutineScope) {
    internal val ioSchedule = TaskSchedule()

    public val size: Int
        get() = ioSchedule.size

    public fun scheduleIO(task: suspend CoroutineScope.() -> Unit) {
        ioSchedule += task
    }

    public fun clear() {
        ioSchedule.tasks.clear()
        ioScope.cancel()
    }

    public fun joinAll(): Unit = runBlocking {
        // Use `async` and `await` to throw any cancellation exceptions
        // on caller thread instead of being suppressed.
        val deferred = ioScope.execute(ioSchedule)
        deferred.await()
    }

    public fun CoroutineScope.execute(
        tasks: List<suspend CoroutineScope.() -> Unit>
    ): Deferred<Unit> = async {
        val jobs = tasks.map { task -> launch { task.invoke(this) } }
        jobs.joinAll()
    }
}
