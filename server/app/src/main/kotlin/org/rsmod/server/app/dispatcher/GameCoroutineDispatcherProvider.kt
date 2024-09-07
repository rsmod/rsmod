package org.rsmod.server.app.dispatcher

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.Provider
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.rsmod.server.app.GAME_THREAD_EXECUTOR_NAME

class GameCoroutineDispatcherProvider : Provider<CoroutineDispatcher> {
    override fun get(): CoroutineDispatcher {
        val factory =
            ThreadFactoryBuilder().setDaemon(false).setNameFormat(GAME_THREAD_EXECUTOR_NAME).build()
        val executor = Executors.newSingleThreadExecutor(factory)
        return executor.asCoroutineDispatcher()
    }
}
