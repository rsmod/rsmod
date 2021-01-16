package org.rsmod.game.update.task

import com.github.michaelbull.logging.InlineLogger

private val logger = InlineLogger()

interface UpdateTask {

    suspend fun execute()
}

class UpdateTaskList(
    private val tasks: MutableList<UpdateTask> = mutableListOf()
) : List<UpdateTask> by tasks {

    fun register(init: UpdateTaskBuilder.() -> Unit) {
        UpdateTaskBuilder(tasks).apply(init)
    }
}

@DslMarker
private annotation class TaskBuilderDslMarker

@TaskBuilderDslMarker
class UpdateTaskBuilder(private val tasks: MutableList<UpdateTask>) {

    operator fun <T : UpdateTask> T.unaryMinus() {
        logger.debug { "Append update task to list (task=${this::class.simpleName})" }
        tasks.add(this)
    }
}
