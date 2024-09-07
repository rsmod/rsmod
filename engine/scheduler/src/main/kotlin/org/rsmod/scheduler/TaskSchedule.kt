package org.rsmod.scheduler

import kotlinx.coroutines.CoroutineScope

internal class TaskSchedule(
    val tasks: MutableList<suspend CoroutineScope.() -> Unit> = mutableListOf()
) : List<suspend CoroutineScope.() -> Unit> by tasks {
    operator fun plusAssign(task: suspend CoroutineScope.() -> Unit) {
        tasks += task
    }
}
