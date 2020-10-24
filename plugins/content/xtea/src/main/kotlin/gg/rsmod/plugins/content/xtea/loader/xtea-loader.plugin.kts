package gg.rsmod.plugins.content.xtea.loader

import gg.rsmod.game.task.StartupTaskList

val tasks: StartupTaskList by inject()
val loader: XteaFileLoader by inject()

tasks.registerNonBlocking {
    loader.load()
}
