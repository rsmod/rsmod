package org.rsmod.game.coroutine

import com.google.inject.Inject
import com.google.inject.name.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

public class GameCoroutineScope @Inject constructor(
    @Named("gameCoroutineDispatcher") override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)
