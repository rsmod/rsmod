package org.rsmod.game.coroutine

import com.google.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Named

class IoCoroutineScope @Inject constructor(
    @Named("ioCoroutineDispatcher") override val coroutineContext: CoroutineDispatcher
) : CoroutineScope by CoroutineScope(coroutineContext)
