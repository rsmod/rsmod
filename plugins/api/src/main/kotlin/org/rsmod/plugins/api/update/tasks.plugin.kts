package org.rsmod.plugins.api.update

import org.rsmod.game.update.task.UpdateTaskList
import org.rsmod.plugins.api.update.npc.task.NpcPostUpdateTask
import org.rsmod.plugins.api.update.npc.task.NpcPreUpdateTask
import org.rsmod.plugins.api.update.npc.task.NpcUpdateTask
import org.rsmod.plugins.api.update.player.task.PathFinderTask
import org.rsmod.plugins.api.update.player.task.PlayerPostUpdateTask
import org.rsmod.plugins.api.update.player.task.PlayerPreUpdateTask
import org.rsmod.plugins.api.update.player.task.PlayerUpdateTask

val npcPreUpdateTask: NpcPreUpdateTask by inject()
val npcUpdateTask: NpcUpdateTask by inject()
val npcPostUpdateTask: NpcPostUpdateTask by inject()

val pathFinderTask: PathFinderTask by inject()
val prePlayerUpdateTask: PlayerPreUpdateTask by inject()
val playerUpdateTask: PlayerUpdateTask by inject()
val playerPostUpdateTask: PlayerPostUpdateTask by inject()

val tasks: UpdateTaskList by inject()

tasks.register {
    -pathFinderTask
    -prePlayerUpdateTask
    -npcPreUpdateTask
    -playerUpdateTask
    -npcUpdateTask
    -playerPostUpdateTask
    -npcPostUpdateTask
}
