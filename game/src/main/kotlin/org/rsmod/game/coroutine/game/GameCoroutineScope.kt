package org.rsmod.game.coroutine.game

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class GameCoroutineScope @Inject constructor(
    @GameCoroutineDispatcher override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)
