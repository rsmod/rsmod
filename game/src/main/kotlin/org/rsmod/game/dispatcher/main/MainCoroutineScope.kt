package org.rsmod.game.dispatcher.main

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
public class MainCoroutineScope @Inject constructor(
    @MainCoroutineDispatcher override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)
