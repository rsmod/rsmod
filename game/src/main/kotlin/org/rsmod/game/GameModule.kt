package org.rsmod.game

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.Scope
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import org.rsmod.buffer.BufferModule
import org.rsmod.game.coroutine.CoroutineModule
import org.rsmod.game.net.NetworkModule
import org.rsmod.game.net.NetworkService

public class GameModule(private val scope: Scope = Scopes.SINGLETON) : AbstractModule() {

    override fun configure() {
        install(BufferModule)
        install(CoroutineModule(scope))
        install(NetworkModule(scope))
        configureServices()
    }

    private fun configureServices() {
        val binder = Multibinder.newSetBinder(binder(), Service::class.java)
        binder.addBinding().to(GameService::class.java).`in`(scope)
        binder.addBinding().to(NetworkService::class.java).`in`(scope)
    }
}
