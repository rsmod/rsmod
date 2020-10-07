package gg.rsmod.game.model.mob.update

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject

private val logger = InlineLogger()

interface UpdateTask {

    suspend fun execute()
}

class UpdateTaskList(
    private val tasks: MutableList<UpdateTask>
) : List<UpdateTask> by tasks {

    @Inject
    constructor() : this(mutableListOf())

    fun register(init: UpdateTaskBuilder.() -> Unit) {
        UpdateTaskBuilder(tasks).apply(init)
    }
}

@DslMarker
private annotation class TaskDslMarker

@TaskDslMarker
class UpdateTaskBuilder(private val tasks: MutableList<UpdateTask>) {

    operator fun <T : UpdateTask> T.unaryMinus() {
        tasks.add(this)
        logger.debug { "Append update task to list (task=${this.javaClass.simpleName}, totalTasks=${tasks.size})" }
    }
}
