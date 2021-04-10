package org.rsmod.plugins.api.spawn

import org.rsmod.game.task.StartupTaskList

val loader: NpcSpawnLoader by inject()
val tasks: StartupTaskList by inject()

tasks.registerNonBlocking {
    loader.spawnAll()
}
