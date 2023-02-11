package org.rsmod.game.coroutine

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.game.coroutines.GameCoroutineScope

public object GameCoroutineModule : AbstractModule() {

    override fun configure() {
        bind(GameCoroutineScope::class.java)
            .toProvider(GameCoroutineScopeProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
