package org.rsmod.server.app.modules

import com.google.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.rsmod.module.ExtendedModule
import org.rsmod.scheduler.TaskScheduler

object TaskModule : ExtendedModule() {
    override fun bind() {
        bindProvider(TaskSchedulerProvider::class.java)
    }
}

private class TaskSchedulerProvider : Provider<TaskScheduler> {
    override fun get(): TaskScheduler = TaskScheduler(CoroutineScope(Dispatchers.IO))
}
