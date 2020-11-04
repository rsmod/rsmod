package org.rsmod.plugins.api.cache.map.xtea.loader

import org.rsmod.game.task.StartupTaskList

val tasks: StartupTaskList by inject()
val loader: XteaFileLoader by inject()

tasks.registerNonBlocking {
    loader.load()
}
