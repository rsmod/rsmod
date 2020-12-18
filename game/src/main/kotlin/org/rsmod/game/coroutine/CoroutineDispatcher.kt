package org.rsmod.game.coroutine

import com.google.inject.Inject
import com.google.inject.Provider
import java.util.concurrent.Executor
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

class GameCoroutineScope @Inject constructor(
    @Named("gameCoroutineDispatcher") override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)

class GameCoroutineDispatcherProvider @Inject constructor(
    private val executor: GameExecutor
) : Provider<CoroutineDispatcher> {

    override fun get(): CoroutineDispatcher {
        return executor.asCoroutineDispatcher()
    }
}

class GameExecutor(
    private val executor: Executor
) : Executor by executor

class IoCoroutineScope @Inject constructor(
    @Named("ioCoroutineDispatcher") override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)
