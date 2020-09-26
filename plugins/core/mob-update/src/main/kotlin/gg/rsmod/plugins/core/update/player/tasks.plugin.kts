package gg.rsmod.plugins.core.update.player

import gg.rsmod.game.model.mob.update.UpdateTaskList

val movementTask: PlayerMovementTask by inject()
val updateTask: PlayerUpdateTask by inject()
val postCycleTask: PlayerPostCycleTask by inject()

val tasks: UpdateTaskList by inject()

tasks.register {
    -movementTask
    -updateTask
    -postCycleTask
}
