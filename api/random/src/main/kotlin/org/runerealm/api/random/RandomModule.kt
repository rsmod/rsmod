package org.runerealm.api.random

import com.google.inject.Provider
import com.google.inject.Scopes
import org.runerealm.module.ExtendedModule

public object RandomModule : ExtendedModule() {
    override fun bind() {
        bind(GameRandom::class.java)
            .annotatedWith(CoreRandom::class.java)
            .toProvider(CoreRandomProvider::class.java)
            .`in`(Scopes.SINGLETON)
        bindProvider(GameRandomProvider::class.java)
    }
}

private class GameRandomProvider : Provider<GameRandom> {
    override fun get(): GameRandom = DefaultGameRandom(seed = System.currentTimeMillis())
}

private class CoreRandomProvider : Provider<GameRandom> {
    override fun get(): GameRandom = DefaultGameRandom(seed = System.currentTimeMillis())
}
