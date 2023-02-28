package org.rsmod.plugins.api.cache.map.xtea

import org.rsmod.game.job.boot.GameBootTaskScheduler

private val loader: XteaFileLoader by inject()
private val scheduler: GameBootTaskScheduler by inject()

scheduler.scheduleNonBlocking { loader.load() }
