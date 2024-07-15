package org.rsmod.game.dispatcher.io

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Singleton
public class IOCoroutineScope @Inject constructor(
    @IOCoroutineDispatcher override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)
