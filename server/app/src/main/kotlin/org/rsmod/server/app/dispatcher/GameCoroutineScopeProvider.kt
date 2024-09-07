package org.rsmod.server.app.dispatcher

import com.google.inject.Provider
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class GameCoroutineScopeProvider
@Inject
constructor(@GameCoroutineDispatcher private val dispatcher: CoroutineDispatcher) :
    Provider<CoroutineScope> {
    override fun get(): CoroutineScope = CoroutineScope(dispatcher)
}
