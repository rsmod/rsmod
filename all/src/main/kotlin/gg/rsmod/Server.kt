package gg.rsmod

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Scopes
import dev.misfitlabs.kotlinguice4.getInstance
import gg.rsmod.game.Game
import gg.rsmod.game.GameModule
import gg.rsmod.game.action.ActionBus
import gg.rsmod.game.cache.CacheModule
import gg.rsmod.game.cache.GameCache
import gg.rsmod.game.config.ConfigModule
import gg.rsmod.game.config.GameConfig
import gg.rsmod.game.coroutine.CoroutineModule
import gg.rsmod.game.coroutine.IoCoroutineScope
import gg.rsmod.game.dispatch.DispatcherModule
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.cmd.CommandMap
import gg.rsmod.game.plugin.kotlin.KotlinModuleLoader
import gg.rsmod.game.plugin.kotlin.KotlinPluginLoader
import gg.rsmod.game.task.StartupTaskList
import gg.rsmod.game.task.launchBlocking
import gg.rsmod.game.task.launchNonBlocking
import gg.rsmod.net.NetworkModule
import gg.rsmod.net.channel.ClientChannelInitializer
import gg.rsmod.net.handshake.HandshakeDecoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress

private val logger = InlineLogger()

fun main() {
    val server = Server()
    server.startup()
}

class Server {

    fun startup() {
        logger.info { "Starting up server - please wait..." }

        val scope = Scopes.SINGLETON

        val moduleLoader = KotlinModuleLoader(scope)
        val moduleScripts = moduleLoader.load()
        val modules = moduleScripts.flatMap { it.modules }

        val injector = Guice.createInjector(
            ServerModule(scope),
            CoroutineModule(scope),
            DispatcherModule(scope),
            ConfigModule(scope),
            CacheModule(scope),
            GameModule(scope),
            NetworkModule(scope),
            *modules.toTypedArray()
        )
        val gameConfig: GameConfig = injector.getInstance()
        logger.info { "Launching ${gameConfig.name}" }

        val cache: GameCache = injector.getInstance()
        cache.start()

        val eventBus: EventBus = injector.getInstance()
        val actions: ActionBus = injector.getInstance()
        val commands: CommandMap = injector.getInstance()
        val pluginLoader = KotlinPluginLoader(injector, eventBus, actions, commands)
        val plugins = pluginLoader.load()

        val ioCoroutineScope: IoCoroutineScope = injector.getInstance()
        val startupTasks: StartupTaskList = injector.getInstance()
        startupTasks.launchNonBlocking(ioCoroutineScope)
        startupTasks.launchBlocking()

        val game: Game = injector.getInstance()
        game.start()

        bind(injector)

        logger.info { "Loaded ${plugins.size} plugin(s)" }
        logger.debug { "Loaded game with configuration: $gameConfig" }
        logger.info { "Game listening to connections on port ${gameConfig.port}" }
    }

    private fun bind(injector: Injector) {
        val channelInitializer = ClientChannelInitializer(
            handshakeDecoder = { injector.getInstance<HandshakeDecoder>() }
        )

        val bootstrap = ServerBootstrap()
        bootstrap.channel(NioServerSocketChannel::class.java)
        bootstrap.childHandler(channelInitializer)
        bootstrap.group(NioEventLoopGroup(2), NioEventLoopGroup(1))

        val config: GameConfig = injector.getInstance()
        val bind = bootstrap.bind(InetSocketAddress(config.port)).awaitUninterruptibly()
        if (!bind.isSuccess) {
            error("Could not bind game port.")
        }
    }
}
