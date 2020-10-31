package gg.rsmod.plugins.api.update.player

import gg.rsmod.game.update.task.UpdateTaskList
import gg.rsmod.plugins.api.update.player.task.PlayerMovementTask
import gg.rsmod.plugins.api.update.player.task.PlayerPostUpdateTask
import gg.rsmod.plugins.api.update.player.task.PlayerUpdateTask

val movementTask: PlayerMovementTask by inject()
val updateTask: PlayerUpdateTask by inject()
val postUpdateTask: PlayerPostUpdateTask by inject()

val tasks: UpdateTaskList by inject()

tasks.register {
    -movementTask
    -updateTask
    -postUpdateTask
}
