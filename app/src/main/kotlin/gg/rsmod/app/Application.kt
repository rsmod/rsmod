package gg.rsmod.app

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Scopes
import dev.misfitlabs.kotlinguice4.getInstance
import gg.rsmod.game.GameModule
import gg.rsmod.game.cache.CacheModule
import gg.rsmod.game.cache.GameCache
import gg.rsmod.game.config.ConfigModule
import gg.rsmod.game.config.GameConfig
import gg.rsmod.game.module.KotlinModuleLoader
import gg.rsmod.game.plugin.kotlin.KotlinPluginLoader
import gg.rsmod.game.service.GameServiceList
import gg.rsmod.net.channel.ClientChannelInitializer
import gg.rsmod.net.NetworkModule
import gg.rsmod.net.handshake.HandshakeDecoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress

private val logger = InlineLogger()

fun main() {
    val app = Application()
    app.startup()
}

class Application {

    fun startup() {
        val scope = Scopes.SINGLETON

        val moduleLoader = KotlinModuleLoader(scope)
        val moduleScripts = moduleLoader.load()
        val modules = moduleScripts.flatMap { it.modules }

        val injector = Guice.createInjector(
            ConfigModule(),
            CacheModule(scope),
            GameModule(scope),
            NetworkModule(scope),
            *modules.toTypedArray()
        )
        val cache: GameCache = injector.getInstance()
        cache.init()

        val pluginLoader = KotlinPluginLoader(injector)
        val plugins = pluginLoader.load()
        println("Loaded ${plugins.size} plugin(s)")

        val services: GameServiceList = injector.getInstance()
        services.forEach { it.start() }

        bind(injector)
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
        logger.info { "Game listening to connections from port ${config.port}" }
    }
}
