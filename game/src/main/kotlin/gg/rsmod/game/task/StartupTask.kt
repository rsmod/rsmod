package gg.rsmod.game.task

import com.google.inject.Inject

internal inline class StartupTask(internal val block: () -> Unit)

class StartupTaskList internal constructor(
    internal val blocking: MutableList<StartupTask>,
    internal val nonBlocking: MutableList<StartupTask>
) {

    @Inject
    constructor() : this(mutableListOf(), mutableListOf())

    fun registerNonBlocking(block: () -> Unit) = nonBlocking.add(StartupTask(block))

    fun registerBlocking(block: () -> Unit) = blocking.add(StartupTask(block))
}
