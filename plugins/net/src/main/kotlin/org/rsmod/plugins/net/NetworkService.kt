package org.rsmod.plugins.net

import com.github.michaelbull.logging.InlineLogger
import com.google.common.util.concurrent.AbstractService
import io.netty.channel.EventLoopGroup
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.plugins.net.service.ServiceChannelInitializer
import org.rsmod.plugins.net.util.NetworkBootstrapFactory

private val logger = InlineLogger()

private const val PORT = 43594

@Singleton
public class NetworkService @Inject constructor(
    private val bootstrapFactory: NetworkBootstrapFactory,
    private val initHandler: ServiceChannelInitializer
) : AbstractService() {

    private lateinit var group: EventLoopGroup

    override fun doStart() {
        group = bootstrapFactory.createEventLoopGroup()
        val bootstrap = bootstrapFactory.createBootstrap(group)
        bootstrap.childHandler(initHandler)
        val bind = bootstrap.bind(PORT).awaitUninterruptibly()
        if (!bind.isSuccess) {
            group.shutdownGracefully()
            notifyFailed(bind.cause())
            error("Could not bind to port $PORT.")
        }
        logger.info { "Network service started - accepting connections from port $PORT." }
        notifyStarted()
    }

    override fun doStop() {
        group.shutdownGracefully().addListener { future ->
            if (future.isSuccess) {
                notifyStopped()
            } else {
                notifyFailed(future.cause())
            }
        }
    }
}
