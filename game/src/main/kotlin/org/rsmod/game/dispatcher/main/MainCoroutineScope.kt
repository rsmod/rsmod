package org.rsmod.game.dispatcher.main

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
public class MainCoroutineScope @Inject constructor(
    @MainCoroutineDispatcher override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)
