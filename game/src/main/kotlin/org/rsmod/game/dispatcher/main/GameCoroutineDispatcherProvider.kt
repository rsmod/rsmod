package org.rsmod.game.dispatcher.main

import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Provider

private const val EXECUTOR_NAME = "GameExecutor"

public class GameCoroutineDispatcherProvider : Provider<CoroutineDispatcher> {

    override fun get(): CoroutineDispatcher {
        val factory = ThreadFactoryBuilder().setDaemon(false).setNameFormat(EXECUTOR_NAME).build()
        val executor = Executors.newSingleThreadExecutor(factory)
        return executor.asCoroutineDispatcher()
    }
}
