package org.rsmod.plugins.api.info

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.game.task.PlayerInfoTask
import org.rsmod.plugins.info.player.PlayerInfo

public object GPIModule : AbstractModule() {

    override fun configure() {
        bind(PlayerInfo::class.java)
            .toProvider(PlayerInfoProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(PlayerInfoTask::class.java)
            .to(SingleThreadedPlayerInfoTask::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
