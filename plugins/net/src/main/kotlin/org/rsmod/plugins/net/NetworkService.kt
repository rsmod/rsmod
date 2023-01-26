package org.rsmod.plugins.net

import com.google.common.util.concurrent.AbstractService
import io.netty.channel.EventLoopGroup
import org.rsmod.plugins.net.bootstrap.NetworkBootstrapFactory
import org.rsmod.plugins.net.service.ServiceChannelInitializer
import javax.inject.Inject
import javax.inject.Singleton

private const val PORT = 43594

@Singleton
class NetworkService @Inject constructor(
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
