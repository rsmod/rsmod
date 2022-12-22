package org.rsmod.game.net

import com.google.common.util.concurrent.AbstractService
import io.netty.channel.EventLoopGroup
import javax.inject.Inject

// TODO: replace with NetConfig data class
private const val GAME_PORT = 43594

public class NetworkService @Inject constructor(
    private val bootstrapFactory: NetworkBootstrapFactory,
    private val initHandler: NetworkChannelInitializer
) : AbstractService() {

    private lateinit var group: EventLoopGroup

    override fun doStart() {
        group = bootstrapFactory.createEventLoopGroup()
        val bootstrap = bootstrapFactory.createBootstrap(group)
        bootstrap.childHandler(initHandler)
        val bind = bootstrap.bind(GAME_PORT).awaitUninterruptibly()
        if (!bind.isSuccess) {
            group.shutdownGracefully()
            notifyFailed(bind.cause())
            error("Could not bind to port $GAME_PORT.")
        }
        notifyStarted()
        println("bound to port $GAME_PORT")
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
