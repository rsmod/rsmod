package org.rsmod.plugins.api.net.upstream.handler

import javax.inject.Inject
import javax.inject.Provider

public class UpstreamHandlerMapProvider @Inject constructor(
    handlers: Set<UpstreamHandler<*>>
) : Provider<UpstreamHandlerMap> {

    private val mapped = handlers.associateBy { it.type }

    override fun get(): UpstreamHandlerMap {
        return UpstreamHandlerMap(mapped)
    }
}
