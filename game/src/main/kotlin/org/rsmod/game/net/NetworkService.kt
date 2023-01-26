package org.rsmod.game.net

import com.google.common.util.concurrent.AbstractService
import io.netty.channel.EventLoopGroup
import org.rsmod.game.config.GameConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class NetworkService @Inject constructor(
    private val config: GameConfig,
    private val bootstrapFactory: NetworkBootstrapFactory,
    private val initHandler: NetworkChannelInitializer
) : AbstractService() {

    private lateinit var group: EventLoopGroup

    override fun doStart() {
        val port = config.port
        group = bootstrapFactory.createEventLoopGroup()
        val bootstrap = bootstrapFactory.createBootstrap(group)
        bootstrap.childHandler(initHandler)
        val bind = bootstrap.bind(port).awaitUninterruptibly()
        if (!bind.isSuccess) {
            group.shutdownGracefully()
            notifyFailed(bind.cause())
            error("Could not bind to port $port.")
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
