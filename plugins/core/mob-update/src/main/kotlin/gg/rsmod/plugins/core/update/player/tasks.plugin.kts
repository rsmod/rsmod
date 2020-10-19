package gg.rsmod.plugins.core.update.player

import gg.rsmod.game.update.task.UpdateTaskList

val movementTask: PlayerMovementTask by inject()
val updateTask: PlayerUpdateTask by inject()
val postUpdateTask: PlayerPostUpdateTask by inject()

val tasks: UpdateTaskList by inject()

tasks.register {
    -movementTask
    -updateTask
    -postUpdateTask
}
