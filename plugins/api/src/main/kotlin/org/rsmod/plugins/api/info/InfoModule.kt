package org.rsmod.plugins.api.info

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.plugins.api.info.player.PlayerInfoProvider
import org.rsmod.plugins.api.info.player.PlayerInfoTask
import org.rsmod.plugins.info.player.PlayerInfo

public object InfoModule : AbstractModule() {

    override fun configure() {
        bind(PlayerInfoTask::class.java).`in`(Scopes.SINGLETON)

        bind(PlayerInfo::class.java)
            .toProvider(PlayerInfoProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
