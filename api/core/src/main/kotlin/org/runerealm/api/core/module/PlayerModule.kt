package org.runerealm.api.core.module

import com.google.inject.Provider
import jakarta.inject.Inject
import org.runerealm.api.player.music.MusicPlayer
import org.runerealm.api.player.protect.ProtectedAccessContextFactory
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.entity.util.ShuffledPlayerList
import org.runerealm.module.ExtendedModule

public object PlayerModule : ExtendedModule() {
    override fun bind() {
        bindInstance<MusicPlayer>()
        bindInstance<ProtectedAccessContextFactory>()
        bindInstance<ProtectedAccessLauncher>()
        bindProvider(ShuffledPlayerListProvider::class.java)
    }

    private class ShuffledPlayerListProvider
    @Inject
    constructor(private val playerList: PlayerList) : Provider<ShuffledPlayerList> {
        override fun get(): ShuffledPlayerList = ShuffledPlayerList(playerList)
    }
}
