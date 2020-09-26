package gg.rsmod.plugins.core.update

import gg.rsmod.game.model.mob.update.UpdateTaskList
import gg.rsmod.plugins.core.update.player.PlayerMovementTask
import gg.rsmod.plugins.core.update.player.PlayerPostCycleTask
import gg.rsmod.plugins.core.update.player.PlayerUpdateTask

val mapRebuildTask: PlayerMovementTask by inject()
val updateTask: PlayerUpdateTask by inject()
val postCycleTask: PlayerPostCycleTask by inject()

val tasks: UpdateTaskList by inject()

tasks.register {
    -mapRebuildTask
    -updateTask
    -postCycleTask
}
