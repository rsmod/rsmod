package gg.rsmod.plugins.core.update

import gg.rsmod.game.model.mob.update.UpdateTaskList
import gg.rsmod.plugins.core.update.player.PlayerMovementTask
import gg.rsmod.plugins.core.update.player.PlayerPostCycleTask
import gg.rsmod.plugins.core.update.player.PlayerUpdateTask

val playerMovementTask: PlayerMovementTask by inject()
val playerUpdateTask: PlayerUpdateTask by inject()
val playerPostCycleTask: PlayerPostCycleTask by inject()

val tasks: UpdateTaskList by inject()

tasks.register {
    -playerMovementTask
    -playerUpdateTask
    -playerPostCycleTask
}
