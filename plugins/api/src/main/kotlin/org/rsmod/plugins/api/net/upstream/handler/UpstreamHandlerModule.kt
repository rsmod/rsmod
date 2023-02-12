package org.rsmod.plugins.api.net.upstream.handler

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder
import org.rsmod.game.task.UpstreamTask

internal object UpstreamHandlerModule : AbstractModule() {

    private val handlers = listOf(
        MoveGameClickHandler::class.java
    )

    override fun configure() {
        val binder = Multibinder.newSetBinder(binder(), GENERIC_HANDLER_LITERAL)
        handlers.forEach { binder.addBinding().to(it) }

        bind(UpstreamHandlerMap::class.java)
            .toProvider(UpstreamHandlerMapProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(UpstreamTask::class.java)
            .to(UpstreamHandlerTask::class.java)
            .`in`(Scopes.SINGLETON)
    }

    private val GENERIC_HANDLER_LITERAL = object : TypeLiteral<UpstreamHandler<*>>() {}
}
