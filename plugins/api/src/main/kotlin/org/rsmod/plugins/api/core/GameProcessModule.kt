package org.rsmod.plugins.api.core

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.rsmod.game.GameProcess
import org.rsmod.game.coroutines.GameCoroutineScope

public object GameProcessModule : AbstractModule() {

    override fun configure() {
        bind(GameProcess::class.java)
            .to(MainGameProcess::class.java)
            .`in`(Scopes.SINGLETON)

        bind(GameCoroutineScope::class.java)
            .annotatedWith(GameProcessScope::class.java)
            .to(GameCoroutineScope::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
