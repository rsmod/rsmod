package gg.rsmod.game.coroutine

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.Provider
import com.google.inject.Scope
import com.google.inject.name.Names
import dev.misfitlabs.kotlinguice4.KotlinModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.Executors

class CoroutineModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<GameExecutor>()
            .toProvider<GameExecutorProvider>()
            .`in`(scope)
        bind<CoroutineDispatcher>()
            .annotatedWith(Names.named("gameCoroutineDispatcher"))
            .toProvider<GameCoroutineDispatcherProvider>()
        bind<GameCoroutineScope>()
            .`in`(scope)

        bind<CoroutineDispatcher>()
            .annotatedWith(Names.named("ioCoroutineDispatcher"))
            .toProvider<IoDispatcherProvider>()
        bind<IoCoroutineScope>()
            .`in`(scope)
    }
}

class GameExecutorProvider : Provider<GameExecutor> {

    override fun get(): GameExecutor {
        val factory = ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("GameExecutor")
            .build()
        val executor = Executors.newSingleThreadExecutor(factory)
        return GameExecutor(executor)
    }
}

class IoDispatcherProvider : Provider<CoroutineDispatcher> {

    override fun get(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
