package org.rsmod.plugins.api.update.player

import org.rsmod.game.update.task.UpdateTaskList
import org.rsmod.plugins.api.update.player.task.PlayerPreUpdateTask
import org.rsmod.plugins.api.update.player.task.PlayerPostUpdateTask
import org.rsmod.plugins.api.update.player.task.PlayerUpdateTask

val preUpdateTask: PlayerPreUpdateTask by inject()
val updateTask: PlayerUpdateTask by inject()
val postUpdateTask: PlayerPostUpdateTask by inject()

val tasks: UpdateTaskList by inject()

tasks.register {
    -preUpdateTask
    -updateTask
    -postUpdateTask
}
