package org.rsmod.game.dispatcher.io

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
public class IOCoroutineScope @Inject constructor(
    @IOCoroutineDispatcher override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)
