package org.rsmod.game.coroutine

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.Provider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

public class GameCoroutineDispatcherProvider : Provider<CoroutineDispatcher> {

    override fun get(): CoroutineDispatcher {
        val factory = ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("GameExecutor")
            .build()
        val executor = Executors.newSingleThreadExecutor(factory)
        return executor.asCoroutineDispatcher()
    }
}
