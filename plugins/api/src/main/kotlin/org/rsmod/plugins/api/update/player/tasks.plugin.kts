package org.rsmod.plugins.api.update.player

import org.rsmod.game.update.task.UpdateTaskList
import org.rsmod.plugins.api.update.player.task.PlayerMovementTask
import org.rsmod.plugins.api.update.player.task.PlayerPostUpdateTask
import org.rsmod.plugins.api.update.player.task.PlayerUpdateTask

val movementTask: PlayerMovementTask by inject()
val updateTask: PlayerUpdateTask by inject()
val postUpdateTask: PlayerPostUpdateTask by inject()

val tasks: UpdateTaskList by inject()

tasks.register {
    -movementTask
    -updateTask
    -postUpdateTask
}
