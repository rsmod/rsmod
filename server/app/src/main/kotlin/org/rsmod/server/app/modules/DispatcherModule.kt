package org.rsmod.server.app.modules

import com.google.inject.Scopes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.rsmod.module.ExtendedModule
import org.rsmod.server.app.dispatcher.GameCoroutineDispatcher
import org.rsmod.server.app.dispatcher.GameCoroutineDispatcherProvider
import org.rsmod.server.app.dispatcher.GameCoroutineScope
import org.rsmod.server.app.dispatcher.GameCoroutineScopeProvider

object DispatcherModule : ExtendedModule() {
    override fun bind() {
        bind(CoroutineDispatcher::class.java)
            .annotatedWith(GameCoroutineDispatcher::class.java)
            .toProvider(GameCoroutineDispatcherProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(CoroutineScope::class.java)
            .annotatedWith(GameCoroutineScope::class.java)
            .toProvider(GameCoroutineScopeProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }
}
