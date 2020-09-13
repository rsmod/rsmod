package gg.rsmod.game.coroutine

import com.google.inject.Inject
import com.google.inject.Provider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor
import javax.inject.Named

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
